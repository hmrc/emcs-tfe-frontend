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

package uk.gov.hmrc.emcstfefrontend.connectors.referenceData

import uk.gov.hmrc.emcstfefrontend.config.AppConfig
import uk.gov.hmrc.emcstfefrontend.models.common.TraderKnownFacts
import uk.gov.hmrc.emcstfefrontend.models.response.{ErrorResponse, JsonValidationError, UnexpectedDownstreamResponseError}
import play.api.libs.json.JsResultException
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetTraderKnownFactsConnector @Inject()(val http: HttpClient,
                                              config: AppConfig) extends GetTraderKnownFactsHttpParser {

  def baseUrl: String = config.traderKnownFactsReferenceDataBaseUrl

  def getTraderKnownFacts(ern: String)
                         (implicit headerCarrier: HeaderCarrier,
                          executionContext: ExecutionContext): Future[Either[ErrorResponse, Option[TraderKnownFacts]]] =
    get(baseUrl + "/oracle/trader-known-facts", ern)
      .recover {
        case JsResultException(errors) =>
          logger.warn(s"[getTraderKnownFacts] Bad JSON response from emcs-tfe-reference-data: " + errors)
          Left(JsonValidationError)
        case error =>
          logger.warn(s"[getTraderKnownFacts] Unexpected error from reference-data: ${error.getClass} ${error.getMessage}")
          Left(UnexpectedDownstreamResponseError)
      }
}
