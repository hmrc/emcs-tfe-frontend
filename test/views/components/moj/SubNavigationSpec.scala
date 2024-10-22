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

package views.components.moj

import base.SpecBase
import fixtures.messages.BaseEnglish
import org.jsoup.Jsoup
import viewmodels.Overview
import views.html.components.moj.subNavigation

class SubNavigationSpec extends SpecBase {

  lazy val subNav: subNavigation = app.injector.instanceOf[subNavigation]

  "subNavigation" when {

    Seq(BaseEnglish).foreach { messagesForLanguage =>

      implicit val msgs = messages(Seq(messagesForLanguage.lang))

      s"rendering in lang code of '${messagesForLanguage.lang.code}" must {

        "have the correct aria-label for the nav" in {

          val document = Jsoup.parse(subNav(Seq(), Overview).toString())
          document.select("nav").attr("aria-label") mustBe messagesForLanguage.subNavAriaLabel
        }
      }
    }
  }
}
