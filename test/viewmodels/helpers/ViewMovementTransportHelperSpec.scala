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

package viewmodels.helpers

import base.SpecBase
import fixtures.GetMovementResponseFixtures
import fixtures.messages.ViewMovementMessages
import models.response.emcsTfe.GetMovementResponse
import org.jsoup.Jsoup
import play.api.i18n.Messages
import viewmodels.govuk.TagFluency

class ViewMovementTransportHelperSpec extends SpecBase with GetMovementResponseFixtures with TagFluency {
  lazy val helper: ViewMovementTransportHelper = app.injector.instanceOf[ViewMovementTransportHelper]

  val movementResponseWithReferenceData: GetMovementResponse = getMovementResponseModel

  ".constructMovementTransport" must {
    Seq(ViewMovementMessages.English).foreach {
      messagesForLang =>
        s"when rendering in language code of '${messagesForLang.lang.code}'" must {

          implicit lazy val msgs: Messages = messages(Seq(messagesForLang.lang))

          "return a table of transport information from the movement formatted with the correct wording" in {
            val result = Jsoup.parse(helper.constructMovementTransport(movementResponseWithReferenceData).toString())

            result.getElementsByTag("h2").get(0).text mustBe messagesForLang.transportDetailsHeading
            val summaryCards = result.getElementsByClass("govuk-summary-card")

            val transportSummaryCard = summaryCards.get(0)
            transportSummaryCard.getElementsByTag("h2").text mustBe messagesForLang.transportSummaryHeading
            val transportSummaryCardRows = transportSummaryCard.getElementsByClass("govuk-summary-list__row")
            transportSummaryCardRows.get(0).getElementsByTag("dt").text() mustBe messagesForLang.transportArranger
            transportSummaryCardRows.get(0).getElementsByTag("dd").text() mustBe "Consignor"
            transportSummaryCardRows.get(1).getElementsByTag("dt").text() mustBe messagesForLang.transportModeOfTransport
            transportSummaryCardRows.get(1).getElementsByTag("dd").text() mustBe "Air transport"
            transportSummaryCardRows.get(2).getElementsByTag("dt").text() mustBe messagesForLang.transportJourneyTime
            transportSummaryCardRows.get(2).getElementsByTag("dd").text() mustBe "20 days"

            val firstTransporterCard = summaryCards.get(1)
            firstTransporterCard.getElementsByTag("h2").text mustBe messagesForLang.transportFirstTransporterHeading
            val firstTransporterCardRows = firstTransporterCard.getElementsByClass("govuk-summary-list__row")
            firstTransporterCardRows.get(0).getElementsByTag("dt").text() mustBe messagesForLang.transportFirstTransporterName
            firstTransporterCardRows.get(0).getElementsByTag("dd").text() mustBe "testFirstTransporterTraderName"
            firstTransporterCardRows.get(1).getElementsByTag("dt").text() mustBe messagesForLang.transportFirstTransporterAddress
            firstTransporterCardRows.get(1).getElementsByTag("dd").text() mustBe "Main101 Zeebrugge ZZ78"
            firstTransporterCardRows.get(2).getElementsByTag("dt").text() mustBe messagesForLang.transportFirstTransporterVatRegistrationNumber
            firstTransporterCardRows.get(2).getElementsByTag("dd").text() mustBe "testVatNumber"

            val transportUnit1Card = summaryCards.get(2)
            transportUnit1Card.getElementsByTag("h2").text mustBe messagesForLang.transportUnitHeading(1)
            val transportUnit1CardRows = transportUnit1Card.getElementsByClass("govuk-summary-list__row")
            transportUnit1CardRows.get(0).getElementsByTag("dt").text() mustBe messagesForLang.transportUnitType
            transportUnit1CardRows.get(0).getElementsByTag("dd").text() mustBe "Container"
            transportUnit1CardRows.get(1).getElementsByTag("dt").text() mustBe messagesForLang.transportUnitIdentity
            transportUnit1CardRows.get(1).getElementsByTag("dd").text() mustBe "AB11 1T4"
            transportUnit1CardRows.get(2).getElementsByTag("dt").text() mustBe messagesForLang.transportUnitCommercialSeal
            transportUnit1CardRows.get(2).getElementsByTag("dd").text() mustBe "Not provided"
            transportUnit1CardRows.get(3).getElementsByTag("dt").text() mustBe messagesForLang.transportUnitComplementaryInformation
            transportUnit1CardRows.get(3).getElementsByTag("dd").text() mustBe "Not provided"
            transportUnit1CardRows.get(4).getElementsByTag("dt").text() mustBe messagesForLang.transportUnitSealInformation
            transportUnit1CardRows.get(4).getElementsByTag("dd").text() mustBe "Not provided"

            val transportUnit2Card = summaryCards.get(3)
            transportUnit2Card.getElementsByTag("h2").text mustBe messagesForLang.transportUnitHeading(2)
            val transportUnit2CardRows = transportUnit2Card.getElementsByClass("govuk-summary-list__row")
            transportUnit2CardRows.get(0).getElementsByTag("dt").text() mustBe messagesForLang.transportUnitType
            transportUnit2CardRows.get(0).getElementsByTag("dd").text() mustBe "Vehicle"
            transportUnit2CardRows.get(1).getElementsByTag("dt").text() mustBe messagesForLang.transportUnitIdentity
            transportUnit2CardRows.get(1).getElementsByTag("dd").text() mustBe "AB22 2T4"
            transportUnit2CardRows.get(2).getElementsByTag("dt").text() mustBe messagesForLang.transportUnitCommercialSeal
            transportUnit2CardRows.get(2).getElementsByTag("dd").text() mustBe "Not provided"
            transportUnit2CardRows.get(3).getElementsByTag("dt").text() mustBe messagesForLang.transportUnitComplementaryInformation
            transportUnit2CardRows.get(3).getElementsByTag("dd").text() mustBe "Not provided"
            transportUnit2CardRows.get(4).getElementsByTag("dt").text() mustBe messagesForLang.transportUnitSealInformation
            transportUnit2CardRows.get(4).getElementsByTag("dd").text() mustBe "Not provided"
          }
        }
    }
  }
}
