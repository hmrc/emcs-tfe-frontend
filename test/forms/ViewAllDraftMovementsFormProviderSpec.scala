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
import models.draftMovements.{DestinationTypeSearchOption, DraftMovementSortingSelectOption, GetDraftMovementsSearchOptions}
import play.api.data.FormError
import viewmodels.draftMovements.DraftMovementsErrorsOption

import java.time.LocalDate

class ViewAllDraftMovementsFormProviderSpec extends SpecBase {

  val form = new ViewAllDraftMovementsFormProvider()()
  val sortByKey = "sortBy"
  val searchValue = "searchValue"
  val exciseProductCode = "exciseProductCode"
  val draftHasErrors = "draftHasErrors[0]"
  val destinationTypes = (idx: Int) => s"destinationTypes[$idx]"
  val dateOfDispatchFrom = "dateOfDispatchFrom"
  val dateOfDispatchFromDay = "dateOfDispatchFrom.day"
  val dateOfDispatchFromMonth = "dateOfDispatchFrom.month"
  val dateOfDispatchFromYear = "dateOfDispatchFrom.year"
  val dateOfDispatchTo = "dateOfDispatchTo"
  val dateOfDispatchToDay = "dateOfDispatchTo.day"
  val dateOfDispatchToMonth = "dateOfDispatchTo.month"
  val dateOfDispatchToYear = "dateOfDispatchTo.year"

  def traderRoleKey(i: Int) = s"traderRole[$i]"

  ".sortBy" should {

    "bind when the searchValue is present" in {

      val expectedResult = GetDraftMovementsSearchOptions(
        sortBy = DraftMovementSortingSelectOption.Oldest
      )

      val actualResult = form.bind(Map(
        sortByKey -> DraftMovementSortingSelectOption.Oldest.code
      )).get

      actualResult mustBe expectedResult
    }

    "remove any non-alphanumerics from the form values" in {

      val expectedResult = GetDraftMovementsSearchOptions(
        sortBy = DraftMovementSortingSelectOption.Oldest
      )

      val actualResult = form.bind(Map(
        sortByKey -> s"^^${DraftMovementSortingSelectOption.Oldest}!?"
      )).get

      actualResult mustBe expectedResult
    }
  }

  ".searchValue" should {

    "bind when the searchValue is present" in {

      val expectedResult = GetDraftMovementsSearchOptions(
        searchValue = Some("searchValue")
      )

      val actualResult = form.bind(Map(
        sortByKey -> DraftMovementSortingSelectOption.Newest.code,
        searchValue -> "searchValue"
      )).get

      actualResult mustBe expectedResult
    }

    "return an error when the value is invalid" in {
      val boundForm = form.bind(Map(
        sortByKey -> DraftMovementSortingSelectOption.Newest.code,
        searchValue -> "<script>alert('hi')</script>",
      ))
      boundForm.errors mustBe List(FormError(searchValue, List("error.invalidCharacter"), List(XSS_REGEX)))
    }
  }

  ".draftHasErrors" should {

    "bind when the draftErrors is selected" in {

      val expectedResult = GetDraftMovementsSearchOptions(
        draftHasErrors = Some(true)
      )

      val actualResult = form.bind(Map(
        sortByKey -> DraftMovementSortingSelectOption.Newest.code,
        draftHasErrors -> DraftMovementsErrorsOption.DraftHasErrors.toString
      )).get

      actualResult mustBe expectedResult
    }

    "bind when the draftErrors is NOT selected" in {

      val expectedResult = GetDraftMovementsSearchOptions(
        draftHasErrors = None
      )

      val actualResult = form.bind(Map(
        sortByKey -> DraftMovementSortingSelectOption.Newest.code
      )).get

      actualResult mustBe expectedResult
    }

    "give an error when draftErrors is invalid" in {

      val expectedResult = List(FormError(
        key = draftHasErrors,
        messages = List("error.invalid"),
        args = List()
      ))

      val actualResult = form.bind(Map(
        sortByKey -> DraftMovementSortingSelectOption.Newest.code,
        draftHasErrors -> "InvalidOption"
      )).errors

      actualResult mustBe expectedResult
    }
  }

