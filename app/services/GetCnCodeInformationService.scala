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

import connectors.referenceData.GetCnCodeInformationConnector
import models.draftTemplates.TemplateItem
import models.requests.{CnCodeInformationItem, CnCodeInformationRequest}
import models.response.CnCodeInformationException
import models.response.emcsTfe.MovementItem
import models.response.referenceData.{CnCodeInformation, CnCodeInformationResponse}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetCnCodeInformationService @Inject()(connector: GetCnCodeInformationConnector)
                                           (implicit ec: ExecutionContext) {

  def getCnCodeInformation(items: Seq[MovementItem])(implicit hc: HeaderCarrier): Future[Seq[(MovementItem, CnCodeInformation)]] = {

    def matchMovementItemsWithReferenceDataValues(response: CnCodeInformationResponse,
                                                          items: Seq[MovementItem]): Seq[(MovementItem, CnCodeInformation)] = {
      items.collect {
        case item if response.data.contains(item.cnCode) =>
          item -> response.data(item.cnCode)
        case item =>
          throw CnCodeInformationException(s"Failed to match item with CN Code information: $item")
      }
    }

    connector.getCnCodeInformation(CnCodeInformationRequest(CnCodeInformationItem(items))).map {
      case Right(response) =>
        matchMovementItemsWithReferenceDataValues(response, items)
      case Left(errorResponse) =>
        throw CnCodeInformationException(s"Failed to retrieve CN Code information: $errorResponse")
    }
  }


  def getCnCodeInformationForTemplateItems(items: Seq[TemplateItem])(implicit hc: HeaderCarrier): Future[Seq[(TemplateItem, CnCodeInformation)]] = {

    val cnCodeItems = items
      .map(item => CnCodeInformationItem(item.itemExciseProductCode, item.itemCommodityCode))
      .distinct

    connector
      .getCnCodeInformation(CnCodeInformationRequest(cnCodeItems))
      .map {
        case Right(response) =>
          items.collect {
            case item if response.data.contains(item.itemCommodityCode) =>
              item -> response.data(item.itemCommodityCode)
            case item =>
              throw CnCodeInformationException(s"Failed to match CN Code information with template item : $item")
          }
        case Left(errorResponse) =>
          throw CnCodeInformationException(s"Failed to retrieve CN Code information: $errorResponse")
      }

  }

}
