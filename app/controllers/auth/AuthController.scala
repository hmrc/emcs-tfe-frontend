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

package controllers.auth

import config.AppConfig
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject


class AuthController @Inject()(val controllerComponents: MessagesControllerComponents,
                               config: AppConfig
                              ) extends FrontendBaseController with I18nSupport {

  def signOut(becauseOfTimeout: Boolean = false): Action[AnyContent] = Action {
    val continueUrl = if(becauseOfTimeout) {
      config.host + routes.TimeoutController.onPageLoad().url
    } else {
      config.feedbackFrontendSurveyUrl
    }
    Redirect(config.signOutUrl, Map("continue" -> Seq(continueUrl)))
  }
}
