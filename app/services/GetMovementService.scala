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

import connectors.emcsTfe.GetMovementConnector
import models.response.MovementException
import models.response.emcsTfe.GetMovementResponse
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetMovementService @Inject()(getMovementConnector: GetMovementConnector,
                                   getPackagingTypesService: GetPackagingTypesService,
                                   getCnCodeInformationService: GetCnCodeInformationService,
                                   getWineOperationsService: GetWineOperationsService,
                                   getMovementHistoryEventsService: GetMovementHistoryEventsService)(implicit ec: ExecutionContext) {

  def getRawMovement(ern: String, arc: String)(implicit hc: HeaderCarrier) : Future[GetMovementResponse] =
    getMovementConnector.getMovement(ern, arc).map {
      case Right(movement) => movement
      case Left(errorResponse) =>
        throw MovementException(s"Failed to retrieve movement from emcs-tfe: $errorResponse")

    }

  def getMovement(ern: String, arc: String)(implicit hc: HeaderCarrier): Future[GetMovementResponse] =
    getRawMovement(ern, arc).flatMap { movement =>
      for {
        historyEvents <- getMovementHistoryEventsService.getMovementHistoryEvents(ern, arc)
        itemsWithWineOperations <- getWineOperationsService.getWineOperations(movement.items)
        itemsWithWineAndPackaging <- getPackagingTypesService.getMovementItemsWithPackagingTypes(itemsWithWineOperations)
        itemsWithCnCodeInfo <- getCnCodeInformationService.getCnCodeInformation(itemsWithWineAndPackaging)
        itemsWithWineAndPackagingAndCnCodeInfo = itemsWithCnCodeInfo.map {
          case (item, cnCodeInfo) => item.copy(
            unitOfMeasure = Some(cnCodeInfo.unitOfMeasure),
            productCodeDescription = Some(cnCodeInfo.exciseProductCodeDescription)
          )
        }
      } yield movement.copy(
        items = itemsWithWineAndPackagingAndCnCodeInfo,
        eventHistorySummary = Some(historyEvents)
      )
    }
}