  ".destinationTypes" should {

    "bind when a single destinationTypes is selected" in {

      val expectedResult = GetDraftMovementsSearchOptions(
        destinationTypes = Some(Seq(DestinationTypeSearchOption.TaxWarehouse))
      )

      val actualResult = form.bind(Map(
        sortByKey -> DraftMovementSortingSelectOption.Newest.code,
        destinationTypes(0) -> DestinationTypeSearchOption.TaxWarehouse.toString
      )).get

      actualResult mustBe expectedResult
    }

    "bind when multiple destinationTypes are selected" in {

      val expectedResult = GetDraftMovementsSearchOptions(
        destinationTypes = Some(Seq(
          DestinationTypeSearchOption.TaxWarehouse,
          DestinationTypeSearchOption.CertifiedConsignee
        ))
      )

      val actualResult = form.bind(Map(
        sortByKey -> DraftMovementSortingSelectOption.Newest.code,
        destinationTypes(0) -> DestinationTypeSearchOption.TaxWarehouse.toString,
        destinationTypes(1) -> DestinationTypeSearchOption.CertifiedConsignee.toString
      )).get

      actualResult mustBe expectedResult
    }

    "bind when NO destinationTypes is selected" in {

      val expectedResult = GetDraftMovementsSearchOptions(
        destinationTypes = None
      )

      val actualResult = form.bind(Map(
        sortByKey -> DraftMovementSortingSelectOption.Newest.code
      )).get

      actualResult mustBe expectedResult
    }

    "give an error when destinationTypes is invalid" in {

      val expectedResult = List(FormError(
        key = destinationTypes(0),
        messages = List("error.invalid"),
        args = List()
      ))

      val actualResult = form.bind(Map(
        sortByKey -> DraftMovementSortingSelectOption.Newest.code,
        destinationTypes(0) -> "InvalidOption"
      )).errors

      actualResult mustBe expectedResult
    }
  }

  ".exciseProductCode" should {

    "bind when the exciseProductCode is present" in {

      val expectedResult = GetDraftMovementsSearchOptions(
        exciseProductCode = Some("exciseProductCode")
      )

      val actualResult = form.bind(Map(
        sortByKey -> DraftMovementSortingSelectOption.Newest.code,
        exciseProductCode -> "exciseProductCode"
      )).get

      actualResult mustBe expectedResult
    }

    "remove any non-alphanumerics from the form values" in {

      val expectedResult = GetDraftMovementsSearchOptions(
        exciseProductCode = Some("exciseProductCode1injectingvirusesscriptalertscript")
      )

      val actualResult = form.bind(Map(
        sortByKey -> DraftMovementSortingSelectOption.Newest.code,
        exciseProductCode -> "exciseProductCode1/injecting-viruses!!!!<script>\"alert</script>"
      )).get

      actualResult mustBe expectedResult
    }
  }

