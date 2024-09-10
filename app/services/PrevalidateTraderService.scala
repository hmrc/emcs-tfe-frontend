/*
 * Copyright 2024 HM Revenue & Customs
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

import connectors.emcsTfe.PrevalidateTraderConnector
import models.prevalidate.EntityGroup
import models.requests.PrevalidateTraderRequest
import models.response.PrevalidateTraderException
import models.response.emcsTfe.prevalidateTrader.PreValidateTraderResponse
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PrevalidateTraderService @Inject()(connector: PrevalidateTraderConnector)
                                        (implicit ec: ExecutionContext) {

  def prevalidateTrader(ern: String, ernToCheck: String, entityGroup: Option[EntityGroup], productCodesToCheck: Option[Seq[String]])(implicit hc: HeaderCarrier): Future[PreValidateTraderResponse] = {

    val requestModel = PrevalidateTraderRequest(ernToCheck, entityGroup, productCodesToCheck)
    connector.prevalidateTrader(ern, requestModel).map {
      case Left(_) => throw PrevalidateTraderException("Prevalidate trader result error")
      case Right(response) => response
    }
  }
}
