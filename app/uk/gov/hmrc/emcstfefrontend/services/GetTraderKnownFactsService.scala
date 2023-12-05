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

package uk.gov.hmrc.emcstfefrontend.services

import uk.gov.hmrc.emcstfefrontend.connectors.referenceData.GetTraderKnownFactsConnector
import uk.gov.hmrc.emcstfefrontend.models.common.TraderKnownFacts
import uk.gov.hmrc.emcstfefrontend.models.response.TraderKnownFactsException
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetTraderKnownFactsService @Inject()(connector: GetTraderKnownFactsConnector)
                                           (implicit ec: ExecutionContext) {


  def getTraderKnownFacts(ern: String)(implicit hc: HeaderCarrier): Future[TraderKnownFacts] = {
    connector.getTraderKnownFacts(ern).map {
      case Right(Some(traderKnownFacts)) => traderKnownFacts
      case _ => throw TraderKnownFactsException(s"No known facts found for trader $ern")
    }
  }


}