  ".dateOfDispatchFrom" should {

    "bind when the date is valid" in {

      val expectedResult = GetDraftMovementsSearchOptions(
        dateOfDispatchFrom = Some(LocalDate.parse("2020-01-01"))
      )

      val actualResult = form.bind(Map(
        sortByKey -> DraftMovementSortingSelectOption.Newest.code,
        dateOfDispatchFromDay -> "01",
        dateOfDispatchFromMonth -> "01",
        dateOfDispatchFromYear -> "2020"
      )).get

      actualResult mustBe expectedResult
    }

    "give an error when the date is invalid" in {

      val expectedResult = List(FormError(
        key = dateOfDispatchFrom,
        messages = List("viewAllDraftMovements.filters.dateOfDispatchFrom.error.notARealDate"),
        args = List()
      ))

      val actualResult = form.bind(Map(
        sortByKey -> DraftMovementSortingSelectOption.Newest.code,
        dateOfDispatchFromDay -> "29",
        dateOfDispatchFromMonth -> "02",
        dateOfDispatchFromYear -> "2022"
      )).errors

      actualResult mustBe expectedResult
    }

    "give an error when the day not given" in {

      val expectedResult = List(FormError(
        key = dateOfDispatchFrom,
        messages = List("viewAllDraftMovements.filters.dateOfDispatchFrom.error.required.one"),
        args = List("day")
      ))

      val actualResult = form.bind(Map(
        sortByKey -> DraftMovementSortingSelectOption.Newest.code,
        dateOfDispatchFromMonth -> "02",
        dateOfDispatchFromYear -> "2022"
      )).errors

      actualResult mustBe expectedResult
    }

    "give an error when the month not given" in {

      val expectedResult = List(FormError(
        key = dateOfDispatchFrom,
        messages = List("viewAllDraftMovements.filters.dateOfDispatchFrom.error.required.one"),
        args = List("month")
      ))

      val actualResult = form.bind(Map(
        sortByKey -> DraftMovementSortingSelectOption.Newest.code,
        dateOfDispatchFromDay -> "01",
        dateOfDispatchFromYear -> "2022"
      )).errors

      actualResult mustBe expectedResult
    }

    "give an error when the year not given" in {

      val expectedResult = List(FormError(
        key = dateOfDispatchFrom,
        messages = List("viewAllDraftMovements.filters.dateOfDispatchFrom.error.required.one"),
        args = List("year")
      ))

      val actualResult = form.bind(Map(
        sortByKey -> DraftMovementSortingSelectOption.Newest.code,
        dateOfDispatchFromDay -> "01",
        dateOfDispatchFromMonth -> "02"
      )).errors

      actualResult mustBe expectedResult
    }

    "give an error when month and year not given" in {

      val expectedResult = List(FormError(
        key = dateOfDispatchFrom,
        messages = List("viewAllDraftMovements.filters.dateOfDispatchFrom.error.required.two"),
        args = List("month", "year")
      ))

      val actualResult = form.bind(Map(
        sortByKey -> DraftMovementSortingSelectOption.Newest.code,
        dateOfDispatchFromDay -> "01"
      )).errors

      actualResult mustBe expectedResult
    }

    "give an error when day and year not given" in {

      val expectedResult = List(FormError(
        key = dateOfDispatchFrom,
        messages = List("viewAllDraftMovements.filters.dateOfDispatchFrom.error.required.two"),
        args = List("day", "year")
      ))

      val actualResult = form.bind(Map(
        sortByKey -> DraftMovementSortingSelectOption.Newest.code,
        dateOfDispatchFromMonth -> "01"
      )).errors

      actualResult mustBe expectedResult
    }

    "give an error when day and month not given" in {

      val expectedResult = List(FormError(
        key = dateOfDispatchFrom,
        messages = List("viewAllDraftMovements.filters.dateOfDispatchFrom.error.required.two"),
        args = List("day", "month")
      ))

      val actualResult = form.bind(Map(
        sortByKey -> DraftMovementSortingSelectOption.Newest.code,
        dateOfDispatchFromYear -> "2020"
      )).errors

      actualResult mustBe expectedResult
    }

    "give an error when the month and year is invalid" in {

      val expectedResult = List(FormError(
        key = dateOfDispatchFrom,
        messages = List("viewAllDraftMovements.filters.dateOfDispatchFrom.error.notARealDate"),
        args = List()
      ))

      val actualResult = form.bind(Map(
        sortByKey -> DraftMovementSortingSelectOption.Newest.code,
        dateOfDispatchFromDay -> "29",
        dateOfDispatchFromMonth -> "0",
        dateOfDispatchFromYear -> "0"
      )).errors

      actualResult mustBe expectedResult
    }
  }

