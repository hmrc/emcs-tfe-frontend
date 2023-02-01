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

import cats.data.EitherT
import connectors.emcsTfe.{HelloConnector => HelloEmcsConnector}
import connectors.referenceData.{HelloConnector => HelloReferenceDataConnector}
import models.response.ErrorResponse
import models.response.emcsTfe.EmcsTfeResponse
import models.response.referenceData.ReferenceDataResponse
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HelloWorldService @Inject()(helloReferenceDataConnector: HelloReferenceDataConnector, helloEmcsConnector: HelloEmcsConnector) {
  def getMessage()(implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): EitherT[Future, ErrorResponse, (ReferenceDataResponse, EmcsTfeResponse)] = for {
    referenceDataResponse <- EitherT(helloReferenceDataConnector.hello())
    emcsTfeResponse <- EitherT(helloEmcsConnector.hello())
  } yield (referenceDataResponse, emcsTfeResponse)
}
