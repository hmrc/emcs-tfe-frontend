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

package viewmodels

import models.EventTypes
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.time.LocalDateTime

class TimelineEventSpec extends AnyFlatSpec with Matchers {

  "id" should "replace spaces with dashes and convert to lowercase" in {
    val event = TimelineEvent(EventTypes.IE801, "Test Title", LocalDateTime.now(), "/test/url")
    event.id should be ("test-title")
  }

  it should "handle titles with no spaces" in {
    val event = TimelineEvent(EventTypes.IE801, "TestTitle", LocalDateTime.now(), "/test/url")
    event.id should be ("testtitle")
  }

  it should "handle empty titles" in {
    val event = TimelineEvent(EventTypes.IE801, "", LocalDateTime.now(), "/test/url")
    event.id should be ("")
  }

  it should "handle titles with multiple consecutive spaces" in {
    val event = TimelineEvent(EventTypes.IE801, "Test  Title", LocalDateTime.now(), "/test/url")
    event.id should be ("test--title")
  }

  it should "handle titles with multiple words" in {
    val event = TimelineEvent(EventTypes.IE801, "Test this title", LocalDateTime.now(), "/test/url")
    event.id should be ("test-this-title")
  }
}