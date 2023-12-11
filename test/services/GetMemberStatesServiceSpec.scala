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

import base.SpecBase
import fixtures.MemberStatesFixtures
import mocks.connectors.MockGetMemberStatesConnector
import models.response.{MemberStatesException, UnexpectedDownstreamResponseError}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class GetMemberStatesServiceSpec extends SpecBase with MockGetMemberStatesConnector with MemberStatesFixtures {

  implicit val hc = HeaderCarrier()
  implicit val ec = ExecutionContext.global

  lazy val testService = new GetMemberStatesService(mockGetMemberStatesConnector)

  ".getMemberStates" must {

    "must return Seq[MemberState]" when {

      "when Connector returns success from downstream" in {

        val expectedResult = Seq(
          memberStateAT,
          memberStateBE
        )

        MockGetMemberStatesConnector.getMemberStates().returns(Future(Right(Seq(memberStateAT, memberStateBE))))

        val actualResults = testService.getMemberStates().futureValue

        actualResults mustBe expectedResult
      }
    }

    "must throw MemberStatesException" when {

      "when Connector returns failure from downstream" in {

        val expectedResult = "No member states retrieved"

        MockGetMemberStatesConnector.getMemberStates().returns(Future(Left(UnexpectedDownstreamResponseError)))

        val actualResult = intercept[MemberStatesException](await(testService.getMemberStates())).getMessage

        actualResult mustBe expectedResult
      }
    }
  }
}
