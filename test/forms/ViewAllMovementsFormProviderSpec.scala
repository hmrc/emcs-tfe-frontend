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

package forms

import base.SpecBase
import models.MovementListSearchOptions
import models.MovementSearchSelectOption.ARC
import models.MovementSortingSelectOption.ArcAscending

class ViewAllMovementsFormProviderSpec extends SpecBase {

  val form = new ViewAllMovementsFormProvider()()

  ".sortBy" should {

    val sortByKey = "sortBy"
    val searchKey = "searchKey"
    val searchValue = "searchValue"

    "bind when the searchKey is present" in {
      val boundForm = form.bind(Map(searchKey -> ARC.code, sortByKey -> ArcAscending.code))
      boundForm.get mustBe MovementListSearchOptions(Some(ARC), None)
    }

    "bind when the searchValue is present" in {
      val boundForm = form.bind(Map(searchValue -> "ARC1", sortByKey -> ArcAscending.code))
      boundForm.get mustBe MovementListSearchOptions(None, Some("ARC1"))
    }

    "remove any non-alphanumerics from the form values" in {
      val boundForm = form.bind(Map(
        searchKey -> "$$arc\\/&.?",
        searchValue -> "ARC1/injecting-viruses!!!!<script>\"alert</script>",
        sortByKey -> "^^arcAsc!?",
      ))
      boundForm.get mustBe MovementListSearchOptions(Some(ARC), Some("ARC1injectingvirusesscriptalertscript"), sortBy = ArcAscending)
    }

    "not bind a value that contains XSS chars" in {

      val boundForm = form.bind(Map(sortByKey -> ArcAscending.code))
      boundForm.errors mustBe Seq.empty
    }
  }
}
