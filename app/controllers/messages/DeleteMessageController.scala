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

package controllers.messages

import config.ErrorHandler
import config.SessionKeys.{DELETED_MESSAGE_DESCRIPTION_KEY, FROM_PAGE}
import controllers.messages.routes.{ViewAllMessagesController, ViewMessageController}
import controllers.predicates.{AuthAction, AuthActionHelper, BetaAllowListAction, DataRetrievalAction}
import forms.DeleteMessageFormProvider
import models.messages.{MessageCache, MessagesSearchOptions}
import models.requests.DataRequest
import pages.{Page, ViewAllMessagesPage, ViewMessagePage}
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.{DeleteMessageService, GetMessagesService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.helpers.messages.{DeleteMessageHelper, MessagesHelper}
import views.html.messages.DeleteMessage

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DeleteMessageController @Inject()(mcc: MessagesControllerComponents,
                                        val auth: AuthAction,
                                        val getData: DataRetrievalAction,
                                        val betaAllowList: BetaAllowListAction,
                                        val getMessagesService: GetMessagesService,
                                        val deleteMessageService: DeleteMessageService,
                                        formProvider: DeleteMessageFormProvider,
                                        val view: DeleteMessage,
                                        val deleteMessageHelper: DeleteMessageHelper,
                                        val messagesHelper: MessagesHelper,
                                        errorHandler: ErrorHandler)
                                       (implicit val executionContext: ExecutionContext)
  extends FrontendController(mcc) with AuthActionHelper with I18nSupport {

  def onPageLoad(exciseRegistrationNumber: String, uniqueMessageIdentifier: Long): Action[AnyContent] = {
    authorisedDataRequestAsync(exciseRegistrationNumber) { implicit request =>
      renderView(
        exciseRegistrationNumber,
        uniqueMessageIdentifier,
        formProvider(),
        pageFromSession(request.session.get(FROM_PAGE))
      )
    }
  }

  def onSubmit(exciseRegistrationNumber: String, uniqueMessageIdentifier: Long): Action[AnyContent] =
    authorisedDataRequestAsync(exciseRegistrationNumber) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors => {
          renderView(exciseRegistrationNumber, uniqueMessageIdentifier, formWithErrors, pageFromSession(request.session.get(FROM_PAGE)))
        },
        userSelectsDeleteMessage => {
          if (userSelectsDeleteMessage) {
            deleteMessage(exciseRegistrationNumber, uniqueMessageIdentifier)
          } else {
            returnToAllMessagesOrMessagePage(exciseRegistrationNumber, uniqueMessageIdentifier)
          }
        }
      )
    }

  private def deleteMessage(exciseRegistrationNumber: String,
                            uniqueMessageIdentifier: Long)(implicit request: DataRequest[_]): Future[Result] =
    getMessagesService.getMessage(exciseRegistrationNumber, uniqueMessageIdentifier) flatMap {
      case Some(messageCache: MessageCache) =>
        deleteMessageService.deleteMessage(exciseRegistrationNumber, uniqueMessageIdentifier) map {
          case deleteMessageResponse if deleteMessageResponse.recordsAffected == 1 =>
            // redirect to all messages page to show success banner, add the deleted message title to session for the banner title
            Redirect(ViewAllMessagesController.onPageLoad(exciseRegistrationNumber, MessagesSearchOptions()).url)
              .flashing(DELETED_MESSAGE_DESCRIPTION_KEY -> messagesHelper.messageDescriptionKey(messageCache.message))
          case _ =>
            InternalServerError(errorHandler.internalServerErrorTemplate(request))
        }
      case _ =>
        Future(InternalServerError(errorHandler.internalServerErrorTemplate(request)))
    }

  def renderView(exciseRegistrationNumber: String,
                 uniqueMessageIdentifier: Long,
                 form: Form[_],
                 fromPage: Page)(implicit dr: DataRequest[_], hc: HeaderCarrier): Future[Result] =
    getMessagesService.getMessage(exciseRegistrationNumber, uniqueMessageIdentifier).flatMap {
      case Some(messageCache) =>
        Future(
          Ok(view(
            messageCache.message,
            form = form,
            returnToMessagesUrl = ViewAllMessagesController.onPageLoad(exciseRegistrationNumber, MessagesSearchOptions()).url,
            fromPage
          ))
        )
      case None =>
        Future(
          amendSession(Redirect(ViewAllMessagesController.onPageLoad(exciseRegistrationNumber, MessagesSearchOptions()).url))
        )
    }

  // When the user selects No, return to ViewAllMessage or ViewMessage page, depending on how they got here
  private def returnToAllMessagesOrMessagePage(exciseRegistrationNumber: String,
                                               uniqueMessageIdentifier: Long)(implicit dr: DataRequest[_]): Future[Result] =
    Future(
      pageFromSession(dr.session.get(FROM_PAGE)) match {
        case ViewAllMessagesPage =>
          amendSession(Redirect(ViewAllMessagesController.onPageLoad(exciseRegistrationNumber, MessagesSearchOptions()).url))
        case _ =>
          amendSession(Redirect(ViewMessageController.onPageLoad(exciseRegistrationNumber, uniqueMessageIdentifier)))
      }
    )

  private def pageFromSession(pageFromSession: Option[String]): Page = {
    pageFromSession match {
      case Some(ViewAllMessagesPage.toString) => ViewAllMessagesPage
      case _ => ViewMessagePage
    }
  }

  // remove the FROM_PAGE variable when navigating away from this page, as it's no longer needed
  private def amendSession(call: Result)(implicit request: RequestHeader): Result = call.removingFromSession(FROM_PAGE)

}
