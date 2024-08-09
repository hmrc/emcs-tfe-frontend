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
import models.MovementFilterDirectionOption._
import models.MovementFilterStatusOption.ChooseStatus
import models.MovementFilterUndischargedOption.Undischarged
import models.MovementSearchSelectOption.ARC
import models.MovementSortingSelectOption.Newest
import models.{MovementFilterStatusOption, MovementListSearchOptions, MovementSortingSelectOption}
import play.api.data.FormError

import java.time.LocalDate

class ViewAllMovementsFormProviderSpec extends SpecBase {

  val form = new ViewAllMovementsFormProvider()()
  val sortBy = "sortBy"
  val searchKey = "searchKey"
  val searchValue = "searchValue"

  // filters
  def traderRole(i: Int) = s"traderRole[$i]"

  def undischarged(i: Int) = s"undischargedMovements[$i]"

  val status = s"movementStatus"
  val exciseProductCode = "exciseProductCode"
  val countryOfOrigin = "countryOfOrigin"
  val dateOfDispatchFrom = "dateOfDispatchFrom"
  val dateOfDispatchTo = "dateOfDispatchTo"
  val dateOfReceiptFrom = "dateOfReceiptFrom"
  val dateOfReceiptTo = "dateOfReceiptTo"

  ".sortBy" should {

    MovementSortingSelectOption.values.foreach {
      value =>
        s"bind when $value is present" in {
          val boundForm = form.bind(Map(
            sortBy -> value.code
          ))

          boundForm.get mustBe MovementListSearchOptions(sortBy = Some(value))
        }
    }

    "not bind an invalid value" in {
      val boundForm = form.bind(Map(
        sortBy -> "BEANS",
      ))

      boundForm.errors mustBe List(FormError(sortBy, List("error.invalid")))
    }
  }

  ".searchKey" should {

    "bind" in {
      val boundForm = form.bind(Map(searchKey -> ARC.code))
      boundForm.get mustBe MovementListSearchOptions(Some(ARC))
    }

    "remove any alphanumeric characters from the form values" in {
      val boundForm = form.bind(Map(
        searchKey -> "$$ ar ?c\\/&. ?"
      ))
      boundForm.get mustBe MovementListSearchOptions(searchKey = Some(ARC))
    }
  }

  ".searchValue" should {

    "bind" in {
      val boundForm = form.bind(Map(searchValue -> "beans"))
      boundForm.get mustBe MovementListSearchOptions(searchValue = Some("beans"))
    }

    "bind leading and trailing spaces by removing them" in {
      val boundForm = form.bind(Map(searchValue -> "   beans  "))
      boundForm.get mustBe MovementListSearchOptions(searchValue = Some("beans"))
    }

    "return an error when the value is invalid" in {
      val boundForm = form.bind(Map(
        searchValue -> "<script>alert('hi')</script>",
        sortBy -> Newest.code
      ))
      boundForm.errors mustBe List(FormError(searchValue, List("error.invalidCharacter"), List(XSS_REGEX)))
    }
  }

  ".traderRole" should {

    "bind when Goods in is present" in {
      val boundForm = form.bind(Map(
        traderRole(0) -> GoodsIn.code
      ))

      boundForm.get mustBe MovementListSearchOptions(traderRole = Some(GoodsIn))
    }

    "bind when Goods out is present" in {
      val boundForm = form.bind(Map(
        traderRole(0) -> GoodsOut.code
      ))

      boundForm.get mustBe MovementListSearchOptions(traderRole = Some(GoodsOut))
    }

    "bind when both Goods in and Goods out are present" in {
      val boundForm = form.bind(Map(
        traderRole(0) -> GoodsIn.code,
        traderRole(1) -> GoodsOut.code
      ))

      boundForm.get mustBe MovementListSearchOptions(traderRole = Some(All))
    }

    "not bind an invalid value" in {
      val boundForm = form.bind(Map(
        sortBy -> Newest.code,
        traderRole(0) -> "BEANS"
      ))

      boundForm.errors mustBe List(FormError(traderRole(0), List("error.invalid")))
    }
  }

  ".undischarged" should {

    "bind when Undischarged is present" in {
      val boundForm = form.bind(Map(
        undischarged(0) -> Undischarged.code
      ))

      boundForm.get mustBe MovementListSearchOptions(undischargedMovements = Some(Undischarged))
    }

    "bind an invalid value to None" in {
      val boundForm = form.bind(Map(
        undischarged(0) -> "BEANS"
      ))

      boundForm.value mustBe None
    }
  }

