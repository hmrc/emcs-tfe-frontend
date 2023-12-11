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

package controllers.errors

import base.SpecBase
import config.ErrorHandler
import fixtures.messages.UnauthorisedMessages
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext

class UnauthorisedControllerSpec extends SpecBase {

  trait Test {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
    implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")

    val errorHandler = app.injector.instanceOf[ErrorHandler]

    val controller: UnauthorisedController = new UnauthorisedController(
      app.injector.instanceOf[MessagesControllerComponents],
      errorHandler,
      ec
    )
  }

  "GET /error/unauthorised" when {

    Seq(UnauthorisedMessages.English) foreach { viewMessages =>

      s"being rendered for lang '${viewMessages.lang.code}'" must {

        s"return UNAUTHORIZED ($UNAUTHORIZED) and correct Error Messages" in new Test {

          val result = controller.unauthorised()(fakeRequest)

          status(result) mustBe UNAUTHORIZED
          Html(contentAsString(result)) mustBe errorHandler.standardErrorTemplate(
            pageTitle = viewMessages.title,
            heading = viewMessages.heading,
            message = viewMessages.message
          )
        }
      }
    }
  }
}
