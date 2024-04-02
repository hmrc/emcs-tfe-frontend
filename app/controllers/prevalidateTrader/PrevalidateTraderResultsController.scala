/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers.prevalidateTrader

import config.ErrorHandler
import connectors.referenceData.GetExciseProductCodesConnector
import controllers.BaseNavigationController
import controllers.predicates._
import models.requests.UserAnswersRequest
import models.response.emcsTfe.prevalidateTrader.ExciseTraderResponse
import models.{Index, NormalMode}
import navigation.PrevalidateTraderNavigator
import pages.prevalidateTrader.{PrevalidateAddedProductCodesPage, PrevalidateConsigneeTraderIdentificationPage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import queries.{PrevalidateTraderAddedValues, PrevalidateTraderEPCCount}
import services.{PrevalidateTraderService, PrevalidateTraderUserAnswersService}
import viewmodels.helpers.PrevalidateTraderResultsHelper
import views.html.prevalidateTrader.PrevalidateTraderResultsView

import javax.inject.Inject
import scala.concurrent.Future


class PrevalidateTraderResultsController @Inject()(
                                                    override val messagesApi: MessagesApi,
                                                    override val userAnswersService: PrevalidateTraderUserAnswersService,
                                                    override val betaAllowList: BetaAllowListAction,
                                                    override val navigator: PrevalidateTraderNavigator,
                                                    override val auth: AuthAction,
                                                    override val getData: DataRetrievalAction,
                                                    val prevalidateTraderService: PrevalidateTraderService,
                                                    val requireData: PrevalidateTraderDataRetrievalAction,
                                                    val controllerComponents: MessagesControllerComponents,
                                                    getExciseProductCodesConnector: GetExciseProductCodesConnector,
                                                    errorHandler: ErrorHandler,
                                                    view: PrevalidateTraderResultsView
                                                  ) extends BaseNavigationController with AuthActionHelper {
  def onPageLoad(ern: String): Action[AnyContent] =
    (authorisedWithData(ern) andThen requireData).async { implicit request =>
      withAllValidRequestData {

        val addItemCall = onMax(
          routes.PrevalidateAddToListController.onPageLoad(request.ern),
          routes.PrevalidateExciseProductCodeController.onPageLoad(request.ern, Index(request.userAnswers.get(PrevalidateTraderEPCCount).getOrElse(0)), NormalMode)
        )

        val enteredEPCs: Seq[String] = request.userAnswers.get(PrevalidateTraderAddedValues).get
        val ernToCheck: String = request.userAnswers.get(PrevalidateConsigneeTraderIdentificationPage).get

        getExciseProductCodesConnector.getExciseProductCodes().flatMap(_.fold(
          _ => Future(InternalServerError(errorHandler.internalServerErrorTemplate)),
          epcs => {
            prevalidateTraderService.prevalidate(ern, ernToCheck, enteredEPCs).map { prevalidateTraderResult =>
              //Spec says that whilst the "exciseTraderResponse" is an array, it has a fixed size of 1, hence headOption (option just in case)
              val firstPrevalidateEntry: Option[ExciseTraderResponse] = prevalidateTraderResult.exciseTraderValidationResponse.exciseTraderResponse.headOption
              val validTraderErn: Option[String] = Option.when(firstPrevalidateEntry.exists(_.validTrader))(firstPrevalidateEntry.map(_.exciseRegistrationNumber)).flatten
              val ineligibleEPCs: Seq[String] = firstPrevalidateEntry.flatMap(
                _.validateProductAuthorisationResponse.flatMap(
                  _.productError.map(
                    _.map(_.exciseProductCode)
                  )
                )
              ).getOrElse(Seq.empty)

              val eligibleEPCs = enteredEPCs.diff(ineligibleEPCs)
              Ok(view(
                ernOpt = validTraderErn,
                addCodeCall = addItemCall,
                approved = PrevalidateTraderResultsHelper.parseExciseProductCodeFromStringToModel(eligibleEPCs, epcs),
                notApproved = PrevalidateTraderResultsHelper.parseExciseProductCodeFromStringToModel(ineligibleEPCs, epcs)
              ))
            }
          }
        ))
      }
    }

  private def onMax[T](maxF: => T, notMaxF: => T)(implicit request: UserAnswersRequest[_]): T =
    request.userAnswers.get(PrevalidateTraderEPCCount) match {
      case Some(value) if value >= PrevalidateAddedProductCodesPage.MAX => maxF
      case _ => notMaxF
    }

  private def withAllValidRequestData(f: => Future[Result])(implicit request: UserAnswersRequest[_]): Future[Result] =
    (request.userAnswers.get(PrevalidateTraderEPCCount), request.userAnswers.get(PrevalidateConsigneeTraderIdentificationPage)) match {
      case (Some(epcCount), Some(_)) if epcCount > 0 => f
      case (None | Some(0), Some(_)) => Future.successful(Redirect(routes.PrevalidateExciseProductCodeController.onPageLoad(request.ern, Index(0), NormalMode)))
      case _ => Future.successful(Redirect(routes.PrevalidateConsigneeTraderIdentificationController.onPageLoad(request.ern)))
    }
}
