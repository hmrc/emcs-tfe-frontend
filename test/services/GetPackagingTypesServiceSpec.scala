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
import mocks.connectors.MockGetItemPackagingTypesConnector
import models.response.emcsTfe.MovementItem
import models.response.referenceData.ItemPackaging
import models.response.{PackagingTypesException, UnexpectedDownstreamResponseError}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class GetPackagingTypesServiceSpec extends SpecBase with MockGetItemPackagingTypesConnector with GetMovementResponseFixtures {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global

  lazy val testService = new GetPackagingTypesService(mockGetItemPackagingTypesConnector)

  val movementItems: Seq[MovementItem] = Seq(item1WithWineOperations, item2WithWineOperations)

  ".getPackagingTypes" must {

    "return Success response" when {

      "Connector returns success from downstream" in {

        MockGetItemPackagingTypesConnector.getItemPackagingTypes.returns(Future.successful(Right(
          testItemPackagingTypes
        )))

        testService.getMovementItemsWithPackagingTypes(movementItems)(hc).futureValue mustBe Seq(
          item1WithWineAndPackaging,
          item2WithWineAndPackaging
        )
      }
    }

    "return Failure response" when {

      "Connector returns failure from downstream" in {

        MockGetItemPackagingTypesConnector.getItemPackagingTypes.returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result = intercept[PackagingTypesException](await(testService.getMovementItemsWithPackagingTypes(movementItems)(hc)))

        result.getMessage must include("Failed to retrieve packaging types from emcs-tfe-reference-data")
      }

      "not all items match something from the Connector" in {
        MockGetItemPackagingTypesConnector.getItemPackagingTypes.returns(Future.successful(Right(
          Seq(ItemPackaging("AE", "Aerosol"))
        )))

        val result = intercept[PackagingTypesException](await(testService.getMovementItemsWithPackagingTypes(movementItems)(hc)))

        result.getMessage must include(s"Failed to match item with packaging type from emcs-tfe-reference-data")
      }
    }
  }

}
