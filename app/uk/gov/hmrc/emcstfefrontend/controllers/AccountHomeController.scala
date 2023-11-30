/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.emcstfefrontend.controllers

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.emcstfefrontend.config.{AppConfig, ErrorHandler}
import uk.gov.hmrc.emcstfefrontend.connectors.emcsTfe.{GetMessageStatisticsConnector, GetMovementConnector}
import uk.gov.hmrc.emcstfefrontend.controllers.predicates.AuthAction
import uk.gov.hmrc.emcstfefrontend.models.common.RoleType
import uk.gov.hmrc.emcstfefrontend.views.html.AccountHomePage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class AccountHomeController @Inject()(mcc: MessagesControllerComponents,
                                      accountHomePage: AccountHomePage,
                                      errorHandler: ErrorHandler,
                                      authAction: AuthAction,
                                      messageStatisticsConnector: GetMessageStatisticsConnector,
                                      appConfig: AppConfig
                                     )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) {

  def viewAccountHome(exciseRegistrationNumber: String): Action[AnyContent] =
    authAction(exciseRegistrationNumber).async { implicit request =>
      messageStatisticsConnector.getMessageStatistics(exciseRegistrationNumber).map {
        case Right(messageStatistics) => Ok(accountHomePage(
          exciseRegistrationNumber,
          RoleType.fromExciseRegistrationNumber(exciseRegistrationNumber),
          "testBusinessName", //TODO - update with real business name when complete
          messageStatistics,
          appConfig.europaCheckLink,
          appConfig.emcsTfeCreateMovementUrl(exciseRegistrationNumber)
        ))
        case Left(_) => InternalServerError(errorHandler.standardErrorTemplate())
      }
    }
}