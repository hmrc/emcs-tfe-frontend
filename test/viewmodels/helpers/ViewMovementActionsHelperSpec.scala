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

package viewmodels.helpers

import base.SpecBase
import fixtures.GetMovementResponseFixtures
import models.MovementEadStatus
import models.common.DestinationType
import models.requests.DataRequest
import play.api.i18n.Messages
import play.api.test.FakeRequest
import views.html.components.link

import java.time.LocalDate

class ViewMovementActionsHelperSpec extends SpecBase with GetMovementResponseFixtures {

  val helper: ViewMovementActionsHelper = app.injector.instanceOf[ViewMovementActionsHelper]
  val link: link = app.injector.instanceOf[link]

  implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"), ern = testErn)
  implicit lazy val messages: Messages = messages(request)

  ".cancelMovementLink" should {

    val testMovement = getMovementResponseModel.copy(
      eadStatus = MovementEadStatus.Accepted,
      eadEsad = getMovementResponseModel.eadEsad.copy(upstreamArc = None),
      dateOfDispatch = LocalDate.now.plusWeeks(1)
    )

    "return a link" when {

      "when the movement can be cancelled" in {
        helper.cancelMovementLink(testMovement) mustBe
          Some(
            link(
              link = appConfig.emcsTfeCancelMovementUrl(testErn, testArc),
              messageKey = "viewMovement.cancelMovement",
              id = Some("cancel-this-movement"),
              hintKey = Some("viewMovement.cancelMovement.info")
            )
          )
      }
    }

    "not return a link" when {

      "when the movement has an incorrect movement status" in {
        helper.cancelMovementLink(
          testMovement.copy(eadStatus = MovementEadStatus.Delivered)
        ) mustBe None
      }

      "when the movement has been split" in {
        helper.cancelMovementLink(
          testMovement.copy(eadEsad = getMovementResponseModel.eadEsad.copy(upstreamArc = Some("123")))
        ) mustBe None
      }

      "when the movement dispatch date has already passed" in {
        helper.cancelMovementLink(
          testMovement.copy(dateOfDispatch = LocalDate.now.minusDays(1))
        ) mustBe None
      }

    }

  }

  ".changeDestinationLink" should {
    val testMovement = getMovementResponseModel.copy(eadStatus = MovementEadStatus.Accepted)

    "return a link" when {

      "when the movement can be diverted" in {
        helper.changeDestinationLink(testMovement) mustBe
          Some(
            link(
              link = appConfig.emcsTfeChangeDestinationUrl(testErn, testArc),
              messageKey = "viewMovement.changeDestination",
              id = Some("submit-a-change-of-destination"),
              hintKey = Some("viewMovement.changeDestination.info")
            )
          )
      }
    }

    "not return a link" when {

      "when the movement has an incorrect movement status" in {
        helper.changeDestinationLink(testMovement.copy(eadStatus = MovementEadStatus.Delivered)) mustBe None
      }

    }

  }

  ".alertOrRejectionLink" should {
    val testMovement = getMovementResponseModel.copy(eadStatus = MovementEadStatus.Accepted)

    "return a link" when {

      "when the movement can be alerted or rejected" in {
        helper.alertOrRejectionLink(testMovement) mustBe
          Some(
            link(
              link = appConfig.emcsTfeAlertOrRejectionUrl(testErn, testArc),
              messageKey = "viewMovement.alertOrRejection",
              id = Some("submit-alert-or-rejection"),
              hintKey = Some("viewMovement.alertOrRejection.info")
            )
          )
      }
    }

    "not return a link" when {

      "when the movement has an incorrect movement status" in {
        helper.alertOrRejectionLink(testMovement.copy(eadStatus = MovementEadStatus.Delivered)) mustBe None
      }

    }

  }

