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

package viewmodels

import base.SpecBase
import uk.gov.hmrc.govukfrontend.views.Aliases.{Text, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Key, SummaryList, SummaryListRow}

class SummaryListHelperSpec extends SpecBase {
  trait Test {
    def list: Seq[(String, String)]

    private val helper: SummaryListHelper = new SummaryListHelper()

    val summaryList: SummaryList = helper.render(list)
  }

  "SummaryListHelper" when {
    "rendering" must {
      "render a list" when {
        "the list has items in it" in new Test {
          override def list: Seq[(String, String)] = Seq(("key", "value"))

          summaryList.rows mustBe Seq(SummaryListRow(
            key = Key(content = Text("key")),
            value = Value(content = Text("value"))
          ))
        }
      }
      "render an empty list" when {
        "the list has no items in it" in new Test {
          override def list: Seq[(String, String)] = Seq()

          summaryList.rows mustBe Seq()
        }
      }
    }
  }
}
