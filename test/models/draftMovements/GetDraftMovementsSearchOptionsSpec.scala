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

package models.draftMovements

import base.SpecBase
import models.common.DestinationType
import viewmodels.draftMovements.DraftMovementsErrorsOption

import java.time.LocalDate

class GetDraftMovementsSearchOptionsSpec extends SpecBase {

  val dateOfDispatchFrom: LocalDate = LocalDate.parse("2024-04-12")
  val dateOfDispatchTo: LocalDate = LocalDate.parse("2024-04-13")

  "GetDraftMovementsSearchOptions" must {

    "startingPosition must be correctly calculated from the index and maxRows" when {

      "maxRows is 10" when {

        "index is 1" in {

          val expectedResult = 0

          val actualResult = GetDraftMovementsSearchOptions(
            index = 1,
            maxRows = 10
          ).startingPosition

          actualResult mustBe expectedResult
        }

        "index is 2" in {

          val expectedResult = 10

          val actualResult = GetDraftMovementsSearchOptions(
            index = 2,
            maxRows = 10
          ).startingPosition

          actualResult mustBe expectedResult
        }

        "index is 50" in {

          val expectedResult = 490

          val actualResult = GetDraftMovementsSearchOptions(
            index = 50,
            maxRows = 10
          ).startingPosition

          actualResult mustBe expectedResult
        }
      }

      "maxRows is 5" when {

        "index is 1" in {

          val expectedResult = 0

          val actualResult = GetDraftMovementsSearchOptions(
            index = 1,
            maxRows = 5
          ).startingPosition

          actualResult mustBe expectedResult
        }

        "index is 2" in {

          val expectedResult = 5

          val actualResult = GetDraftMovementsSearchOptions(
            index = 2,
            maxRows = 5
          ).startingPosition

          actualResult mustBe expectedResult
        }

        "index is 50" in {

          val expectedResult = 245

          val actualResult = GetDraftMovementsSearchOptions(
            index = 50,
            maxRows = 5
          ).startingPosition

          actualResult mustBe expectedResult
        }
      }

      "maxRows is 30" when {

        "index is 1" in {

          val expectedResult = 0

          val actualResult = GetDraftMovementsSearchOptions(
            index = 1,
            maxRows = 5
          ).startingPosition

          actualResult mustBe expectedResult
        }

        "index is 2" in {

          val expectedResult = 30

          val actualResult = GetDraftMovementsSearchOptions(
            index = 2,
            maxRows = 30
          ).startingPosition

          actualResult mustBe expectedResult
        }

        "index is 50" in {

          val expectedResult = 1470

          val actualResult = GetDraftMovementsSearchOptions(
            index = 50,
            maxRows = 30
          ).startingPosition

          actualResult mustBe expectedResult
        }
      }
    }

    "have the correct query params" when {

      "minimum values given" in {

        val expectedResult = Seq(
          "search.sortField" -> DraftMovementSortingSelectOption.Newest.sortField,
          "search.sortOrder" -> DraftMovementSortingSelectOption.Newest.sortOrder,
          "search.startPosition" -> "0",
          "search.maxRows" -> "10"
        )

        val actualResult = GetDraftMovementsSearchOptions(
          sortBy = DraftMovementSortingSelectOption.Newest,
          index = 1,
          maxRows = 10
        ).queryParams

        actualResult mustBe expectedResult
      }

      "maximum values given" in {

        val expectedResult = Seq(
          "search.sortField" -> DraftMovementSortingSelectOption.LrnAscending.sortField,
          "search.sortOrder" -> DraftMovementSortingSelectOption.LrnAscending.sortOrder,
          "search.startPosition" -> "0",
          "search.maxRows" -> "10",
          "search.searchTerm" -> "term",
          "search.draftHasErrors" -> "true",
          "search.destinationType" -> DestinationType.TaxWarehouse.toString,
          "search.dateOfDispatchFrom" -> dateOfDispatchFrom.toString,
          "search.dateOfDispatchTo" -> dateOfDispatchTo.toString,
          "search.exciseProductCode" -> "exciseProductCode"
        )

        val actualResult = GetDraftMovementsSearchOptions(
          sortBy = DraftMovementSortingSelectOption.LrnAscending,
          index = 1,
          maxRows = 10,
          searchValue = Some("term"),
          draftHasErrors = Some(true),
          destinationTypes = Some(Seq(DestinationTypeSearchOption.TaxWarehouse)),
          dateOfDispatchFrom = Some(dateOfDispatchFrom),
          dateOfDispatchTo = Some(dateOfDispatchTo),
          exciseProductCode = Some("exciseProductCode")
        ).queryParams

        actualResult mustBe expectedResult
      }

      "multiple destination types are given" in {

        val expectedResult = Seq(
          "search.sortField" -> DraftMovementSortingSelectOption.LrnAscending.sortField,
          "search.sortOrder" -> DraftMovementSortingSelectOption.LrnAscending.sortOrder,
          "search.startPosition" -> "0",
          "search.maxRows" -> "10",
          "search.searchTerm" -> "term",
          "search.draftHasErrors" -> "true",
          "search.destinationType" -> DestinationType.TaxWarehouse.toString,
          "search.destinationType" -> DestinationType.RegisteredConsignee.toString,
          "search.destinationType" -> DestinationType.TemporaryRegisteredConsignee.toString,
          "search.destinationType" -> DestinationType.DirectDelivery.toString,
          "search.destinationType" -> DestinationType.ExemptedOrganisation.toString,
          "search.destinationType" -> DestinationType.Export.toString,
          "search.destinationType" -> DestinationType.UnknownDestination.toString,
          "search.destinationType" -> DestinationType.CertifiedConsignee.toString,
          "search.destinationType" -> DestinationType.TemporaryCertifiedConsignee.toString,
          "search.dateOfDispatchFrom" -> dateOfDispatchFrom.toString,
          "search.dateOfDispatchTo" -> dateOfDispatchTo.toString,
          "search.exciseProductCode" -> "exciseProductCode"
        )

        val actualResult = GetDraftMovementsSearchOptions(
          sortBy = DraftMovementSortingSelectOption.LrnAscending,
          index = 1,
          maxRows = 10,
          searchValue = Some("term"),
          draftHasErrors = Some(true),
          destinationTypes = Some(DestinationTypeSearchOption.values),
          dateOfDispatchFrom = Some(dateOfDispatchFrom),
          dateOfDispatchTo = Some(dateOfDispatchTo),
          exciseProductCode = Some("exciseProductCode")
        ).queryParams

        actualResult mustBe expectedResult
      }
    }

    "bind to correctly" when {

      "no query parameters are supplied" in {

        val expectedResult = Some(Right(GetDraftMovementsSearchOptions()))

        val actualResult = GetDraftMovementsSearchOptions.queryStringBinder.bind("", Map())

        actualResult mustBe expectedResult
      }

      "all query parameters are supplied" in {

        val expectedResult = Some(Right(GetDraftMovementsSearchOptions(
          sortBy = DraftMovementSortingSelectOption.LrnAscending,
          index = 1,
          maxRows = 10,
          searchValue = Some("term"),
          draftHasErrors = Some(true),
          destinationTypes = Some(Seq(DestinationTypeSearchOption.TaxWarehouse)),
          dateOfDispatchFrom = Some(dateOfDispatchFrom),
          dateOfDispatchTo = Some(dateOfDispatchTo),
          exciseProductCode = Some("exciseProductCode")
        )))

        val actualResult = GetDraftMovementsSearchOptions.queryStringBinder.bind("", Map(
          "sortBy" -> Seq(DraftMovementSortingSelectOption.LrnAscending.toString),
          "startPosition" -> Seq("0"),
          "maxRows" -> Seq("10"),
          "searchValue" -> Seq("term"),
          "draftHasErrors" -> Seq("true"),
          "destinationType" -> Seq(DestinationTypeSearchOption.TaxWarehouse.toString),
          "dateOfDispatchFrom" -> Seq(dateOfDispatchFrom.toString),
          "dateOfDispatchTo" -> Seq(dateOfDispatchTo.toString),
          "exciseProductCode" -> Seq("exciseProductCode")
        ))

        actualResult mustBe expectedResult
      }

      "multiple destination type parameters are supplied" in {

        val expectedResult = Some(Right(GetDraftMovementsSearchOptions(
          destinationTypes = Some(DestinationTypeSearchOption.values)
        )))

        val actualResult = GetDraftMovementsSearchOptions.queryStringBinder.bind("", Map(
          "destinationType" -> DestinationTypeSearchOption.values.map(_.toString)
        ))

        actualResult mustBe expectedResult
      }
    }

    "unbind to correctly" when {

      "with a single DestiantionType supplied" in {

        val expectedResult =
          s"sortBy=${DraftMovementSortingSelectOption.LrnAscending.toString}&" +
          "index=1&" +
          "searchValue=term&" +
          "draftHasErrors=true&" +
          s"destinationType=${DestinationTypeSearchOption.TaxWarehouse.toString}&" +
          s"dateOfDispatchFrom=${dateOfDispatchFrom.toString}&" +
          s"dateOfDispatchTo=${dateOfDispatchTo.toString}&" +
          "exciseProductCode=exciseProductCode"

        val actualResult = GetDraftMovementsSearchOptions.queryStringBinder.unbind("", GetDraftMovementsSearchOptions(
          sortBy = DraftMovementSortingSelectOption.LrnAscending,
          index = 1,
          maxRows = 10,
          searchValue = Some("term"),
          draftHasErrors = Some(true),
          destinationTypes = Some(Seq(DestinationTypeSearchOption.TaxWarehouse)),
          dateOfDispatchFrom = Some(dateOfDispatchFrom),
          dateOfDispatchTo = Some(dateOfDispatchTo),
          exciseProductCode = Some("exciseProductCode")
        ))

        actualResult mustBe expectedResult
      }

      "multiple destination type parameters are supplied" in {

        val expectedResult =
          s"sortBy=${DraftMovementSortingSelectOption.Newest.toString}&" +
            "index=1&" +
            DestinationTypeSearchOption.values.map(destinationType => s"destinationType=$destinationType").mkString("&")

        val actualResult = GetDraftMovementsSearchOptions.queryStringBinder.unbind("", GetDraftMovementsSearchOptions(
          destinationTypes = Some(DestinationTypeSearchOption.values)
        ))

        actualResult mustBe expectedResult
      }
    }

    "apply" when {

      "should return valid GetDraftMovementSearchOptions" in {

        val expectedResult = GetDraftMovementsSearchOptions(
          sortBy = DraftMovementSortingSelectOption.Oldest,
          searchValue = Some("search term"),
          draftHasErrors = Some(true),
          destinationTypes = Some(Seq(DestinationTypeSearchOption.TaxWarehouse)),
          dateOfDispatchFrom = Some(dateOfDispatchFrom),
          dateOfDispatchTo = Some(dateOfDispatchTo),
          exciseProductCode = Some("exciseProductCode")
        )

        val actualResult = GetDraftMovementsSearchOptions.apply(
          sortBy = DraftMovementSortingSelectOption.Oldest.toString,
          searchValue = Some("search term"),
          errors = Set(DraftMovementsErrorsOption.DraftHasErrors),
          destinationTypes = Set(DestinationTypeSearchOption.TaxWarehouse),
          exciseProductCode = Some("exciseProductCode"),
          dateOfDispatchFrom = Some(dateOfDispatchFrom),
          dateOfDispatchTo = Some(dateOfDispatchTo)
        )

        actualResult mustBe expectedResult
      }

      "should return GetDraftMovementSearchOptions with exciseProductCode None for ExciseProductCode when left as default" in {

        val expectedResult = GetDraftMovementsSearchOptions(
          sortBy = DraftMovementSortingSelectOption.Oldest,
          searchValue = Some("search term"),
          draftHasErrors = Some(true),
          destinationTypes = Some(Seq(DestinationTypeSearchOption.TaxWarehouse)),
          dateOfDispatchFrom = Some(dateOfDispatchFrom),
          dateOfDispatchTo = Some(dateOfDispatchTo),
          exciseProductCode = None
        )

        val actualResult = GetDraftMovementsSearchOptions.apply(
          sortBy = DraftMovementSortingSelectOption.Oldest.toString,
          searchValue = Some("search term"),
          errors = Set(DraftMovementsErrorsOption.DraftHasErrors),
          destinationTypes = Set(DestinationTypeSearchOption.TaxWarehouse),
          exciseProductCode = Some(GetDraftMovementsSearchOptions.CHOOSE_PRODUCT_CODE.code),
          dateOfDispatchFrom = Some(dateOfDispatchFrom),
          dateOfDispatchTo = Some(dateOfDispatchTo)
        )

        actualResult mustBe expectedResult
      }
    }

    "unapply" when {

      "should return form field values from the GetDraftMovementSearchOptions model" in {

        val expectedResult = Some((
          DraftMovementSortingSelectOption.Oldest.toString,
          Some("search term"),
          Set(DraftMovementsErrorsOption.DraftHasErrors),
          Set(DestinationTypeSearchOption.TaxWarehouse),
          Some("exciseProductCode"),
          Some(dateOfDispatchFrom),
          Some(dateOfDispatchTo)
        ))

        val actualResult = GetDraftMovementsSearchOptions.unapply(
          GetDraftMovementsSearchOptions(
            sortBy = DraftMovementSortingSelectOption.Oldest,
            searchValue = Some("search term"),
            draftHasErrors = Some(true),
            destinationTypes = Some(Seq(DestinationTypeSearchOption.TaxWarehouse)),
            dateOfDispatchFrom = Some(dateOfDispatchFrom),
            dateOfDispatchTo = Some(dateOfDispatchTo),
            exciseProductCode = Some("exciseProductCode")
          )
        )

        actualResult mustBe expectedResult
      }
    }
  }
}
