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
import config.SessionKeys.{DELETED_MESSAGE_TITLE, FROM_PAGE}
import controllers.predicates.{AuthAction, AuthActionHelper, BetaAllowListAction, DataRetrievalAction}
import models.messages.MessagesSearchOptions.DEFAULT_MAX_ROWS
import models.messages.{MessagesSearchOptions, MessagesSortingSelectOption}
import models.requests.DataRequest
import pages.ViewAllMessagesPage
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.GetMessagesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.messages.ViewAllMessages

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ViewAllMessagesController @Inject()(mcc: MessagesControllerComponents,
                                          val auth: AuthAction,
                                          val getData: DataRetrievalAction,
                                          val betaAllowList: BetaAllowListAction,
                                          getMessagesService: GetMessagesService,
                                          val view: ViewAllMessages,
                                          errorHandler: ErrorHandler)
                                         (implicit val executionContext: ExecutionContext)
  extends FrontendController(mcc) with AuthActionHelper with I18nSupport {

  def onPageLoad(ern: String, search: MessagesSearchOptions): Action[AnyContent] = {
    authorisedWithData(ern).async { implicit request =>
      val session = amendSession(request.session)

      if (search.index <= 0) {
        Future.successful(
          Redirect(routes.ViewAllMessagesController.onPageLoad(ern, MessagesSearchOptions(index = 1))).withSession(session)
        )
      } else {
        renderView(Ok, ern, search, session, request.session.get(DELETED_MESSAGE_TITLE))
      }
    }
  }

  private def renderView(status: Status,
                         ern: String,
                         search: MessagesSearchOptions,
                         session: Session,
                         maybeDeletedMessageTitle: Option[String])(implicit request: DataRequest[_]): Future[Result] = {

    getMessagesService.getMessages(ern, Some(search)).map { allMessages =>

      val totalNumberOfPages: Int = calculatePageCount(
        allMessages.totalNumberOfMessagesAvailable.toInt,
        DEFAULT_MAX_ROWS
      )

      if (search.index > totalNumberOfPages) {
        Redirect(routes.ViewAllMessagesController.onPageLoad(ern, MessagesSearchOptions(index = 1))).withSession(session)
      } else {
        status(
          view(
            sortSelectItems = MessagesSortingSelectOption.constructSelectItems(Some(search.sortBy.code)),
            allMessages = allMessages.messages,
            totalNumberOfPages = totalNumberOfPages,
            searchOptions = search,
            maybeDeletedPageTitle = maybeDeletedMessageTitle
          )
        ).withSession(session)
      }
    } recover {
      case _ =>
        InternalServerError(errorHandler.standardErrorTemplate())
    }
  }

  private def calculatePageCount(totalNumberOfMessagesAvailable: Int, maximumRowsPerPage: Int): Int = {
    if (totalNumberOfMessagesAvailable == 0) {
      1
    } else if (totalNumberOfMessagesAvailable % maximumRowsPerPage != 0) {
      (totalNumberOfMessagesAvailable / maximumRowsPerPage) + 1
    } else {
      totalNumberOfMessagesAvailable / maximumRowsPerPage
    }
  }

  // Set the FROM_PAGE session variable used by the delete message controller.
  // If set, unset the DELETED_MESSAGE_TITLE variable, so the 'Success Message Deleted' banner does not show when we visit
  // this page, if the user has not deleted a message.
  private def amendSession(previousSession: Session): Session = {
    val session = previousSession + (FROM_PAGE -> ViewAllMessagesPage.toString)

    if (previousSession.get(DELETED_MESSAGE_TITLE).isDefined) {
      session - DELETED_MESSAGE_TITLE
    } else {
      session
    }
  }
}
