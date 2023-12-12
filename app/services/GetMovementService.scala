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
import uk.gov.hmrc.emcstfefrontend.models.response.emcsTfe.GetMovementResponse
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetMovementService @Inject()(getMovementConnector: GetMovementConnector,
                                   getPackagingTypesService: GetPackagingTypesService,
                                   getCnCodeInformationService: GetCnCodeInformationService)(implicit ec: ExecutionContext) {

  def getMovement(ern: String, arc: String)(implicit hc: HeaderCarrier): Future[GetMovementResponse] =
    getMovementConnector.getMovement(ern, arc).flatMap {
      case Right(movement) =>
        for {
          itemsWithPackaging <- getPackagingTypesService.getMovementItemsWithPackagingTypes(movement.items)
          itemsWithCnCodeInfo <- getCnCodeInformationService.getCnCodeInformation(itemsWithPackaging)
          itemsWithPackagingAndCnCodeInfo = itemsWithCnCodeInfo.map {
            case (item, cnCodeInfo) => item.copy(unitOfMeasure = Some(cnCodeInfo.unitOfMeasure))
          }
        } yield movement.copy(items = itemsWithPackagingAndCnCodeInfo)

      case Left(errorResponse) =>
        throw MovementException(s"Failed to retrieve movement from emcs-tfe: $errorResponse")
    }
}
