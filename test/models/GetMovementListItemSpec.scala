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
import models.response.emcsTfe.GetMovementListItem
import play.api.libs.json.{JsSuccess, Json}


class GetMovementListItemSpec extends SpecBase with MovementListFixtures {

  "GetMovementListResponse" must {

    "deserialise from JSON" in {
      Json.fromJson[GetMovementListItem](movement1Json) mustBe JsSuccess(movement1)
    }

    "have a link to view the detailed movement information" in {
      movement1.viewMovementUrl(testErn) mustBe routes.ViewMovementController.viewMovementOverview(testErn, s"${movement1.arc}")
    }
  }
}