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

import connectors.referenceData.GetItemPackagingTypesConnector
import models.response.PackagingTypesException
import models.response.emcsTfe.MovementItem
import models.response.referenceData.ItemPackaging
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetPackagingTypesService @Inject()(connector: GetItemPackagingTypesConnector)
                                        (implicit ec: ExecutionContext) {

  def getMovementItemsWithPackagingTypes(items: Seq[MovementItem])(implicit hc: HeaderCarrier): Future[Seq[MovementItem]] = {
    connector.getItemPackagingTypes.map {
      case Right(response) => matchItemsWithReferenceDataValues(response, items)
      case Left(errorResponse) => throw PackagingTypesException(s"Failed to retrieve packaging types from emcs-tfe-reference-data: $errorResponse")
    }
  }

  private def matchItemsWithReferenceDataValues(response: Seq[ItemPackaging], items: Seq[MovementItem]): Seq[MovementItem] =
    items.map { item =>
      item.copy(packaging = item.packaging.collect {
        case packaging =>
          response.find(_.packagingType == packaging.typeOfPackage) match {
            case Some(value) =>
              packaging.copy(typeOfPackage = value.description)
            case None =>
              throw PackagingTypesException(s"Failed to match item with packaging type from emcs-tfe-reference-data: $item")
          }
      })
    }
}
