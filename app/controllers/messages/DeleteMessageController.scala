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

import config.SessionKeys.{DELETED_MESSAGE_TITLE, FROM_PAGE}
import controllers.messages.routes.{ViewAllMessagesController, ViewMessageController}
import controllers.predicates.{AuthAction, AuthActionHelper, BetaAllowListAction, DataRetrievalAction}
import forms.DeleteMessageFormProvider
import models.messages.MessagesSearchOptions
import models.requests.DataRequest
import pages.{Page, ViewAllMessagesPage, ViewMessagePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc._
import services.{DeleteMessageService, GetMessagesService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.messages.DeleteMessage
import viewmodels.helpers.messages.DeleteMessageHelper

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
                                        val messages: Messages)
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

  def onSubmit(exciseRegistrationNumber: String, uniqueMessageIdentifier: Long): Action[AnyContent] = {

    authorisedDataRequestAsync(exciseRegistrationNumber) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors => {
          renderView(exciseRegistrationNumber, uniqueMessageIdentifier, formWithErrors, pageFromSession(request.session.get(FROM_PAGE)))
        },
        deleteMessage => {
          if (deleteMessage) {
            deleteMessageService.deleteMessage(exciseRegistrationNumber, uniqueMessageIdentifier)
              .map(deleteMessageResponse => {

                if (deleteMessageResponse.recordsAffected == 1) {
                  /*
                    TODO
                     - set a session key with the message title
                     - redirect to the ViewAllMessage controller with the message deleted success thingy

                   */


                  Redirect(ViewAllMessagesController.onPageLoad(exciseRegistrationNumber, MessagesSearchOptions()).url)

                } else {

                  // TODO error deleting message ???
                }

                ???
              }) // TODO recoverWith ???

          } else {

            pageFromSession(request.session.get(FROM_PAGE)) match {
              case ViewAllMessagesPage =>
                Future(
                  removeFromPageSessionValue(Redirect(ViewAllMessagesController.onPageLoad(exciseRegistrationNumber, MessagesSearchOptions()).url))
                )
              case _ =>
                Future(
                  removeFromPageSessionValue(Redirect(ViewMessageController.onPageLoad(exciseRegistrationNumber, uniqueMessageIdentifier)))
                )
            }
          }
        }
      )

    }
  }

  def renderView(exciseRegistrationNumber: String,
                 uniqueMessageIdentifier: Long,
                 form: Form[_],
                 fromPage: Page)(implicit dr: DataRequest[_], hc: HeaderCarrier): Future[Result] = {

    getMessagesService.getMessage(exciseRegistrationNumber, uniqueMessageIdentifier).flatMap {
      case Some(messageCache) =>
        Future(
          removeFromPageSessionValue(
            Ok(view(
              messageCache.message,
              form = form,
              returnToMessagesUrl = ViewAllMessagesController.onPageLoad(exciseRegistrationNumber, MessagesSearchOptions()).url,
              fromPage
            ))
          ).addingToSession(DELETED_MESSAGE_TITLE -> messages(deleteMessageHelper.getMessageTitleKey(messageCache.message)))
        )
      case None =>
        Future(
          removeFromPageSessionValue(Redirect(ViewAllMessagesController.onPageLoad(exciseRegistrationNumber, MessagesSearchOptions()).url))
        )
    }
  }

  private def pageFromSession(pageFromSession: Option[String]): Page = {
    pageFromSession match {
      case Some(ViewMessagePage.toString) => ViewMessagePage
      case Some(ViewAllMessagesPage.toString) => ViewAllMessagesPage
      case _ => ViewAllMessagesPage
    }
  }

  private def removeFromPageSessionValue(call: Result)(implicit request: RequestHeader): Result = call.removingFromSession(FROM_PAGE)

}