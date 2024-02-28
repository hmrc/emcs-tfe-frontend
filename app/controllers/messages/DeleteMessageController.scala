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
import config.SessionKeys.{DELETED_MESSAGE_TITLE, FROM_PAGE, TEMP_DELETE_MESSAGE_TITLE}
import controllers.messages.routes.{ViewAllMessagesController, ViewMessageController}
import controllers.predicates.{AuthAction, AuthActionHelper, BetaAllowListAction, DataRetrievalAction}
import forms.DeleteMessageFormProvider
import models.messages.MessagesSearchOptions
import models.requests.DataRequest
import pages.{Page, ViewAllMessagesPage, ViewMessagePage}
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.{DeleteMessageService, GetMessagesService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.helpers.messages.DeleteMessageHelper
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
            deleteMessageService.deleteMessage(exciseRegistrationNumber, uniqueMessageIdentifier) map {
              case deleteMessageResponse if deleteMessageResponse.recordsAffected == 1 =>
                // redirect to all messages page to show success banner, add the deleted message title to session for the banner title
                Redirect(ViewAllMessagesController.onPageLoad(exciseRegistrationNumber, MessagesSearchOptions()).url)
                  .addingToSession(DELETED_MESSAGE_TITLE -> request.session.get(TEMP_DELETE_MESSAGE_TITLE).getOrElse(""))
                  .removingFromSession(TEMP_DELETE_MESSAGE_TITLE)

              case _ =>
                InternalServerError(errorHandler.standardErrorTemplate())
            }
          } else {
            returnToAllMessagesOrMessagePage(exciseRegistrationNumber, uniqueMessageIdentifier)
          }
        }
      )
    }

  def renderView(exciseRegistrationNumber: String,
                 uniqueMessageIdentifier: Long,
                 form: Form[_],
                 fromPage: Page)(implicit dr: DataRequest[_], hc: HeaderCarrier): Future[Result] =
    getMessagesService.getMessage(exciseRegistrationNumber, uniqueMessageIdentifier).flatMap {
      case Some(messageCache) =>
        Future(
          removeFromPageSessionValue(
            Ok(view(
              messageCache.message,
              form = form,
              returnToMessagesUrl = returnUrl(fromPage, exciseRegistrationNumber, uniqueMessageIdentifier),
              fromPage
            ))
          ).addingToSession(
            // used to avoid another call to the getMessagesService in the onSubmit method, as we can set the message title here
            TEMP_DELETE_MESSAGE_TITLE -> mcc.messagesApi.messages("default")(deleteMessageHelper.getMessageTitleKey(messageCache.message))
          )
        )
      case None => // TODO check this...
        Future(
          removeFromPageSessionValue(Redirect(ViewAllMessagesController.onPageLoad(exciseRegistrationNumber, MessagesSearchOptions()).url))
        )
    }

  // When the user selects No, return to ViewAllMessage or ViewMessage page, depending on how they got here
  private def returnToAllMessagesOrMessagePage(exciseRegistrationNumber: String,
                                               uniqueMessageIdentifier: Long)(implicit dr: DataRequest[_]): Future[Result] =
    Future(
      pageFromSession(dr.session.get(FROM_PAGE)) match {
        case ViewAllMessagesPage =>
          removeFromPageSessionValue(Redirect(ViewAllMessagesController.onPageLoad(exciseRegistrationNumber, MessagesSearchOptions()).url))
        case _ =>
          removeFromPageSessionValue(Redirect(ViewMessageController.onPageLoad(exciseRegistrationNumber, uniqueMessageIdentifier)))
      }
    )

  private def pageFromSession(pageFromSession: Option[String]): Page = {
    pageFromSession match {
      case Some(ViewAllMessagesPage.toString) => ViewAllMessagesPage
      case _ => ViewMessagePage
    }
  }

  private def returnUrl(fromPage: Page,
                        exciseRegistrationNumber: String,
                        uniqueMessageIdentifier: Long): String =
    fromPage match {
      case ViewAllMessagesPage => ViewAllMessagesController.onPageLoad(exciseRegistrationNumber, MessagesSearchOptions()).url
      case _ => ViewMessageController.onPageLoad(exciseRegistrationNumber, uniqueMessageIdentifier).url
    }


  private def removeFromPageSessionValue(call: Result)(implicit request: RequestHeader): Result = call.removingFromSession(FROM_PAGE)

}
