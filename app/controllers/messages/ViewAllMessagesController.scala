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
import controllers.predicates.{AuthAction, AuthActionHelper, DataRetrievalAction}
import models.messages.MessagesSearchOptions.DEFAULT_MAX_ROWS
import models.messages.{MessagesSearchOptions, MessagesSortingSelectOption}
import models.requests.DataRequest
import pages.ViewAllMessagesPage
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.GetMessagesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.Logging
import views.html.messages.ViewAllMessagesView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ViewAllMessagesController @Inject()(mcc: MessagesControllerComponents,
                                          val auth: AuthAction,
                                          val getData: DataRetrievalAction,
                                          getMessagesService: GetMessagesService,
                                          val view: ViewAllMessagesView,
                                          errorHandler: ErrorHandler)
                                         (implicit val executionContext: ExecutionContext)
  extends FrontendController(mcc) with AuthActionHelper with I18nSupport with Logging {

  def onPageLoad(ern: String, search: MessagesSearchOptions): Action[AnyContent] = {
    authorisedDataRequestAsync(ern) { implicit request =>

      if (search.index <= 0) {
        Future.successful(
          Redirect(routes.ViewAllMessagesController.onPageLoad(ern, MessagesSearchOptions(index = 1)))
        )
      } else {
        renderView(Ok, ern, search)
      }
    }
  }

  private def renderView(status: Status,
                         ern: String,
                         search: MessagesSearchOptions)(implicit request: DataRequest[_]): Future[Result] = {

    getMessagesService.getMessages(ern, Some(search)).map { allMessages =>

      // Set the FROM_PAGE session variable used by the delete message controller
      val session = request.session + (FROM_PAGE -> ViewAllMessagesPage.toString)

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
            maybeDeletedMessageDescriptionKey = request.flash.get(DELETED_MESSAGE_DESCRIPTION_KEY)
          )
        ).withSession(session)
      }
    } recoverWith {
      case e =>
        logger.warn(s"[onPageLoad][$ern] Unexpected exception thrown of type ${e.getClass.getSimpleName.stripSuffix("$")}. Message: ${e.getMessage}")
        errorHandler.standardErrorTemplate().map(html => InternalServerError(html))
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

}
