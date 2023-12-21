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

package services

import base.SpecBase
import fixtures.GetMovementResponseFixtures
import mocks.connectors.MockGetWineOperationsConnector
import models.common.WineProduct
import models.requests.WineOperationsRequest
import models.response.emcsTfe.MovementItem
import models.response.referenceData.WineOperationsResponse
import models.response.{UnexpectedDownstreamResponseError, WineOperationsException}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class GetWineOperationsServiceSpec extends SpecBase with MockGetWineOperationsConnector with GetMovementResponseFixtures {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global

  lazy val testService = new GetWineOperationsService(mockGetWineOperationsConnector)

  val request: WineOperationsRequest = WineOperationsRequest(wineOperations = Seq("4", "5"))
  val movementItems: Seq[MovementItem] = Seq(item1)

  val updatedWineProduct: WineProduct = wineProduct.copy(wineOperations = Some(Seq("Reason 4", "Reason 5")))

  ".getWineOperations" should {

    "return Success response" when {

      "Connector returns success from downstream" in {
        MockGetWineOperationsConnector.getWineOperations(request).returns(Future.successful(Right(WineOperationsResponse(data = Map(
          "4" -> "Reason 4",
          "5" -> "Reason 5"
        )))))

        testService.getWineOperations(movementItems)(hc).futureValue mustBe Seq(
          item1.copy(wineProduct = Some(updatedWineProduct)),
        )
      }
    }

    "return Failure response" when {

      "Connector returns failure from downstream" in {

        MockGetWineOperationsConnector.getWineOperations(request).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result = intercept[WineOperationsException](await(testService.getWineOperations(movementItems)(hc)))

        result.getMessage must include("Failed to retrieve wine operations from emcs-tfe-reference-data")
      }

      "not all items match something from the Connector" in {
        MockGetWineOperationsConnector.getWineOperations(request).returns(Future.successful(Right(WineOperationsResponse(data = Map(
          "4" -> "Reason 4"
        )))))

        val result = intercept[WineOperationsException](await(testService.getWineOperations(movementItems)(hc)))

        result.getMessage must include(s"Failed to match item with wine operation from emcs-tfe-reference-data")
      }
    }
  }
}
