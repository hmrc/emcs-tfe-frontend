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

import controllers.messages.routes.ViewAllMessagesController
import controllers.predicates.{AuthAction, AuthActionHelper, BetaAllowListAction, DataRetrievalAction}
import forms.DeleteMessageFormProvider
import models.messages.MessagesSearchOptions
import pages.{Page, ViewMessagePage}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{DeleteMessageService, GetMessagesService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
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
                                        val view: DeleteMessage
                                       )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) with AuthActionHelper with I18nSupport {


  def onPageLoad(exciseRegistrationNumber: String, uniqueMessageIdentifier: Long, fromPage: Option[Page]): Action[AnyContent] = {
    authorisedDataRequestAsync(exciseRegistrationNumber) { implicit request =>

      val page = fromPage.getOrElse(ViewMessagePage)

      getMessagesService.getMessage(exciseRegistrationNumber, uniqueMessageIdentifier).map {
        case Some(messageCache) =>
          Ok(view(
            messageCache.message,
            form = formProvider(),
            returnToMessagesUrl = ViewAllMessagesController.onPageLoad(exciseRegistrationNumber, MessagesSearchOptions()).url,
            page
          ))
        case None => ??? //TODO
      }
    }
  }

  def onSubmit(exciseRegistrationNumber: String, uniqueMessageIdentifier: Long): Action[AnyContent] = {

    authorisedDataRequestAsync(exciseRegistrationNumber) { implicit request =>
//      getMessagesService.getMessage(exciseRegistrationNumber, uniqueMessageIdentifier).map {
//        case Some(messageCache) =>
//      formProvider().bindFromRequest().fold(
//        formWithErrors => Future.successful(BadRequest(view(messageCache, formWithErrors)))
//      )
      /*
        TODO go to message inbox, which will now show the message removed, and a message saying Message Deleted
       */
      ???
    }

    ???
  }


}
