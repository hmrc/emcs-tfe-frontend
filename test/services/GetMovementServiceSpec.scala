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
import mocks.services.{MockGetCnCodeInformationService, MockGetMovementHistoryEventsService, MockGetPackagingTypesService, MockGetWineOperationsService}
import models.common.UnitOfMeasure.Kilograms
import models.response.emcsTfe.MovementItem
import models.response.referenceData.CnCodeInformation
import models.response.{CnCodeInformationException, MovementException, MovementHistoryEventsException, PackagingTypesException, UnexpectedDownstreamResponseError, WineOperationsException}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class GetMovementServiceSpec extends SpecBase
  with MockEmcsTfeConnector
  with MockGetPackagingTypesService
  with MockGetCnCodeInformationService
  with MockGetMovementHistoryEventsService
  with MockGetWineOperationsService
  with GetMovementResponseFixtures {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global

  lazy val testService = new GetMovementService(
    getMovementConnector = mockGetMovementConnector,
    getPackagingTypesService = mockGetPackagingTypesService,
    getCnCodeInformationService = mockGetCnCodeInformationService,
    getMovementHistoryEventsService = mockGetMovementHistoryEventsService,
    getWineOperationsService = mockGetWineOperationsService
  )

  val movementItems: Seq[MovementItem] = Seq(item1, item2)
  val movementItemsWithWineOperations = Seq(item1WithWineOperations, item2WithWineOperations)
  val movementItemsWithWineAndPackaging: Seq[MovementItem] = Seq(item1WithWineAndPackaging, item2WithWineAndPackaging)

  ".getMovement" when {

    "GetMovementConnector returns success from downstream" when {

      "GetMovementHistoryEventsService returns success from downstream" when {

        "Reference Data returns success for Wine Operations" when {

          "Reference Data returns success for Packaging Types" when {

            "Reference Data returns success for CnCodeInformation" must {

              "return successful movement with the packaging and unit of measure added to the response" in {

                MockEmcsTfeConnector.getMovement(testErn, testArc)
                  .returns(Future.successful(Right(getMovementResponseModel.copy(items = movementItems))))

                MockGetMovementHistoryEventsService.getMovementHistoryEvents(testErn, testArc)
                  .returns(Future.successful(getMovementHistoryEventsResponseModel))

                MockGetWineOperationsService.getWineOperations(movementItems)
                  .returns(Future.successful(movementItemsWithWineOperations))

                MockGetPackagingTypesService.getMovementItemsWithPackagingTypes(movementItemsWithWineOperations)
                  .returns(Future.successful(movementItemsWithWineAndPackaging))

                MockGetCnCodeInformationService.getCnCodeInformation(movementItemsWithWineAndPackaging)
                  .returns(Future.successful(Seq(
                    item1WithWineAndPackaging -> CnCodeInformation(
                      cnCode = "T400",
                      cnCodeDescription = "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
                      exciseProductCode = "24029000",
                      exciseProductCodeDescription = "Fine-cut tobacco for the rolling of cigarettes",
                      unitOfMeasure = Kilograms
                    ),
                    item2WithWineAndPackaging -> CnCodeInformation(
                      cnCode = "T400",
                      cnCodeDescription = "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
                      exciseProductCode = "24029000",
                      exciseProductCodeDescription = "Fine-cut tobacco for the rolling of cigarettes",
                      unitOfMeasure = Kilograms
                    )
                  )))

                testService.getMovement(testErn, testArc)(hc).futureValue mustBe getMovementResponseModel.copy(items = Seq(
                  item1WithWineAndPackagingAndCnCodeInfo,
                  item2WithWineAndPackagingAndCnCodeInfo
                ))
              }
            }

            "Reference Data returns failure for CnCodeInformation" must {

              "raise a CnCodeInformationException" in {

                MockEmcsTfeConnector.getMovement(testErn, testArc)
                  .returns(Future.successful(Right(getMovementResponseModel.copy(items = movementItems))))

                MockGetMovementHistoryEventsService.getMovementHistoryEvents(testErn, testArc)
                  .returns(Future.successful(getMovementHistoryEventsResponseModel))

                MockGetWineOperationsService.getWineOperations(movementItems)
                  .returns(Future.successful(movementItemsWithWineOperations))

                MockGetPackagingTypesService.getMovementItemsWithPackagingTypes(movementItemsWithWineOperations)
                  .returns(Future.successful(movementItemsWithWineAndPackaging))

                MockGetCnCodeInformationService.getCnCodeInformation(movementItemsWithWineAndPackaging)
                  .returns(Future.failed(CnCodeInformationException("bang")))

                val result = intercept[CnCodeInformationException](await(testService.getMovement(testErn, testArc)(hc)))

                result.getMessage must include("bang")
              }
            }
          }

          "Reference Data returns failure for Package Types" must {

            "raise a PackagingTypesException" in {

              MockGetMovementHistoryEventsService.getMovementHistoryEvents(testErn, testArc)
                .returns(Future.successful(getMovementHistoryEventsResponseModel))

              MockEmcsTfeConnector.getMovement(testErn, testArc)
                .returns(Future.successful(Right(getMovementResponseModel.copy(items = movementItems))))

              MockGetWineOperationsService.getWineOperations(movementItems)
                .returns(Future.successful(movementItemsWithWineOperations))

              MockGetPackagingTypesService.getMovementItemsWithPackagingTypes(movementItemsWithWineOperations)
                .returns(Future.failed(PackagingTypesException("bang")))

              val result = intercept[PackagingTypesException](await(testService.getMovement(testErn, testArc)(hc)))

              result.getMessage must include("bang")
            }
          }
        }

        "Reference Data returns failure for Wine Operations" must {

          "raise a WineOperationsExcepion" in {

            MockGetMovementHistoryEventsService.getMovementHistoryEvents(testErn, testArc)
              .returns(Future.successful(getMovementHistoryEventsResponseModel))

            MockEmcsTfeConnector.getMovement(testErn, testArc)
              .returns(Future.successful(Right(getMovementResponseModel.copy(items = movementItems))))

            MockGetWineOperationsService.getWineOperations(movementItems)
              .returns(Future.failed(WineOperationsException("bang")))

            val result = intercept[WineOperationsException](await(testService.getMovement(testErn, testArc)(hc)))

            result.getMessage must include("bang")
          }
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

    "GetMovementConnector returns failure from downstream" must {

      "raise a MovementException" in {

        MockEmcsTfeConnector.getMovement(testErn, testArc)
          .returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result = intercept[MovementException](await(testService.getMovement(testErn, testArc)(hc)))

        result.getMessage must include("Failed to retrieve movement from emcs-tfe")
      }
    }
  }
}
