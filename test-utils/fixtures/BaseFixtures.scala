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

package fixtures

import models.common.TraderKnownFacts
import models.response.emcsTfe.GetMessageStatisticsResponse
import play.api.libs.json.{JsValue, Json}


trait BaseFixtures {

  val testErn = "GBWKTestErn"
  val testArc = "ARC"
  val testCredId = "cred1234567891"
  val testInternalId = "int1234567891"
  val testSequenceNumber = 1
  val testLrn = "123"
  val testTraderName = "testTraderName"
  val testUniqueMessageIdentifier = 1234

  val testDutyPaidErn = "XIPATestErn"

  val testMinTraderKnownFacts: TraderKnownFacts = TraderKnownFacts(
    traderName = testTraderName,
    addressLine1 = None,
    addressLine2 = None,
    addressLine3 = None,
    addressLine4 = None,
    addressLine5 = None,
    postcode = None
  )

  val testTraderKnownFactsJson: JsValue = Json.parse("""{ "traderName": "Trader" }""")

  val testMessageStatistics: GetMessageStatisticsResponse = GetMessageStatisticsResponse(
    countOfAllMessages = 10,
    countOfNewMessages = 5
  )

  val testMessageStatisticsJson: JsValue = Json.parse(
    s"""
       |{
       |   "countOfAllMessages" : 1,
       |   "countOfNewMessages" : 1
       |}""".stripMargin
  )

}
