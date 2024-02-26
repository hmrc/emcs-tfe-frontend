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

/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import base.SpecBase
import controllers.routes
import fixtures.MovementListFixtures
import fixtures.messages.ViewAllMovementsMessages.English
import models.response.emcsTfe.GetMovementListItem
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.json.{JsSuccess, Json}
import uk.gov.hmrc.govukfrontend.views.html.components.Text
import viewmodels.govuk.TagFluency


class GetMovementListItemSpec extends SpecBase with MovementListFixtures with TagFluency {

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(English.lang))

  "GetMovementListResponse" must {

    "reads" should {

      "deserialise from JSON" in {

        Json.fromJson[GetMovementListItem](movement1Json) mustBe JsSuccess(movement1)
      }

      "have a link to view the detailed movement information" in {

        movement1.viewMovementUrl(testErn) mustBe routes.ViewMovementController.viewMovementOverview(testErn, s"${movement1.arc}")
      }
    }

    "formattedDateOfDispatch" should {

      "have the correctly formatted date" in {

        movement1.formattedDateOfDispatch mustBe "26 January 2009"
      }
    }

    "statusTag" should {

      "return an blue tag when status is Accepted" in {

        movement1.copy(movementStatus = "Accepted").statusTag() mustBe TagViewModel(Text("Accepted")).blue().withCssClass("govuk-!-margin-top-5")
      }

      "return an green tag Deemed exported, Diverted or Exporting" in {

        movement1.copy(movementStatus = "DeemedExported").statusTag() mustBe TagViewModel(Text("Deemed exported")).green().withCssClass("govuk-!-margin-top-5")
        movement1.copy(movementStatus = "Diverted").statusTag() mustBe TagViewModel(Text("Diverted")).green().withCssClass("govuk-!-margin-top-5")
        movement1.copy(movementStatus = "Exporting").statusTag() mustBe TagViewModel(Text("Exporting")).green().withCssClass("govuk-!-margin-top-5")
      }

      "return an orange tag Partially refused, Refused or Rejected" in {

        movement1.copy(movementStatus = "PartiallyRefused").statusTag() mustBe TagViewModel(Text("Partially refused")).orange().withCssClass("govuk-!-margin-top-5")
        movement1.copy(movementStatus = "Refused").statusTag() mustBe TagViewModel(Text("Refused")).orange().withCssClass("govuk-!-margin-top-5")
        movement1.copy(movementStatus = "Rejected").statusTag() mustBe TagViewModel(Text("Rejected")).orange().withCssClass("govuk-!-margin-top-5")
      }

      "return an purple tag Cancelled, Manually closed, Replaced or Stopped" in {

        movement1.copy(movementStatus = "Cancelled").statusTag() mustBe TagViewModel(Text("Cancelled")).purple().withCssClass("govuk-!-margin-top-5")
        movement1.copy(movementStatus = "ManuallyClosed").statusTag() mustBe TagViewModel(Text("Manually closed")).purple().withCssClass("govuk-!-margin-top-5")
        movement1.copy(movementStatus = "Replaced").statusTag() mustBe TagViewModel(Text("Replaced")).purple().withCssClass("govuk-!-margin-top-5")
        movement1.copy(movementStatus = "Stopped").statusTag() mustBe TagViewModel(Text("Stopped")).purple().withCssClass("govuk-!-margin-top-5")
      }

      "return an green tag is Delivered" in {

        movement1.copy(movementStatus = "Delivered").statusTag() mustBe TagViewModel(Text("Delivered")).withCssClass("govuk-!-margin-top-5")
      }
    }
  }
}