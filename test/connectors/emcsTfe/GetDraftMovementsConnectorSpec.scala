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

package connectors.emcsTfe

import base.SpecBase
import fixtures.GetMovementResponseFixtures
import mocks.connectors.MockHttpClient
import models.draftMovements.GetDraftMovementsSearchOptions
import models.response.JsonValidationError
import play.api.http.{HeaderNames, MimeTypes, Status}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class GetDraftMovementsConnectorSpec extends SpecBase
  with Status with MimeTypes with HeaderNames with MockHttpClient with GetMovementResponseFixtures {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  lazy val connector = new GetDraftMovementsConnector(mockHttpClient, appConfig)

  "getMovement" should {

    "return a successful response" when {

      "with no query params" when {

        "downstream call is successful" in {

          val expectedResult = Right(getMovementResponseModel)

          MockHttpClient
            .get(s"${appConfig.emcsTfeBaseUrl}/user-answers/create-movement/drafts/search/$testErn")
            .returns(Future.successful(Right(getMovementResponseModel)))

          val actualResult = connector.getDraftMovements(
            ern = testErn,
            search = None
          ).futureValue

          actualResult mustBe expectedResult
        }
      }

      "with query params" when {

        "downstream call is successful" in {

          val expectedResult = Right(getMovementResponseModel)

          MockHttpClient
            .get(
              s"${appConfig.emcsTfeBaseUrl}/user-answers/create-movement/drafts/search/$testErn",
              Seq(("search.sortField", "lastUpdated"), ("search.sortOrder", "D"), ("search.startPosition", "10"), ("search.maxRows", "10"))
            ).returns(Future.successful(Right(getMovementResponseModel)))

          val actualResult = connector.getDraftMovements(
            ern = testErn,
            search = Some(GetDraftMovementsSearchOptions(index = 2))
          ).futureValue

          actualResult mustBe expectedResult
        }
      }
    }

    "return an error response" when {

      "when downstream call fails" in {

        val expectedResult = Left(JsonValidationError)

        MockHttpClient
          .get(s"${appConfig.emcsTfeBaseUrl}/user-answers/create-movement/drafts/search/$testErn")
          .returns(Future.successful(Left(JsonValidationError)))

        val actualResult = connector.getDraftMovements(ern = testErn).futureValue

        actualResult mustBe expectedResult
      }
    }
  }
}
