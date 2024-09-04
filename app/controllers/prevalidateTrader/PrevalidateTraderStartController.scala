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

import controllers.predicates.{AuthAction, AuthActionHelper, DataRetrievalAction}
import models.UserAnswers
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.PrevalidateTraderUserAnswersService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.prevalidateTrader.PrevalidateTraderStartView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PrevalidateTraderStartController @Inject()(mcc: MessagesControllerComponents,
                                                 val auth: AuthAction,
                                                 val getData: DataRetrievalAction,
                                                 val userAnswersService: PrevalidateTraderUserAnswersService,
                                                 view: PrevalidateTraderStartView
                                                )(implicit val executionContext: ExecutionContext)
  extends FrontendController(mcc) with AuthActionHelper with I18nSupport {

  def onPageLoad(ern: String): Action[AnyContent] =
    authorisedDataRequest(ern) { implicit request =>
      Ok(view(routes.PrevalidateTraderStartController.onSubmit(ern)))
    }

  def onSubmit(ern: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern) { _ =>
      userAnswersService.get(ern).flatMap {
        case Some(_) => Future.successful(nextPage(ern))
        case None =>
          userAnswersService.set(UserAnswers(ern)).map { _ =>
            nextPage(ern)
          }
      }
    }

  private def nextPage(ern: String): Result = Redirect(routes.PrevalidateConsigneeTraderIdentificationController.onPageLoad(ern))


}