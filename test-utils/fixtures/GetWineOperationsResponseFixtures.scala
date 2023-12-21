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

import play.api.libs.json.Json

trait GetWineOperationsResponseFixtures { _: BaseFixtures =>

  val wineOperations = Map(
    "0" -> "Reason 0",
    "1" -> "Reason 1",
    "2" -> "Reason 2",
    "3" -> "Reason 3",
    "4" -> "Reason 4",
    "5" -> "Reason 5",
    "6" -> "Reason 6"
  )

  val wineOperationsJson = Json.toJson(wineOperations)

}
