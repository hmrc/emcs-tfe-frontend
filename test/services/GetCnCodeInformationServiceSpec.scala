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
import fixtures.ItemFixtures
import mocks.connectors.MockGetCnCodeInformationConnector
import models.common.UnitOfMeasure.Kilograms
import models.requests.{CnCodeInformationItem, CnCodeInformationRequest}
import models.response.{CnCodeInformationException, UnexpectedDownstreamResponseError}
import models.response.referenceData.{CnCodeInformation, CnCodeInformationResponse}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class GetCnCodeInformationServiceSpec extends SpecBase with ItemFixtures with MockGetCnCodeInformationConnector {

  implicit val hc = HeaderCarrier()
  implicit val ec = ExecutionContext.global

  lazy val testService = new GetCnCodeInformationService(mockGetCnCodeInformationConnector)

  val items = Seq(item1.copy(productCode = "T400", cnCode = "24029000"))
  val request = CnCodeInformationRequest(CnCodeInformationItem(items))

  ".getCnCodeInformationWithMovementItems" must {

    "return Success response" when {

      "Connector returns success from downstream" in {
        MockGetCnCodeInformationConnector.getCnCodeInformation(request).returns(Future.successful(Right(CnCodeInformationResponse(data = Map(
          "24029000" -> CnCodeInformation(
            cnCode = "T400",
            cnCodeDescription = "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
            exciseProductCode = "24029000",
            exciseProductCodeDescription = "Fine-cut tobacco for the rolling of cigarettes",
            unitOfMeasure = Kilograms
          )
        )))))

        testService.getCnCodeInformation(items)(hc).futureValue mustBe Seq((items.head, CnCodeInformation(
          cnCode = "T400",
          cnCodeDescription = "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
          exciseProductCode = "24029000",
          exciseProductCodeDescription = "Fine-cut tobacco for the rolling of cigarettes",
          unitOfMeasure = Kilograms
        )))
      }
    }

    "return Failure response" when {

      "Connector returns failure from downstream" in {

        MockGetCnCodeInformationConnector.getCnCodeInformation(request).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result = intercept[CnCodeInformationException](await(testService.getCnCodeInformation(items)(hc)))

        result.getMessage must include(s"Failed to retrieve CN Code information")
      }

      "not all items match something from the Connector" in {

        val items = Seq(
          item1.copy(productCode = "T400", cnCode = "24029000"),
          item1.copy(productCode = "T401", cnCode = "24029001"),
          item1.copy(productCode = "T402", cnCode = "24029002")
        )
        val request = CnCodeInformationRequest(CnCodeInformationItem(items))

        MockGetCnCodeInformationConnector.getCnCodeInformation(request).returns(Future.successful(Right(CnCodeInformationResponse(data = Map(
          "24029000" -> CnCodeInformation(
            cnCode = "T400",
            cnCodeDescription = "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
            exciseProductCode = "24029000",
            exciseProductCodeDescription = "Fine-cut tobacco for the rolling of cigarettes",
            unitOfMeasure = Kilograms
          ),
          "24029001" -> CnCodeInformation(
            cnCode = "T401",
            cnCodeDescription = "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
            exciseProductCode = "24029001",
            exciseProductCodeDescription = "Fine-cut tobacco for the rolling of cigarettes",
            unitOfMeasure = Kilograms
          )
        )))))

        val result = intercept[CnCodeInformationException](await(testService.getCnCodeInformation(items)(hc)))

        result.getMessage must include(s"Failed to match item with CN Code information")
      }
    }
  }
}
