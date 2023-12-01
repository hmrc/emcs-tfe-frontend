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
import uk.gov.hmrc.emcstfefrontend.config.EnrolmentKeys
import uk.gov.hmrc.emcstfefrontend.controllers.predicates.SelectExciseNumberAuthAction
import uk.gov.hmrc.emcstfefrontend.utils.Logging
import uk.gov.hmrc.emcstfefrontend.views.html.ExciseNumbersPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class IndexController @Inject()(mcc: MessagesControllerComponents,
                                view: ExciseNumbersPage,
                                authAction: SelectExciseNumberAuthAction
                               )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) with Logging {

  def exciseNumber(): Action[AnyContent] = authAction { implicit request =>
    request.exciseEnrolments.flatMap(_.identifiers.find(_.key == EnrolmentKeys.ERN)) match {
      case exciseRegistrationNumbers if exciseRegistrationNumbers.isEmpty =>
        logger.error("[exciseNumber] User had no ERN identifiers against their EMCS enrolment(s)")
        Redirect(errors.routes.UnauthorisedController.unauthorised().url)
      case exciseRegistrationNumbers if exciseRegistrationNumbers.size == 1 =>
        logger.debug("[exciseNumber] User only has one active EMCS enrolment.")
        Redirect(routes.AccountHomeController.viewAccountHome(exciseRegistrationNumbers.head.value))
      case exciseRegistrationNumbers =>
        logger.debug("[exciseNumber] User has multiple active EMCS enrolments.")
        Ok(view(exciseRegistrationNumbers.map(_.value)))
    }
  }
}
