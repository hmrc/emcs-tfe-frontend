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

import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, convertToStringShouldWrapper}
import play.api.http.Status
import play.api.i18n.MessagesApi
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.emcstfefrontend.base.SpecBase
import uk.gov.hmrc.emcstfefrontend.config.ErrorHandler
import uk.gov.hmrc.emcstfefrontend.controllers.predicates.{FakeAuthAction, FakeDataRequiredAction, FakeDataRetrievalAction}
import uk.gov.hmrc.emcstfefrontend.fixtures.MovementListFixtures
import uk.gov.hmrc.emcstfefrontend.fixtures.messages.EN
import uk.gov.hmrc.emcstfefrontend.mocks.connectors.MockEmcsTfeConnector
import uk.gov.hmrc.emcstfefrontend.models.auth.UserRequest
import uk.gov.hmrc.emcstfefrontend.models.requests.DataRequest
import uk.gov.hmrc.emcstfefrontend.models.response.UnexpectedDownstreamResponseError
import uk.gov.hmrc.emcstfefrontend.views.html.ViewMovementListPage
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class ViewMovementListControllerSpec extends SpecBase with MovementListFixtures with FakeAuthAction {

  trait Test extends MockEmcsTfeConnector {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
    implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")
    implicit val messages = app.injector.instanceOf[MessagesApi].preferred(Seq(EN.lang))

    val view = app.injector.instanceOf[ViewMovementListPage]
    val errorHandler = app.injector.instanceOf[ErrorHandler]

    val controller: ViewMovementListController = new ViewMovementListController(
      app.injector.instanceOf[MessagesControllerComponents],
      FakeSuccessAuthAction,
      new FakeDataRetrievalAction(Some(testMinTraderKnownFacts)),
      new FakeDataRequiredAction(testMinTraderKnownFacts),
      mockGetMovementListConnector,
      view,
      errorHandler
    )
  }

  "GET /" when {

    "connector call is successful" should {

      "return 200" in new Test {

        MockEmcsTfeConnector
          .getMovementList(testErn)
          .returns(Future.successful(Right(getMovementListResponse)))

        val result: Future[Result] = controller.viewMovementList(testErn)(fakeRequest)

        val dataRequest = DataRequest(
          UserRequest(fakeRequest, testErn, testInternalId, testCredId, hasMultipleErns = false),
          testMinTraderKnownFacts
        )

        status(result) shouldBe Status.OK
        Html(contentAsString(result)) shouldBe view(testErn, getMovementListResponse)(dataRequest, messages)
      }
    }

    "connector call is unsuccessful" should {

      "return 500" in new Test {

        MockEmcsTfeConnector
          .getMovementList(testErn)
          .returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result: Future[Result] = controller.viewMovementList(testErn)(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        Html(contentAsString(result)) shouldBe errorHandler.internalServerErrorTemplate
      }
    }
  }
}
