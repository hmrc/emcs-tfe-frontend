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

package uk.gov.hmrc.emcstfefrontend.models

import play.api.libs.json.{JsSuccess, Json}
import uk.gov.hmrc.emcstfefrontend.controllers.routes
import uk.gov.hmrc.emcstfefrontend.fixtures.MovementListFixtures
import uk.gov.hmrc.emcstfefrontend.models.response.emcsTfe.GetMovementListItem
import uk.gov.hmrc.emcstfefrontend.support.UnitSpec


class GetMovementListItemSpec extends UnitSpec with MovementListFixtures {

  "GetMovementListResponse" should {

    "deserialise from JSON" in {
      Json.fromJson[GetMovementListItem](movement1Json) shouldBe JsSuccess(movement1)
    }

    "have a link to view the detailed movement information" in {
      movement1.viewMovementUrl(testErn) shouldBe routes.ViewMovementController.viewMovement(testErn, s"${movement1.arc}")
    }
  }
}