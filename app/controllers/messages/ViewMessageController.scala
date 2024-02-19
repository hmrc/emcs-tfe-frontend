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
import services.{GetMessagesService, GetMovementService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.messages.ViewMessage

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ViewMessageController @Inject()(mcc: MessagesControllerComponents,
                                      val auth: AuthAction,
                                      val getData: DataRetrievalAction,
                                      val betaAllowList: BetaAllowListAction,
                                      getMessagesService: GetMessagesService,
                                      getMovementService: GetMovementService,
                                      val view: ViewMessage
                                     )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) with AuthActionHelper with I18nSupport {

  private val messagesThatNeedMovement = Seq("IE871")

  def onPageLoad(ern: String, uniqueMessageIdentifier: Long): Action[AnyContent] = {
    authorisedDataRequestAsync(ern) { implicit request =>

      getMessagesService.getMessage(ern, uniqueMessageIdentifier).flatMap {
        case Some(msg) if messagesThatNeedMovement.contains(msg.message.messageType) && msg.message.arc.isDefined =>
          getMovementService.getRawMovement(ern, msg.message.arc.get).map { movement =>
            Ok(view(msg, Some(movement)))
          }
        case Some(msg) =>
          Future.successful(
            Ok(view(msg, None))
          )
        case _ =>
          Future.successful(
            Redirect(routes.ViewAllMessagesController.onPageLoad(request.ern, MessagesSearchOptions()))
          )
      }
    }
  }

}
