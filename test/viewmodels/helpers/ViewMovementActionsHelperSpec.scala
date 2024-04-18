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
import models.common.DestinationType
import models.requests.DataRequest
import play.api.i18n.Messages
import play.api.test.FakeRequest

import java.time.LocalDate

class ViewMovementActionsHelperSpec extends SpecBase with GetMovementResponseFixtures {

  val helper: ViewMovementActionsHelper = app.injector.instanceOf[ViewMovementActionsHelper]

  implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"))
  implicit lazy val messages: Messages = messages(request)

  ".cancelMovementLink" should {

    val testMovement = getMovementResponseModel.copy(
      eadStatus = "Accepted",
      eadEsad = getMovementResponseModel.eadEsad.copy(upstreamArc = None),
      dateOfDispatch = LocalDate.now.plusWeeks(1)
    )

    "return a link" when {

      "when the movement can be cancelled" in {
        helper.cancelMovementLink(testMovement) mustBe defined
      }
    }

    "not return a link" when {

      "when the movement has an incorrect movement status" in {
        helper.cancelMovementLink(
          testMovement.copy(eadStatus = "Delivered")
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
    val testMovement = getMovementResponseModel.copy(eadStatus = "Accepted")

    "return a link" when {

      "when the movement can be diverted" in {
        helper.changeDestinationLink(testMovement) mustBe defined
      }
    }

    "not return a link" when {

      "when the movement has an incorrect movement status" in {
        helper.changeDestinationLink(testMovement.copy(eadStatus = "Delivered")) mustBe None
      }

    }

  }

  ".alertOrRejectionLink" should {
    val testMovement = getMovementResponseModel.copy(eadStatus = "Accepted")

    "return a link" when {

      "when the movement can be alerted or rejected" in {
        helper.alertOrRejectionLink(testMovement) mustBe defined
      }
    }

    "not return a link" when {

      "when the movement has an incorrect movement status" in {
        helper.alertOrRejectionLink(testMovement.copy(eadStatus = "Delivered")) mustBe None
      }

    }

  }

  ".reportOfReceiptLink" should {
    val testMovement = getMovementResponseModel.copy(eadStatus = "Accepted")

    "return a link" when {

      "when the movement can be receipted" in {
        helper.reportOfReceiptLink(testMovement) mustBe defined
      }
    }

    "not return a link" when {

      "when the movement has an incorrect movement status to be receipted" in {
        helper.reportOfReceiptLink(testMovement.copy(eadStatus = "Delivered")) mustBe None
      }

    }

  }

  ".explainADelayLink" should {
    val testMovement = getMovementResponseModel

    "return a link" when {

      "when an explanation of delay could be submitted" in {
        helper.explainADelayLink(testMovement) mustBe defined
      }
    }
  }

  ".shortageOrExcessLink" when {

    "when the movement is an export" should {
      val testMovement = getMovementResponseModel.copy(
        eadStatus = "Accepted",
        destinationType = DestinationType.Export
      )

      "return a link" when {
        "when a shortage or excess is allowed" in {
          helper.shortageOrExcessLink(testMovement) mustBe defined
        }
      }

      "not return a link" when {
        "when the movement has the wrong status" in {
          helper.shortageOrExcessLink(testMovement.copy(eadStatus = "DeemedExporting")) mustBe None
        }
      }
    }

    "when the movement is not an export" should {
      val testMovement = getMovementResponseModel.copy(
        eadStatus = "Exporting",
        destinationType = DestinationType.TemporaryCertifiedConsignee
      )

      "return a link" when {
        "when a shortage or excess is allowed" in {
          helper.shortageOrExcessLink(testMovement) mustBe defined
        }
      }

      "not return a link" when {
        "when the movement has the wrong status" in {
          helper.shortageOrExcessLink(testMovement.copy(eadStatus = "Accepted")) mustBe None
        }
      }
    }

  }

  ".printLink" should {
    "always be present" in {
      helper.printLink() mustBe defined
    }
  }
}
