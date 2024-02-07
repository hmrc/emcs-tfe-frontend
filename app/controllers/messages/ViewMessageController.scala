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
import models.messages.MessagesSearchOptions
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.GetMessagesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.messages.ViewMessage

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ViewMessageController @Inject()(mcc: MessagesControllerComponents,
                                      val auth: AuthAction,
                                      val getData: DataRetrievalAction,
                                      val betaAllowList: BetaAllowListAction,
                                      getMessagesService: GetMessagesService,
                                      val view: ViewMessage
                                     )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) with AuthActionHelper with I18nSupport {

  def onPageLoad(ern: String, uniqueMessageIdentifier: Long): Action[AnyContent] = {
    authorisedWithData(ern).async { implicit request =>

      getMessagesService.getMessage(ern, uniqueMessageIdentifier).flatMap {
        case Some(message) =>
          Future.successful(
            Ok(view(message))
          )
        case _ =>
          Future.successful(
            Redirect(routes.ViewAllMessagesController.onPageLoad(request.ern, MessagesSearchOptions()))
          )
      }

    }
  }

}
