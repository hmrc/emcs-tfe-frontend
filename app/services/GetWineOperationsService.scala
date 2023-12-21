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

import connectors.referenceData.GetWineOperationsConnector
import models.common.WineProduct
import models.requests.WineOperationsRequest
import models.response.WineOperationsException
import models.response.emcsTfe.MovementItem
import models.response.referenceData.WineOperationsResponse
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetWineOperationsService @Inject()(connector: GetWineOperationsConnector)
                                        (implicit ec: ExecutionContext) {

  def getWineOperations(items: Seq[MovementItem])(implicit hc: HeaderCarrier): Future[Seq[MovementItem]] =
    items.flatMap(_.wineProduct.flatMap(_.wineOperations)).flatten match {
      case Nil => Future.successful(items)
      case operations =>
        connector.getWineOperations(WineOperationsRequest(operations.distinct)).map {
          case Right(response) => matchItemsWithReferenceDataValues(response, items)
          case Left(errorResponse) => throw WineOperationsException(s"Failed to retrieve wine operations from emcs-tfe-reference-data: $errorResponse")
        }
    }

  private def matchItemsWithReferenceDataValues(response: WineOperationsResponse, items: Seq[MovementItem]): Seq[MovementItem] =
    items.map { item =>
      val newWineProduct: Option[WineProduct] = item.wineProduct.map { wineProduct =>
        val newOperations: Option[Seq[String]] = wineProduct.wineOperations.map { operations =>
          operations.map(response.data.get(_) match {
            case Some(value) => value
            case None => throw WineOperationsException(s"Failed to match item with wine operation from emcs-tfe-reference-data: $item")
          })
        }
        wineProduct.copy(wineOperations = newOperations)
      }
      item.copy(wineProduct = newWineProduct)
    }
}
