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

import connectors.emcsTfe.GetMessageStatisticsConnector
import models.response.MessageStatisticsException
import models.response.emcsTfe.GetMessageStatisticsResponse
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}
import javax.inject.{Inject, Singleton}

@Singleton
class GetMessageStatisticsService @Inject()(connector: GetMessageStatisticsConnector)(implicit ec: ExecutionContext) {

  def getMessageStatistics(ern: String)(implicit hc: HeaderCarrier): Future[GetMessageStatisticsResponse] = {
    connector.getMessageStatistics(ern).map {
      case Right(messageStatistics) => messageStatistics
      case _ => throw MessageStatisticsException(s"No message statistics found for trader $ern")
    }
  }

}
