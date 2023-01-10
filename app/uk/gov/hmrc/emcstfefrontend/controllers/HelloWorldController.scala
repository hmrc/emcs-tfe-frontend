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

import javax.inject.{Inject, Singleton}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.emcstfefrontend.services.HelloWorldService
import uk.gov.hmrc.emcstfefrontend.views.html.{ErrorTemplate, HelloWorldPage}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.ExecutionContext

@Singleton
class HelloWorldController @Inject()(
                                      mcc: MessagesControllerComponents,
                                      service: HelloWorldService,
                                      helloWorldPage: HelloWorldPage,
                                      errorPage: ErrorTemplate,
                                      implicit val executionContext: ExecutionContext)
  extends FrontendController(mcc) {

  def helloWorld(): Action[AnyContent] = Action.async { implicit request =>
    val response = service.getMessage map {
      case (referenceDataResponse, emcsTfeResponse) => Ok(helloWorldPage(referenceDataResponse, emcsTfeResponse))
    }

    response.leftMap(value => InternalServerError(errorPage("Something went wrong!", "Oh no!", value))).merge
  }

}
