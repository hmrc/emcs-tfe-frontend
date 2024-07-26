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

package controllers.partials

import config.EnrolmentKeys.withActiveEmcsEnrolment
import models.NavigationBannerInfo
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.GetMessageStatisticsService
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import utils.Logging
import views.html.components.navigation_bar

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class NavBarPartialController @Inject()(mcc: MessagesControllerComponents,
                                        navBarPartial: navigation_bar,
                                        messageStatisticsService: GetMessageStatisticsService,
                                        override val authConnector: AuthConnector,
                                       )(implicit val executionContext: ExecutionContext)
  extends FrontendController(mcc) with AuthorisedFunctions with Logging {

  def navBar(ern: String): Action[AnyContent] = Action.async { implicit request =>
    
    implicit val hc = HeaderCarrierConverter.fromRequest(request)

    authorised(withActiveEmcsEnrolment(ern)) {
      messageStatisticsService.getMessageStatistics(ern).map { messageStatistics =>
        Ok(navBarPartial(NavigationBannerInfo(ern, messageStatistics.map(_.countOfNewMessages), None)))
      }
    }.recover { _ =>
        logger.warn("[navBar] Authorisation error occurred, returning NoContent for NavBar")
        NoContent
    }
  }
}