  ".status" should {
    MovementFilterStatusOption.values.filterNot(_ == ChooseStatus).foreach {
      value =>
        s"bind when $value is present" in {
          val boundForm = form.bind(Map(
            status -> value.code
          ))

          boundForm.get mustBe MovementListSearchOptions(movementStatus = Some(value))
        }
    }

    "bind ChooseStatus to None" in {
      val boundForm = form.bind(Map(
        status -> ChooseStatus.code
      ))

      boundForm.get mustBe MovementListSearchOptions(movementStatus = None)
    }

    "not bind an invalid value" in {
      val boundForm = form.bind(Map(
        status -> "BEANS"
      ))

      boundForm.errors mustBe List(FormError(status, List("error.invalid")))
    }
  }

  ".exciseProductCode" should {

    "bind" in {
      val boundForm = form.bind(Map(exciseProductCode -> "beans"))
      boundForm.get mustBe MovementListSearchOptions(exciseProductCode = Some("beans"))
    }

    "remove any non-alphanumeric characters from the form values" in {
      val boundForm = form.bind(Map(
        exciseProductCode -> "$$ bea ?ns\\/&. ?"
      ))
      boundForm.get mustBe MovementListSearchOptions(exciseProductCode = Some("beans"))
    }
  }

  ".countryOfOrigin" should {

    "bind" in {
      val boundForm = form.bind(Map(countryOfOrigin -> "beans"))
      boundForm.get mustBe MovementListSearchOptions(countryOfOrigin = Some("beans"))
    }

    "remove any non-alphanumeric characters from the form values" in {
      val boundForm = form.bind(Map(
        countryOfOrigin -> "$$ bea ?ns\\/&. ?"
      ))
      boundForm.get mustBe MovementListSearchOptions(countryOfOrigin = Some("beans"))
    }
  }

  testDate("dateOfDispatchFrom")
  testDate("dateOfDispatchTo")
  testDate("dateOfReceiptFrom")
  testDate("dateOfReceiptTo")

  //noinspection ScalaStyle
  def testDate(dateKey: String): Unit = {
    val date: LocalDate = LocalDate.now()

    def formAnswersMap(
                        day: String = date.getDayOfMonth.toString,
                        month: String = date.getMonthValue.toString,
                        year: String = date.getYear.toString
                      ): Map[String, String] =
      Map(
        s"$dateKey.day" -> day,
        s"$dateKey.month" -> month,
        s"$dateKey.year" -> year
      )

    s".$dateKey" should {
      "bind valid data" in {
        val data = formAnswersMap(
          day = date.getDayOfMonth.toString,
          month = date.getMonthValue.toString,
          year = date.getYear.toString
        )

        val result = form.bind(data)

        result.value.value.toString must include(date.toString)
        result.errors mustBe empty
      }

      "return an error" when {
        "the date is invalid" in {

          val data = formAnswersMap(day = "1000", month = "1000", year = "1000")

          val expectedResult = Seq(FormError(dateKey, s"viewAllMovements.filters.$dateKey.error.invalid"))

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedResult
        }

        "the date except the day field is not supplied" in {
          val data = formAnswersMap(day = "")

          val expectedResult = Seq(FormError(dateKey, s"viewAllMovements.filters.$dateKey.error.required", List("day")))

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedResult
        }

        "the date except the month field is not supplied" in {
          val data = formAnswersMap(month = "")

          val expectedResult = Seq(FormError(dateKey, s"viewAllMovements.filters.$dateKey.error.required", List("month")))

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedResult
        }

        "the date except the year field is not supplied" in {
          val data = formAnswersMap(year = "")

          val expectedResult = Seq(FormError(dateKey, s"viewAllMovements.filters.$dateKey.error.required", List("year")))

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedResult
        }

        "only the day is entered" in {
          val data = formAnswersMap(month = "", year = "")

          val expectedResult = Seq(FormError(dateKey, s"viewAllMovements.filters.$dateKey.error.required.two", List("month", "year")))

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedResult
        }

        "only the month is entered" in {
          val data = formAnswersMap(day = "", year = "")

          val expectedResult = Seq(FormError(dateKey, s"viewAllMovements.filters.$dateKey.error.required.two", List("day", "year")))

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedResult
        }

        "only the year is entered" in {
          val data = formAnswersMap(day = "", month = "")

          val expectedResult = Seq(FormError(dateKey, s"viewAllMovements.filters.$dateKey.error.required.two", List("day", "month")))

          val actualResult = form.bind(data)

          actualResult.errors mustBe expectedResult
        }
      }
    }
  }
}
