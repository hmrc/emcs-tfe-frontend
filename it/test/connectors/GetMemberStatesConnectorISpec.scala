/*
 * Copyright 2025 HM Revenue & Customs
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

package connectors

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, urlEqualTo}
import com.github.tomakehurst.wiremock.http.Fault
import connectors.referenceData.GetMemberStatesConnector
import fixtures.{BaseFixtures, MemberStatesFixtures}
import models.MemberState
import models.response.UnexpectedDownstreamResponseError
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import play.api.libs.json.Json
import play.api.test.Helpers._
import support.IntegrationBaseSpec
import uk.gov.hmrc.http.HeaderCarrier

class GetMemberStatesConnectorISpec extends IntegrationBaseSpec
  with ScalaFutures
  with BaseFixtures
  with IntegrationPatience
  with MemberStatesFixtures {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  val url = "/emcs-tfe-reference-data/oracle/member-states"

  val memberStatesSeq: Seq[MemberState] = Seq(
    memberStateAT,
    memberStateBE
  )

  ".getMemberStates" must {

    lazy val connector: GetMemberStatesConnector = app.injector.instanceOf[GetMemberStatesConnector]

    "must return Right(Seq[MemberState]) when the server responds OK" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.stringify(Json.arr(memberStateJsonBT, memberStateJsonBE))))
      )

      connector.getMemberStates().futureValue mustBe Right(memberStatesSeq)
    }

    "must fail when the server responds with any other status" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.getMemberStates().futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.getMemberStates().futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }
}
