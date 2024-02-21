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

import controllers.predicates.{AuthAction, AuthActionHelper, BetaAllowListAction, DataRetrievalAction}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{DeleteMessageService, GetMessagesService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.messages.DeleteMessage

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class DeleteMessageController @Inject()(mcc: MessagesControllerComponents,
                                        val auth: AuthAction,
                                        val getData: DataRetrievalAction,
                                        val betaAllowList: BetaAllowListAction,
                                        val getMessagesService: GetMessagesService,
                                        val deleteMessageService: DeleteMessageService,
                                        val view: DeleteMessage
                                       )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) with AuthActionHelper with I18nSupport {


  def onPageLoad(exciseRegistrationNumber: String, uniqueMessageIdentifier: Long): Action[AnyContent] = {
    authorisedDataRequestAsync(exciseRegistrationNumber) { implicit request =>

      /*


           TODO show the delete message page
          */

      getMessagesService.getMessage(exciseRegistrationNumber, uniqueMessageIdentifier).map {
        case Some(message) =>
          Ok(view(message))
        case None => ???
      }



    }
  }

  def onSubmit(exciseRegistrationNumber: String, uniqueMessageIdentifier: Long): Action[AnyContent] = {

    authorisedDataRequestAsync(exciseRegistrationNumber) { implicit request =>
      /*
        TODO go to message inbox, which will now show the message removed, and a message saying Message Deleted
       */
      ???
    }

    ???
  }


}
