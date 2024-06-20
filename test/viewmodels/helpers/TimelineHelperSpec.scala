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

package viewmodels.helpers

import base.SpecBase
import fixtures.GetMovementResponseFixtures
import models.EventTypes
import models.EventTypes._
import models.common.DestinationType
import models.response.emcsTfe.GetMovementResponse
import models.response.emcsTfe.getMovementHistoryEvents.MovementHistoryEvent
import play.api.i18n.Messages
import play.api.test.FakeRequest

import java.time.{LocalDate, LocalDateTime}

// scalastyle:off magic.number
class TimelineHelperSpec extends SpecBase with GetMovementResponseFixtures {

  implicit lazy val msgs: Messages = messages(FakeRequest())

  val helper: TimelineHelper = app.injector.instanceOf[TimelineHelper]

  ".parseDateTime" must {

    "return a parseable ChRIS EventDate" in {
      val input = "2023-12-02T14:35:07"
      val expected = LocalDateTime.of(2023, 12, 2, 14, 35, 7)

      val response = helper.parseDateTime(input)

      response mustBe expected
    }

    "return a parseable EIS EventDate" in {
      val input = "2023-12-02T14:35:07.000Z"
      val expected = LocalDateTime.of(2023, 12, 2, 14, 35, 7, 0)

      val response = helper.parseDateTime(input)

      response mustBe expected
    }

    "return today's date if unable to parse date/time" in {
      val input = "2023-twelve-02T14:35:07"

      val response = helper.parseDateTime(input)

      response mustBe LocalDate.now.atStartOfDay
    }
  }

  ".getEventTitleKey" must {
    "return the correct message key" when {

      def getKey(
                  eventType: EventTypes,
                  sequenceNumber: Int,
                  messageRole: Int,
                  firstEventTypeInTimeline: Boolean = false,
                  movement: GetMovementResponse = getMovementResponseModel
                ):String = {
        helper.getEventTitleKey(
          MovementHistoryEvent(
            eventType = eventType,
            eventDate = "",
            sequenceNumber = sequenceNumber,
            messageRole = messageRole,
            None,
            isFirstEventTypeInHistory = firstEventTypeInTimeline
          ),
          movement
        )
      }

      "the event is for a movement created" in {
        getKey(IE801, 1, 0, firstEventTypeInTimeline = true) mustBe "movementHistoryEvent.IE801.first.label"
      }
      "the event is for an update to the movement" in {
        getKey(IE801, 2, 0) mustBe "movementHistoryEvent.IE801.further.label"
      }
      "the event is for a reminder to provide a change of destination" in {
        getKey(IE802, 0, 1) mustBe "movementHistoryEvent.IE802.cod.label"
      }
      "the event is for a reminder to provide a report of receipt" in {
        getKey(IE802, 0, 2) mustBe "movementHistoryEvent.IE802.ror.label"
      }
      "the event is for a reminder to provide a destination" in {
        getKey(IE802, 0, 3) mustBe "movementHistoryEvent.IE802.des.label"
      }
      "the event is for a diverted movement" in {
        getKey(IE803, 0, 1) mustBe "movementHistoryEvent.IE803.diverted.label"
      }
      "the event is for a split movement" in {
        getKey(IE803, 0, 2) mustBe "movementHistoryEvent.IE803.split.label"
      }
      "the event is for a movement intercepted" in {
        getKey(IE807, 0, 0) mustBe "movementHistoryEvent.IE807.label"
      }
      "the event is for a cancellation" in {
        getKey(IE810, 0, 0) mustBe "movementHistoryEvent.IE810.label"
      }
      "the event is for a change of destination submission" in {
        getKey(IE813, 0, 0) mustBe "movementHistoryEvent.IE813.label"
      }
      "the event is for a report of receipt submission and an export" in {
        getKey(IE818, 0, 0, movement = getMovementResponseModel.copy(destinationType = DestinationType.Export)) mustBe "movementHistoryEvent.IE818.label.export"
      }
      "the event is for a report of receipt submission and not an export" in {
        DestinationType.values.filterNot(_ == DestinationType.Export).foreach {
          destinationType =>
            getKey(IE818, 0, 0, movement = getMovementResponseModel.copy(destinationType = destinationType)) mustBe "movementHistoryEvent.IE818.label"
        }
      }
      "the event is for a alert/rejection submission" in {
        getKey(IE819, 0, 0) mustBe "movementHistoryEvent.IE819.label"
      }
      "the event is for a movement accepted by customs" in {
        getKey(IE829, 0, 0) mustBe "movementHistoryEvent.IE829.label"
      }
      "the event is for a explanation for delay submission" in {
        getKey(IE837, 0, 0) mustBe "movementHistoryEvent.IE837.label"
      }
      "the event is for a customs rejection of a movement for export" in {
        getKey(IE839, 0, 0) mustBe "movementHistoryEvent.IE839.label"
      }
      "the event is for a first notification of event" in {
        getKey(IE840, 0, 1) mustBe "movementHistoryEvent.IE840.first.label"
      }
      "the event is for a complementary notification of event" in {
        getKey(IE840, 0, 2) mustBe "movementHistoryEvent.IE840.complementary.label"
      }
      "the event is for an explanation for a shortage or excess" in {
        getKey(IE871, 0, 0) mustBe "movementHistoryEvent.IE871.label"
      }
      "the event is for a manual closure response" in {
        getKey(IE881, 0, 0) mustBe "movementHistoryEvent.IE881.label"
      }
      "the event is for a manual closure response of a movement" in {
        getKey(IE905, 0, 0) mustBe "movementHistoryEvent.IE905.label"
      }
    }
  }

  ".getHistoryEventUrl" must {
    "create an event detail url" in {

      val eventDate = "2023-12-02T14:35:07"

      val input = MovementHistoryEvent(
        eventType = IE818,
        eventDate = eventDate,
        sequenceNumber = 1,
        messageRole = 0,
        upstreamArc = None,
        isFirstEventTypeInHistory = true
      )

      /*
       * The event id is calculated by using the hash-code of the event date/time and
       * then applying an unsigned bit shift to the right of 1. The unsigned bit
       * shift ensures that we don't get negative event id's, as these event id's
       * are to be displayed in the URL to locate the correct movement history
       * event when drilling down for further details of the history event.
       */
      val expectedEventId = eventDate.hashCode >>> 1

      val response = helper.getHistoryEventUrl(input)

      response mustBe s"event/$expectedEventId/report-receipt-submitted"
    }
  }
}
