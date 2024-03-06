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

import config.{AppConfig, ErrorHandler}
import config.ErrorHandler
import config.SessionKeys.FROM_PAGE
import controllers.predicates.{AuthAction, AuthActionHelper, BetaAllowListAction, DataRetrievalAction}
import models.messages.MessagesSearchOptions
import pages.ViewMessagePage
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{DraftMovementService, DeleteMessageService, GetMessagesService, GetMovementService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.Logging
import views.html.messages.ViewMessage

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ViewMessageController @Inject()(mcc: MessagesControllerComponents,
                                      val auth: AuthAction,
                                      val getData: DataRetrievalAction,
                                      val betaAllowList: BetaAllowListAction,
                                      getMessagesService: GetMessagesService,
                                      getMovementService: GetMovementService,
                                      draftMovementService: DraftMovementService,
                                      deleteMessageService: DeleteMessageService,
                                      val view: ViewMessage,
                                      errorHandler: ErrorHandler,
                                      appConfig: AppConfig
                                     )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) with AuthActionHelper with I18nSupport with Logging {

  private val messagesThatNeedMovement = Seq("IE871")

  def onPageLoad(ern: String, uniqueMessageIdentifier: Long): Action[AnyContent] = {
    authorisedDataRequestAsync(ern) { implicit request =>

      val sessionWithFromPageSet = request.session + (FROM_PAGE -> ViewMessagePage.toString)

      getMessagesService.getMessage(ern, uniqueMessageIdentifier).flatMap {
        case Some(msg) if messagesThatNeedMovement.contains(msg.message.messageType) && msg.message.arc.isDefined =>
          getMovementService.getRawMovement(ern, msg.message.arc.get).map { movement =>
            Ok(view(msg, Some(movement))).withSession(sessionWithFromPageSet)
          }
        case Some(msg) =>
          Future.successful(
            Ok(view(msg, None)).withSession(sessionWithFromPageSet)
          )
        case _ =>
          Future.successful(
            Redirect(routes.ViewAllMessagesController.onPageLoad(request.ern, MessagesSearchOptions()))
          )
      }
    }
  }

  def removeMessageAndRedirectToDraftMovement(ern: String, uniqueMessageIdentifier: Long): Action[AnyContent] =
    authorisedDataRequestAsync(ern) { implicit request =>
      getMessagesService.getMessage(ern, uniqueMessageIdentifier).flatMap {
        case Some(msg) =>
          msg.errorMessage match {
            case Some(errorMessageResponse) if errorMessageResponse.relatedMessageType.contains("IE815") =>
              deleteMessageService.deleteMessage(ern, uniqueMessageIdentifier).flatMap(response => {
                if (response.recordsAffected == 1) {
                  draftMovementService.putErrorMessagesAndMarkMovementAsDraft(ern, errorMessageResponse).map {
                    case Some(draftId) => Redirect(appConfig.emcsTfeCreateMovementTaskListUrl(ern, draftId))
                    case None => InternalServerError(errorHandler.internalServerErrorTemplate)
                  }
                } else {
                  Future(InternalServerError(errorHandler.internalServerErrorTemplate(request)))
                }
              })
            case _ =>
              logger.warn(s"[removeMessageAndRedirectToDraftMovement] - Message type was not IE815 or LRN did not exist for ERN: $ern and message ID: $uniqueMessageIdentifier - showing not found page")
              Future(NotFound(errorHandler.notFoundTemplate))
          }
        case _ => Future(NotFound(errorHandler.notFoundTemplate))
      }
    }
}
