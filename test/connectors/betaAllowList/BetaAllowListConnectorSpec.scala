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

package connectors.betaAllowList

import base.SpecBase
import mocks.connectors.MockHttpClient
import models.response.UnexpectedDownstreamResponseError
import org.scalatest.BeforeAndAfterAll
import play.api.http.{HeaderNames, MimeTypes, Status}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class BetaAllowListConnectorSpec extends SpecBase
  with Status with MimeTypes with HeaderNames with MockHttpClient with BeforeAndAfterAll {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  lazy val connector = new BetaAllowListConnector(mockHttpClient, appConfig)

  "check" should {

    "return a successful response" when {

      "downstream call is successful" in {

        MockHttpClient.get(
          url = s"${appConfig.emcsTfeBaseUrl}/beta/eligibility/$testErn/navHub"
        ).returns(Future.successful(Right(true)))

        connector.check(testErn, appConfig.betaCheckServiceName).futureValue mustBe Right(true)
      }
    }

    "return an error response" when {

      "downstream call fails" in {

        MockHttpClient.get(
          url = s"${appConfig.emcsTfeBaseUrl}/beta/eligibility/$testErn/navHub"
        ).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        connector.check(testErn, appConfig.betaCheckServiceName).futureValue mustBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }
}