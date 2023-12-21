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
import mocks.connectors.MockEmcsTfeConnector
import mocks.services.{MockGetCnCodeInformationService, MockGetMovementHistoryEventsService, MockGetPackagingTypesService}
import models.common.UnitOfMeasure.Kilograms
import models.response.emcsTfe.MovementItem
import models.response.referenceData.CnCodeInformation
import models.response.{CnCodeInformationException, MovementException, MovementHistoryEventsException, PackagingTypesException, UnexpectedDownstreamResponseError}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class GetMovementServiceSpec extends SpecBase
  with MockEmcsTfeConnector
  with MockGetPackagingTypesService
  with MockGetCnCodeInformationService
  with MockGetMovementHistoryEventsService
  with GetMovementResponseFixtures {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global

  lazy val testService = new GetMovementService(
    getMovementConnector = mockGetMovementConnector,
    getPackagingTypesService = mockGetPackagingTypesService,
    getCnCodeInformationService = mockGetCnCodeInformationService,
    getMovementHistoryEventsService = mockGetMovementHistoryEventsService
  )

  val movementItems: Seq[MovementItem] = Seq(item1, item2)
  val movementItemsWithPackaging: Seq[MovementItem] = Seq(item1WithPackaging, item2WithPackaging)

  ".getMovement" when {

    "GetMovementConnector returns success from downstream" when {

      "Reference Data returns success for Packaging Types" when {

        "Reference Data returns success for CnCodeInformation" must {

          "return successful movement with the packaging and unit of measure added to the response" in {

            MockEmcsTfeConnector.getMovement(testErn, testArc)
              .returns(Future.successful(Right(getMovementResponseModel.copy(items = movementItems))))

            MockGetMovementHistoryEventsService.getMovementHistoryEvents(testErn, testArc)
              .returns(Future.successful(getMovementHistoryEventsResponseModel))

            MockGetPackagingTypesService.getMovementItemsWithPackagingTypes(movementItems)
              .returns(Future.successful(movementItemsWithPackaging))

            MockGetCnCodeInformationService.getCnCodeInformation(movementItemsWithPackaging)
              .returns(Future.successful(Seq(
                item1WithPackaging -> CnCodeInformation(
                  cnCode = "T400",
                  cnCodeDescription = "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
                  exciseProductCode = "24029000",
                  exciseProductCodeDescription = "Fine-cut tobacco for the rolling of cigarettes",
                  unitOfMeasure = Kilograms
                ),
                item2WithPackaging -> CnCodeInformation(
                  cnCode = "T400",
                  cnCodeDescription = "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
                  exciseProductCode = "24029000",
                  exciseProductCodeDescription = "Fine-cut tobacco for the rolling of cigarettes",
                  unitOfMeasure = Kilograms
                )
              )))

            testService.getMovement(testErn, testArc)(hc).futureValue mustBe getMovementResponseModel.copy(items = Seq(
              item1WithPackagingAndCnCodeInfo,
              item2WithPackagingAndCnCodeInfo
            ))
          }
        }

        "Reference Data returns failure for CnCodeInformation" must {

          "raise a CnCodeInformationException" in {

            MockEmcsTfeConnector.getMovement(testErn, testArc)
              .returns(Future.successful(Right(getMovementResponseModel.copy(items = movementItems))))

            MockGetMovementHistoryEventsService.getMovementHistoryEvents(testErn, testArc)
              .returns(Future.successful(getMovementHistoryEventsResponseModel))

            MockGetPackagingTypesService.getMovementItemsWithPackagingTypes(movementItems)
              .returns(Future.successful(movementItemsWithPackaging))

            MockGetCnCodeInformationService.getCnCodeInformation(movementItemsWithPackaging)
              .returns(Future.failed(CnCodeInformationException("bang")))

            val result = intercept[CnCodeInformationException](await(testService.getMovement(testErn, testArc)(hc)))

            result.getMessage must include("bang")
          }
        }
      }

      "Reference Data returns failure for Package Types" must {

        "raise a CnCodeInformationException" in {

          MockGetMovementHistoryEventsService.getMovementHistoryEvents(testErn, testArc)
            .returns(Future.successful(getMovementHistoryEventsResponseModel))

          MockEmcsTfeConnector.getMovement(testErn, testArc)
            .returns(Future.successful(Right(getMovementResponseModel.copy(items = movementItems))))

          MockGetPackagingTypesService.getMovementItemsWithPackagingTypes(movementItems)
            .returns(Future.failed(PackagingTypesException("bang")))

          val result = intercept[PackagingTypesException](await(testService.getMovement(testErn, testArc)(hc)))

          result.getMessage must include("bang")
        }
      }
    }

    "GetMovementConnector returns failure from downstream" must {
      "raise a MovementException" in {

        MockEmcsTfeConnector.getMovement(testErn, testArc)
          .returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result = intercept[MovementException](await(testService.getMovement(testErn, testArc)(hc)))

        result.getMessage must include("Failed to retrieve movement from emcs-tfe")
      }
    }

    "GetMovementHistoryEventsService returns failure from downstream" must {
      "raise a MovementHistoryEventsException" in {

        MockEmcsTfeConnector.getMovement(testErn, testArc)
          .returns(Future.successful(Right(getMovementResponseModel.copy(items = movementItems))))

        MockGetMovementHistoryEventsService.getMovementHistoryEvents(testErn, testArc)
          .returns(Future.failed(MovementHistoryEventsException("bang")))

        val result = intercept[MovementHistoryEventsException](await(testService.getMovement(testErn, testArc)(hc)))

        result.getMessage must include("bang")
      }
    }
  }
}
