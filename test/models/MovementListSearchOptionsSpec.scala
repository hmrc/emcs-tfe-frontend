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

package models

import base.SpecBase
import models.MovementListSearchOptions.CHOOSE_PRODUCT_CODE
import models.MovementSearchSelectOption.{ARC, ERN, Transporter}
import models.MovementSortingSelectOption.{ArcDescending, Newest, Oldest}

import java.time.LocalDate

class MovementListSearchOptionsSpec extends SpecBase {

  val testDate: LocalDate = LocalDate.now()

  "MovementListSearchOptions" must {

    "construct with default options" in {

      val expectedResult = MovementListSearchOptions(
        sortBy = None,
        index = 1,
        maxRows = 10
      )

      val actualResult = MovementListSearchOptions()

      actualResult mustBe expectedResult
    }

    "be able to be constructed by a QueryStringBinder" when {

      "no query params are supplied" in {

        val expectedResult = Some(Right(MovementListSearchOptions()))
        val actualResult = MovementListSearchOptions.queryStringBinder.bind("search", Map())

        actualResult mustBe expectedResult
      }

      "all query parameters are supplied" in {

        val expectedResult = Some(Right(MovementListSearchOptions(
          searchKey = Some(MovementSearchSelectOption.values.head),
          searchValue = Some("beans"),
          sortBy = Some(ArcDescending),
          traderRole = Some(MovementFilterDirectionOption.values.head),
          undischargedMovements = Some(MovementFilterUndischargedOption.values.head),
          movementStatus = Some(MovementFilterStatusOption.values.head),
          exciseProductCode = Some("testEpc"),
          countryOfOrigin = Some("testCountry"),
          dateOfDispatchFrom = Some(testDate),
          dateOfDispatchTo = Some(testDate),
          dateOfReceiptFrom = Some(testDate),
          dateOfReceiptTo = Some(testDate),
          index = 5,
          maxRows = MovementListSearchOptions.DEFAULT_MAX_ROWS
        )))

        val actualResult = MovementListSearchOptions.queryStringBinder.bind("search", Map(
          "searchKey" -> Seq(MovementSearchSelectOption.values.head.code),
          "searchValue" -> Seq("beans"),
          "sortBy" -> Seq(ArcDescending.code),
          "traderRole" -> Seq(MovementFilterDirectionOption.values.head.code),
          "undischargedMovements" -> Seq(MovementFilterUndischargedOption.values.head.code),
          "movementStatus" -> Seq(MovementFilterStatusOption.values.head.code),
          "exciseProductCode" -> Seq("testEpc"),
          "countryOfOrigin" -> Seq("testCountry"),
          "dateOfDispatchFrom" -> Seq(MovementListSearchOptions.localDateToString(testDate)),
          "dateOfDispatchTo" -> Seq(MovementListSearchOptions.localDateToString(testDate)),
          "dateOfReceiptFrom" -> Seq(MovementListSearchOptions.localDateToString(testDate)),
          "dateOfReceiptTo" -> Seq(MovementListSearchOptions.localDateToString(testDate)),
          "index" -> Seq("5")
        ))

        actualResult mustBe expectedResult
      }

      "some query parameters are supplied" in {

        val expectedResult = Some(Right(MovementListSearchOptions(
          index = 5,
          maxRows = MovementListSearchOptions.DEFAULT_MAX_ROWS
        )))

        val actualResult = MovementListSearchOptions.queryStringBinder.bind("search", Map(
          "index" -> Seq("5")
        ))

        actualResult mustBe expectedResult
      }

      "invalid query parameters are supplied" in {
        val expectedResult = Some(Right(MovementListSearchOptions(
          traderRole = None,
          undischargedMovements = None,
          movementStatus = None
        )))

        val actualResult = MovementListSearchOptions.queryStringBinder.bind("search", Map(
          "traderRole" -> Seq("ADMIN"),
          "undischargedMovements" -> Seq("YES"),
          "movementStatus" -> Seq("RunningLate")
        ))

        actualResult mustBe expectedResult
      }
    }

    "unbind QueryString to URL format" in {

      val expectedResult = (s"searchKey=${MovementSearchSelectOption.values.head.code}&" +
        s"searchValue=beans&" +
        s"sortBy=${ArcDescending.code}&" +
        s"index=5&" +
        s"traderRole=${MovementFilterDirectionOption.values.head.code}&" +
        s"undischargedMovements=${MovementFilterUndischargedOption.values.head.code}&" +
        s"movementStatus=${MovementFilterStatusOption.values.head.code}&" +
        s"exciseProductCode=testEpc&" +
        s"countryOfOrigin=testCountry&" +
        s"dateOfDispatchFrom=${MovementListSearchOptions.localDateToString(testDate)}&" +
        s"dateOfDispatchTo=${MovementListSearchOptions.localDateToString(testDate)}&" +
        s"dateOfReceiptFrom=${MovementListSearchOptions.localDateToString(testDate)}&" +
        s"dateOfReceiptTo=${MovementListSearchOptions.localDateToString(testDate)}")
        .replace("/", "%2F")
        .replace(" ", "+")

      val actualResult = MovementListSearchOptions.queryStringBinder.unbind("search", MovementListSearchOptions(
        searchKey = Some(MovementSearchSelectOption.values.head),
        searchValue = Some("beans"),
        sortBy = Some(ArcDescending),
        traderRole = Some(MovementFilterDirectionOption.values.head),
        undischargedMovements = Some(MovementFilterUndischargedOption.values.head),
        movementStatus = Some(MovementFilterStatusOption.values.head),
        exciseProductCode = Some("testEpc"),
        countryOfOrigin = Some("testCountry"),
        dateOfDispatchFrom = Some(testDate),
        dateOfDispatchTo = Some(testDate),
        dateOfReceiptFrom = Some(testDate),
        dateOfReceiptTo = Some(testDate),
        index = 5,
        maxRows = MovementListSearchOptions.DEFAULT_MAX_ROWS
      ))

      actualResult mustBe expectedResult
    }

    "have the correct downstreamQueryParams values" when {

      "using default values" in {

        val expectedResult: Seq[(String, String)] = Seq(
          "search.sortOrder" -> Newest.sortOrder,
          "search.sortField" -> Newest.sortField,
          "search.startPosition" -> "0",
          "search.maxRows" -> "10"
        )

        val actualResult = MovementListSearchOptions().downstreamQueryParams

        actualResult mustBe expectedResult
      }

      "given values" in {

        val expectedResult: Seq[(String, String)] = Seq(
          "search.transporterTraderName" -> "robots in disguise",
          "search.dateOfDispatchFrom" -> MovementListSearchOptions.localDateToString(testDate),
          "search.dateOfDispatchTo" -> MovementListSearchOptions.localDateToString(testDate),
          "search.dateOfReceiptFrom" -> MovementListSearchOptions.localDateToString(testDate),
          "search.dateOfReceiptTo" -> MovementListSearchOptions.localDateToString(testDate),
          "search.sortOrder" -> Oldest.sortOrder,
          "search.sortField" -> Oldest.sortField,
          "search.startPosition" -> "25",
          "search.maxRows" -> "5"
        )

        val actualResult = MovementListSearchOptions(
          searchKey = Some(Transporter),
          searchValue = Some("robots in disguise"),
          sortBy = Some(Oldest),
          dateOfDispatchFrom = Some(testDate),
          dateOfDispatchTo = Some(testDate),
          dateOfReceiptFrom = Some(testDate),
          dateOfReceiptTo = Some(testDate),
          index = 6,
          maxRows = 5
        ).downstreamQueryParams

        actualResult mustBe expectedResult
      }
    }

    "startingPosition must be correctly calculated from the index and maxRows" when {

      "maxRows is 10" when {

        "index is 1" in {

          val expectedResult = 0

          val actualResult = MovementListSearchOptions(
            index = 1,
            maxRows = 10
          ).startingPosition

          actualResult mustBe expectedResult
        }

        "index is 2" in {

          val expectedResult = 10

          val actualResult = MovementListSearchOptions(
            index = 2,
            maxRows = 10
          ).startingPosition

          actualResult mustBe expectedResult
        }

        "index is 50" in {

          val expectedResult = 490

          val actualResult = MovementListSearchOptions(
            index = 50,
            maxRows = 10
          ).startingPosition

          actualResult mustBe expectedResult
        }
      }

      "maxRows is 5" when {

        "index is 1" in {

          val expectedResult = 0

          val actualResult = MovementListSearchOptions(
            index = 1,
            maxRows = 5
          ).startingPosition

          actualResult mustBe expectedResult
        }

        "index is 2" in {

          val expectedResult = 5

          val actualResult = MovementListSearchOptions(
            index = 2,
            maxRows = 5
          ).startingPosition

          actualResult mustBe expectedResult
        }

        "index is 50" in {

          val expectedResult = 245

          val actualResult = MovementListSearchOptions(
            index = 50,
            maxRows = 5
          ).startingPosition

          actualResult mustBe expectedResult
        }
      }

      "maxRows is 30" when {

        "index is 1" in {

          val expectedResult = 0

          val actualResult = MovementListSearchOptions(
            index = 1,
            maxRows = 5
          ).startingPosition

          actualResult mustBe expectedResult
        }

        "index is 2" in {

          val expectedResult = 30

          val actualResult = MovementListSearchOptions(
            index = 2,
            maxRows = 30
          ).startingPosition

          actualResult mustBe expectedResult
        }

        "index is 50" in {

          val expectedResult = 1470

          val actualResult = MovementListSearchOptions(
            index = 50,
            maxRows = 30
          ).startingPosition

          actualResult mustBe expectedResult
        }
      }
    }

    "apply" should {

      "return a valid MovementListSearchOption" in {

        val expectedResult = MovementListSearchOptions(
          searchKey = Some(ARC),
          searchValue = Some("ARC123"),
          sortBy = Some(Oldest),
          traderRole = Some(MovementFilterDirectionOption.GoodsIn),
          undischargedMovements = Some(MovementFilterUndischargedOption.Undischarged),
          movementStatus = Some(MovementFilterStatusOption.Active),
          exciseProductCode = Some("abc"),
          countryOfOrigin = Some("GB"),
          dateOfDispatchFrom = Some(testDate),
          dateOfDispatchTo = Some(testDate),
          dateOfReceiptFrom = Some(testDate),
          dateOfReceiptTo = Some(testDate),
          index = 1,
          maxRows = 10
        )

        val actualResult = MovementListSearchOptions.apply(
          searchKey = Some(ARC.code),
          searchValue = Some("ARC123"),
          sortBy = Some(Oldest),
          traderRoleOptions = Set(MovementFilterDirectionOption.GoodsIn),
          undischargedMovementsOptions = Set(MovementFilterUndischargedOption.Undischarged),
          movementStatusOption = Some(MovementFilterStatusOption.Active),
          exciseProductCodeOption = Some("abc"),
          countryOfOriginOption = Some("GB"),
          dateOfDispatchFrom = Some(testDate),
          dateOfDispatchTo = Some(testDate),
          dateOfReceiptFrom = Some(testDate),
          dateOfReceiptTo = Some(testDate)
        )

        actualResult mustBe expectedResult
      }

      "filter out CHOOSE_PRODUCT_CODE" in {
        val expectedResult = MovementListSearchOptions(
          searchKey = Some(ARC),
          searchValue = Some("ARC123"),
          sortBy = Some(Oldest),
          traderRole = Some(MovementFilterDirectionOption.GoodsIn),
          undischargedMovements = Some(MovementFilterUndischargedOption.Undischarged),
          movementStatus = Some(MovementFilterStatusOption.Active),
          exciseProductCode = None,
          countryOfOrigin = Some("GB"),
          dateOfDispatchFrom = Some(testDate),
          dateOfDispatchTo = Some(testDate),
          dateOfReceiptFrom = Some(testDate),
          dateOfReceiptTo = Some(testDate),
          index = 1,
          maxRows = 10
        )

        val actualResult = MovementListSearchOptions.apply(
          searchKey = Some(ARC.code),
          searchValue = Some("ARC123"),
          sortBy = Some(Oldest),
          traderRoleOptions = Set(MovementFilterDirectionOption.GoodsIn),
          undischargedMovementsOptions = Set(MovementFilterUndischargedOption.Undischarged),
          movementStatusOption = Some(MovementFilterStatusOption.Active),
          exciseProductCodeOption = Some(CHOOSE_PRODUCT_CODE.code),
          countryOfOriginOption = Some("GB"),
          dateOfDispatchFrom = Some(testDate),
          dateOfDispatchTo = Some(testDate),
          dateOfReceiptFrom = Some(testDate),
          dateOfReceiptTo = Some(testDate)
        )

        actualResult mustBe expectedResult
      }

      "filter out Choose status" in {
        val expectedResult = MovementListSearchOptions(
          searchKey = Some(ARC),
          searchValue = Some("ARC123"),
          sortBy = Some(Oldest),
          traderRole = Some(MovementFilterDirectionOption.GoodsIn),
          undischargedMovements = Some(MovementFilterUndischargedOption.Undischarged),
          movementStatus = None,
          exciseProductCode = Some("abc"),
          countryOfOrigin = Some("GB"),
          dateOfDispatchFrom = Some(testDate),
          dateOfDispatchTo = Some(testDate),
          dateOfReceiptFrom = Some(testDate),
          dateOfReceiptTo = Some(testDate),
          index = 1,
          maxRows = 10
        )

        val actualResult = MovementListSearchOptions.apply(
          searchKey = Some(ARC.code),
          searchValue = Some("ARC123"),
          sortBy = Some(Oldest),
          traderRoleOptions = Set(MovementFilterDirectionOption.GoodsIn),
          undischargedMovementsOptions = Set(MovementFilterUndischargedOption.Undischarged),
          movementStatusOption = Some(MovementFilterStatusOption.ChooseStatus),
          exciseProductCodeOption = Some("abc"),
          countryOfOriginOption = Some("GB"),
          dateOfDispatchFrom = Some(testDate),
          dateOfDispatchTo = Some(testDate),
          dateOfReceiptFrom = Some(testDate),
          dateOfReceiptTo = Some(testDate)
        )

        actualResult mustBe expectedResult
      }

      "filter out Choose country" in {
        val expectedResult = MovementListSearchOptions(
          searchKey = Some(ARC),
          searchValue = Some("ARC123"),
          sortBy = Some(Oldest),
          traderRole = Some(MovementFilterDirectionOption.GoodsIn),
          undischargedMovements = Some(MovementFilterUndischargedOption.Undischarged),
          movementStatus = Some(MovementFilterStatusOption.Active),
          exciseProductCode = Some("abc"),
          countryOfOrigin = None,
          dateOfDispatchFrom = Some(testDate),
          dateOfDispatchTo = Some(testDate),
          dateOfReceiptFrom = Some(testDate),
          dateOfReceiptTo = Some(testDate),
          index = 1,
          maxRows = 10
        )

        val actualResult = MovementListSearchOptions.apply(
          searchKey = Some(ARC.code),
          searchValue = Some("ARC123"),
          sortBy = Some(Oldest),
          traderRoleOptions = Set(MovementFilterDirectionOption.GoodsIn),
          undischargedMovementsOptions = Set(MovementFilterUndischargedOption.Undischarged),
          movementStatusOption = Some(MovementFilterStatusOption.Active),
          exciseProductCodeOption = Some("abc"),
          countryOfOriginOption = Some(MovementListSearchOptions.CHOOSE_COUNTRY.code),
          dateOfDispatchFrom = Some(testDate),
          dateOfDispatchTo = Some(testDate),
          dateOfReceiptFrom = Some(testDate),
          dateOfReceiptTo = Some(testDate)
        )

        actualResult mustBe expectedResult
      }
    }

    "unapply" should {

      "return the string values for MovementListSearchOptions" in {

        val expectedResult = Some((
          Some("otherTraderId"),
          Some("ERN123456"),
          Some(Oldest),
          Set(MovementFilterDirectionOption.GoodsOut),
          Set(MovementFilterUndischargedOption.Undischarged),
          Some(MovementFilterStatusOption.Active),
          Some("abc"),
          Some("GB"),
          Some(testDate),
          Some(testDate),
          Some(testDate),
          Some(testDate)
        ))

        val actualResult = MovementListSearchOptions.unapply(
          MovementListSearchOptions(
            searchKey = Some(ERN),
            searchValue = Some("ERN123456"),
            sortBy = Some(Oldest),
            traderRole = Some(MovementFilterDirectionOption.GoodsOut),
            undischargedMovements = Some(MovementFilterUndischargedOption.Undischarged),
            movementStatus = Some(MovementFilterStatusOption.Active),
            exciseProductCode = Some("abc"),
            countryOfOrigin = Some("GB"),
            dateOfDispatchFrom = Some(testDate),
            dateOfDispatchTo = Some(testDate),
            dateOfReceiptFrom = Some(testDate),
            dateOfReceiptTo = Some(testDate),
            index = 1,
            maxRows = 10
          )
        )

        actualResult mustBe expectedResult
      }
    }

    "getSearchFields" must {
      "return Some(searchKey -> searchValue)" when {
        "searchKey and searchValue are both defined" in {
          val result = MovementListSearchOptions(
            searchKey = Some(MovementSearchSelectOption.ARC),
            searchValue = Some("beans")
          ).getSearchFields

          result mustBe Some(s"search.${MovementSearchSelectOption.ARC}" -> "beans")
        }
      }
      "return None" when {
        "searchKey is not defined" in {
          val result = MovementListSearchOptions(
            searchKey = None,
            searchValue = Some("beans")
          ).getSearchFields

          result mustBe None
        }
        "searchValue is not defined" in {
          val result = MovementListSearchOptions(
            searchKey = Some(MovementSearchSelectOption.ARC),
            searchValue = None
          ).getSearchFields

          result mustBe None
        }
      }
    }

    "getTraderRole" must {

      val validValues: Seq[MovementFilterDirectionOption] = Seq(MovementFilterDirectionOption.GoodsIn, MovementFilterDirectionOption.GoodsOut)
      val invalidValues: Seq[MovementFilterDirectionOption] = MovementFilterDirectionOption.values.filterNot(validValues.contains)

      validValues.foreach {
        value =>
          s"return Some(search.traderRole -> $value)" when {
            s"traderRole is Some($value)" in {
              val result = MovementListSearchOptions(
                traderRole = Some(value)
              ).getTraderRole

              result mustBe Some("search.traderRole" -> value.toString)
            }
          }
      }

      "return None" when {
        invalidValues.foreach {
          value =>
            s"traderRole is Some($value)" in {
              val result = MovementListSearchOptions(
                traderRole = Some(value)
              ).getTraderRole

              result mustBe None
            }
        }
        "traderRole is None" in {
          val result = MovementListSearchOptions(
            traderRole = None
          ).getTraderRole

          result mustBe None
        }
      }
    }

    "getUndischargedMovementsFlag" must {

      val validValues: Seq[MovementFilterUndischargedOption] = Seq[MovementFilterUndischargedOption](MovementFilterUndischargedOption.Undischarged)
      val invalidValues: Seq[MovementFilterUndischargedOption] = MovementFilterUndischargedOption.values.filterNot(validValues.contains)

      validValues.foreach {
        value =>
          s"return Some(search.undischargedMovements -> $value)" when {
            s"undischargedMovements is Some($value)" in {
              val result = MovementListSearchOptions(
                undischargedMovements = Some(value)
              ).getUndischargedMovementsFlag

              result mustBe Some("search.undischargedMovements" -> value.toString)
            }
          }
      }

      "return None" when {
        invalidValues.foreach {
          value =>
            s"undischargedMovements is Some($value)" in {
              val result = MovementListSearchOptions(
                undischargedMovements = Some(value)
              ).getUndischargedMovementsFlag

              result mustBe None
            }
        }
        "undischargedMovements is None" in {
          val result = MovementListSearchOptions(
            undischargedMovements = None
          ).getUndischargedMovementsFlag

          result mustBe None
        }
      }
    }

    "getMovementStatus" must {

      val invalidValues: Seq[MovementFilterStatusOption] = Seq(MovementFilterStatusOption.ChooseStatus)
      val validValues: Seq[MovementFilterStatusOption] = MovementFilterStatusOption.values.filterNot(invalidValues.contains)

      validValues.foreach {
        value =>
          s"return Some(search.movementStatus -> $value)" when {
            s"movementStatus is Some($value)" in {
              val result = MovementListSearchOptions(
                movementStatus = Some(value)
              ).getMovementStatus

              result mustBe Some("search.movementStatus" -> value.toString)
            }
          }
      }

      "return None" when {
        invalidValues.foreach {
          value =>
            s"movementStatus is Some($value)" in {
              val result = MovementListSearchOptions(
                movementStatus = Some(value)
              ).getMovementStatus

              result mustBe None
            }
        }
        "movementStatus is None" in {
          val result = MovementListSearchOptions(
            movementStatus = None
          ).getMovementStatus

          result mustBe None
        }
      }
    }

    "getEpc" must {
      s"return Some(search.exciseProductCode -> value)" when {
        s"exciseProductCode is not ${MovementListSearchOptions.CHOOSE_PRODUCT_CODE.code}" in {
          val result = MovementListSearchOptions(
            exciseProductCode = Some("value")
          ).getEpc

          result mustBe Some("search.exciseProductCode" -> "value")
        }
      }

      "return None" when {
        s"exciseProductCode is Some(${MovementListSearchOptions.CHOOSE_PRODUCT_CODE.code})" in {
          val result = MovementListSearchOptions(
            exciseProductCode = Some(MovementListSearchOptions.CHOOSE_PRODUCT_CODE.code)
          ).getEpc

          result mustBe None
        }
        "exciseProductCode is None" in {
          val result = MovementListSearchOptions(
            exciseProductCode = None
          ).getEpc

          result mustBe None
        }
      }
    }

    "getCountryOfOrigin" must {
      s"return Some(search.countryOfOrigin -> value)" when {
        s"countryOfOrigin is not ${MovementListSearchOptions.CHOOSE_COUNTRY.code}" in {
          val result = MovementListSearchOptions(
            countryOfOrigin = Some("value")
          ).getCountryOfOrigin

          result mustBe Some("search.countryOfOrigin" -> "value")
        }
      }

      "return None" when {
        s"countryOfOrigin is Some(${MovementListSearchOptions.CHOOSE_COUNTRY.code})" in {
          val result = MovementListSearchOptions(
            countryOfOrigin = Some(MovementListSearchOptions.CHOOSE_COUNTRY.code)
          ).getCountryOfOrigin

          result mustBe None
        }
        "countryOfOrigin is None" in {
          val result = MovementListSearchOptions(
            countryOfOrigin = None
          ).getCountryOfOrigin

          result mustBe None
        }
      }
    }

    "localDateToString" must {
      "convert a LocalDate to a String in the format dd/MM/yyyy" in {
        MovementListSearchOptions.localDateToString(LocalDate.parse("2024-12-30")) mustBe "30/12/2024"
      }
      "pad days and months with leading zeros if necessary" in {
        MovementListSearchOptions.localDateToString(LocalDate.of(2024, 1, 3)) mustBe "03/01/2024"
      }
    }

    "stringToLocalDate" must {
      "turn a String with format dd/MM/yyyy into a LocalDate" in {
        MovementListSearchOptions.stringToLocalDate("15/02/2024") mustBe LocalDate.of(2024, 2, 15)
      }
    }
  }
}