  ".dateOfDispatchTo" should {

    "bind when the date is valid" in {

      val expectedResult = GetDraftMovementsSearchOptions(
        dateOfDispatchTo = Some(LocalDate.parse("2020-01-01"))
      )

      val actualResult = form.bind(Map(
        sortByKey -> DraftMovementSortingSelectOption.Newest.code,
        dateOfDispatchToDay -> "01",
        dateOfDispatchToMonth -> "01",
        dateOfDispatchToYear -> "2020"
      )).get

      actualResult mustBe expectedResult
    }

    "give an error when the date is invalid" in {

      val expectedResult = List(FormError(
        key = dateOfDispatchTo,
        messages = List("viewAllDraftMovements.filters.dateOfDispatchTo.error.notARealDate"),
        args = List()
      ))

      val actualResult = form.bind(Map(
        sortByKey -> DraftMovementSortingSelectOption.Newest.code,
        dateOfDispatchToDay -> "29",
        dateOfDispatchToMonth -> "02",
        dateOfDispatchToYear -> "2022"
      )).errors

      actualResult mustBe expectedResult
    }

    "give an error when the day not given" in {

      val expectedResult = List(FormError(
        key = dateOfDispatchTo,
        messages = List("viewAllDraftMovements.filters.dateOfDispatchTo.error.required.one"),
        args = List("day")
      ))

      val actualResult = form.bind(Map(
        sortByKey -> DraftMovementSortingSelectOption.Newest.code,
        dateOfDispatchToMonth -> "02",
        dateOfDispatchToYear -> "2022"
      )).errors

      actualResult mustBe expectedResult
    }

    "give an error when the month not given" in {

      val expectedResult = List(FormError(
        key = dateOfDispatchTo,
        messages = List("viewAllDraftMovements.filters.dateOfDispatchTo.error.required.one"),
        args = List("month")
      ))

      val actualResult = form.bind(Map(
        sortByKey -> DraftMovementSortingSelectOption.Newest.code,
        dateOfDispatchToDay -> "01",
        dateOfDispatchToYear -> "2022"
      )).errors

      actualResult mustBe expectedResult
    }

    "give an error when the year not given" in {

      val expectedResult = List(FormError(
        key = dateOfDispatchTo,
        messages = List("viewAllDraftMovements.filters.dateOfDispatchTo.error.required.one"),
        args = List("year")
      ))

      val actualResult = form.bind(Map(
        sortByKey -> DraftMovementSortingSelectOption.Newest.code,
        dateOfDispatchToDay -> "01",
        dateOfDispatchToMonth -> "02"
      )).errors

      actualResult mustBe expectedResult
    }

    "give an error when month and year not given" in {

      val expectedResult = List(FormError(
        key = dateOfDispatchTo,
        messages = List("viewAllDraftMovements.filters.dateOfDispatchTo.error.required.two"),
        args = List("month", "year")
      ))

      val actualResult = form.bind(Map(
        sortByKey -> DraftMovementSortingSelectOption.Newest.code,
        dateOfDispatchToDay -> "01"
      )).errors

      actualResult mustBe expectedResult
    }

    "give an error when day and year not given" in {

      val expectedResult = List(FormError(
        key = dateOfDispatchTo,
        messages = List("viewAllDraftMovements.filters.dateOfDispatchTo.error.required.two"),
        args = List("day", "year")
      ))

      val actualResult = form.bind(Map(
        sortByKey -> DraftMovementSortingSelectOption.Newest.code,
        dateOfDispatchToMonth -> "01"
      )).errors

      actualResult mustBe expectedResult
    }

    "give an error when day and month not given" in {

      val expectedResult = List(FormError(
        key = dateOfDispatchTo,
        messages = List("viewAllDraftMovements.filters.dateOfDispatchTo.error.required.two"),
        args = List("day", "month")
      ))

      val actualResult = form.bind(Map(
        sortByKey -> DraftMovementSortingSelectOption.Newest.code,
        dateOfDispatchToYear -> "2020"
      )).errors

      actualResult mustBe expectedResult
    }

    "give an error when the month and year is invalid" in {

      val expectedResult = List(FormError(
        key = dateOfDispatchTo,
        messages = List("viewAllDraftMovements.filters.dateOfDispatchTo.error.notARealDate"),
        args = List()
      ))

      val actualResult = form.bind(Map(
        sortByKey -> DraftMovementSortingSelectOption.Newest.code,
        dateOfDispatchToDay -> "29",
        dateOfDispatchToMonth -> "0",
        dateOfDispatchToYear -> "0"
      )).errors

      actualResult mustBe expectedResult
    }
  }

}
