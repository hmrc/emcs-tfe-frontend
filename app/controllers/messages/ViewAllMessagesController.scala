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
import controllers.predicates.{AuthAction, AuthActionHelper, DataRetrievalAction}
import models.messages.{MessagesSearchOptions, MessagesSortingSelectOption}
import models.requests.DataRequest
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.GetMessagesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.messages.ViewAllMessages

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ViewAllMessagesController @Inject()(mcc: MessagesControllerComponents,
                                          val auth: AuthAction,
                                          val getData: DataRetrievalAction,
                                          getMessagesService: GetMessagesService,
                                          val view: ViewAllMessages,
                                          errorHandler: ErrorHandler
                                         )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) with AuthActionHelper with I18nSupport {


  def onPageLoad(ern: String, search: MessagesSearchOptions): Action[AnyContent] =
    authorisedWithData(ern).async { implicit request =>
      renderView(Ok, ern, search)
    }

  private def renderView(status: Status, ern: String, search: MessagesSearchOptions)(implicit request: DataRequest[_]): Future[Result] =
    getMessagesService.getMessages(ern, Some(search)).map { allMessages =>
      status(
        view(
          sortSelectItems = MessagesSortingSelectOption.constructSelectItems(None),
          allMessages = allMessages.messagesData.messages
        )
      )
    } recover {
      case _ =>
        InternalServerError(errorHandler.standardErrorTemplate())
    }

}