  ".reportOfReceiptLink" should {
    val testMovement = getMovementResponseModel.copy(
      eadStatus = MovementEadStatus.Accepted,
      consigneeTrader = getMovementResponseModel.consigneeTrader.map(_.copy(traderExciseNumber = Some(testErn)))
    )

    "return a link" when {

      "when the movement can be receipted and logged in user is the consignee of the movement" in {
        helper.reportOfReceiptLink(testMovement) mustBe
          Some(
            link(
              link = appConfig.emcsTfeReportAReceiptUrl(testErn, testArc),
              messageKey = "viewMovement.reportAReceipt",
              id = Some("submit-report-of-receipt"),
              hintKey = Some("viewMovement.reportAReceipt.info")
            )
          )
      }
    }

    "not return a link" when {

      "when the movement has an incorrect movement status to be receipted" in {
        helper.reportOfReceiptLink(testMovement.copy(eadStatus = MovementEadStatus.Delivered)) mustBe None
      }

      "has the correct status but the logged in user is not the consignee of the movement" in {
        val testMovement = getMovementResponseModel.copy(
          eadStatus = MovementEadStatus.Accepted,
          consigneeTrader = getMovementResponseModel.consigneeTrader.map(_.copy(traderExciseNumber = Some("GBWKTest123")))
        )

        helper.reportOfReceiptLink(testMovement) mustBe None
      }

    }

  }

  ".explainADelayLink" should {
    val testMovement = getMovementResponseModel

    "return a link" when {

      "when an explanation of delay could be submitted" in {
        helper.explainADelayLink(testMovement) mustBe
          Some(
            link(
              link = appConfig.emcsTfeExplainDelayUrl(testErn, testArc),
              messageKey = "viewMovement.explainDelay",
              id = Some("explain-a-delay"),
              hintKey = Some("viewMovement.explainDelay.info")
            )
          )
      }
    }
  }

  ".shortageOrExcessLink" when {

    "when the movement is an export" should {
      val testMovement = getMovementResponseModel.copy(
        eadStatus = MovementEadStatus.Accepted,
        destinationType = DestinationType.Export
      )

      "return a link" when {
        "when a shortage or excess is allowed" in {
          helper.shortageOrExcessLink(testMovement) mustBe
            Some(
              link(
                link = appConfig.emcsTfeExplainShortageOrExcessUrl(testErn, testArc),
                messageKey = "viewMovement.explainShortageOrExcess",
                id = Some("explain-shortage-or-excess"),
                hintKey = Some("viewMovement.explainShortageOrExcess.info")
              )
            )
        }
      }

      "not return a link" when {
        "when the movement has the wrong status" in {
          helper.shortageOrExcessLink(testMovement.copy(eadStatus = MovementEadStatus.DeemedExported)) mustBe None
        }
      }
    }

    "when the movement is not an export" should {
      val testMovement = getMovementResponseModel.copy(
        eadStatus = MovementEadStatus.Exporting,
        destinationType = DestinationType.TemporaryCertifiedConsignee
      )

      "return a link" when {
        "when a shortage or excess is allowed" in {
          helper.shortageOrExcessLink(testMovement) mustBe
            Some(
              link(
                link = appConfig.emcsTfeExplainShortageOrExcessUrl(testErn, testArc),
                messageKey = "viewMovement.explainShortageOrExcess",
                id = Some("explain-shortage-or-excess"),
                hintKey = Some("viewMovement.explainShortageOrExcess.info")
              )
            )
        }
      }

      "not return a link" when {
        "when the movement has the wrong status" in {
          helper.shortageOrExcessLink(testMovement.copy(eadStatus = MovementEadStatus.Accepted)) mustBe None
        }
      }
    }

  }

// ETFE-2556 will re-enable this link
//  ".printLink" should {
//    "always be present" in {
//      helper.printLink() mustBe
//        Some(
//          link(
//            link = "print",
//            messageKey = "viewMovement.printOrSaveEad",
//            id = Some("print-or-save-ead"),
//            hintKey = Some("viewMovement.printOrSaveEad.info")
//          )
//        )
//    }
//  }
}
