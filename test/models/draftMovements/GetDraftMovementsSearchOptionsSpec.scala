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
import models.GetDraftMovementsSearchOptions
import models.common.DestinationType
import models.draftMovements.DraftMovementSortingSelectOption.{LrnAscending, Newest}

import java.time.LocalDate

class GetDraftMovementsSearchOptionsSpec extends SpecBase {

  val dateOfDispatchFrom: LocalDate = LocalDate.parse("2024-04-12")
  val dateOfDispatchTo: LocalDate = LocalDate.parse("2024-04-13")

  "GetDraftMovementsSearchOptions" must {

    "have the correct query params" when {

      "minimum values given" in {

        val expectedResult = Seq(
          "search.sortField" -> Newest.sortField,
          "search.sortOrder" -> Newest.sortOrder,
          "search.startPosition" -> "1",
          "search.maxRows" -> "10"
        )

        val actualResult = GetDraftMovementsSearchOptions(
          sortBy = Newest,
          index = 1,
          maxRows = 10
        ).queryParams

        actualResult mustBe expectedResult
      }

      "maximum values given" in {

        val expectedResult = Seq(
          "search.sortField" -> LrnAscending.sortField,
          "search.sortOrder" -> LrnAscending.sortOrder,
          "search.startPosition" -> "1",
          "search.maxRows" -> "10",
          "search.searchTerm" -> "term",
          "search.draftHasErrors" -> "true",
          "search.destinationType" -> DestinationType.TaxWarehouse.toString,
          "search.dateOfDispatchFrom" -> dateOfDispatchFrom.toString,
          "search.dateOfDispatchTo" -> dateOfDispatchTo.toString,
          "search.exciseProductCode" -> "exciseProductCode"
        )

        val actualResult = GetDraftMovementsSearchOptions(
          sortBy = LrnAscending,
          index = 1,
          maxRows = 10,
          searchTerm = Some("term"),
          draftHasErrors = Some(true),
          destinationTypes = Some(Seq(DestinationType.TaxWarehouse)),
          dateOfDispatchFrom = Some(dateOfDispatchFrom),
          dateOfDispatchTo = Some(dateOfDispatchTo),
          exciseProductCode = Some("exciseProductCode")
        ).queryParams

        actualResult mustBe expectedResult
      }

      "multiple destination types are given" in {

        val expectedResult = Seq(
          "search.sortField" -> LrnAscending.sortField,
          "search.sortOrder" -> LrnAscending.sortOrder,
          "search.startPosition" -> "1",
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
          "search.destinationType" -> DestinationType.ReturnToThePlaceOfDispatchOfTheConsignor.toString,
          "search.dateOfDispatchFrom" -> dateOfDispatchFrom.toString,
          "search.dateOfDispatchTo" -> dateOfDispatchTo.toString,
          "search.exciseProductCode" -> "exciseProductCode"
        )

        val actualResult = GetDraftMovementsSearchOptions(
          sortBy = LrnAscending,
          index = 1,
          maxRows = 10,
          searchTerm = Some("term"),
          draftHasErrors = Some(true),
          destinationTypes = Some(DestinationType.values),
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
          sortBy = LrnAscending,
          index = 1,
          maxRows = 10,
          searchTerm = Some("term"),
          draftHasErrors = Some(true),
          destinationTypes = Some(Seq(DestinationType.TaxWarehouse)),
          dateOfDispatchFrom = Some(dateOfDispatchFrom),
          dateOfDispatchTo = Some(dateOfDispatchTo),
          exciseProductCode = Some("exciseProductCode")
        )))

        val actualResult = GetDraftMovementsSearchOptions.queryStringBinder.bind("", Map(
          "sortBy" -> Seq(LrnAscending.toString),
          "startPosition" -> Seq("1"),
          "maxRows" -> Seq("10"),
          "searchTerm" -> Seq("term"),
          "draftHasErrors" -> Seq("true"),
          "destinationType" -> Seq(DestinationType.TaxWarehouse.toString),
          "dateOfDispatchFrom" -> Seq(dateOfDispatchFrom.toString),
          "dateOfDispatchTo" -> Seq(dateOfDispatchTo.toString),
          "exciseProductCode" -> Seq("exciseProductCode")
        ))

        actualResult mustBe expectedResult
      }

      "multiple destination type parameters are supplied" in {

        val expectedResult = Some(Right(GetDraftMovementsSearchOptions(
          destinationTypes = Some(DestinationType.values),
        )))

        val actualResult = GetDraftMovementsSearchOptions.queryStringBinder.bind("", Map(
          "destinationType" -> DestinationType.values.map(_.toString)
        ))

        actualResult mustBe expectedResult
      }
    }

    "unbind to correctly" when {

      "with a single DestiantionType supplied" in {

        val expectedResult =
          s"sortBy=${LrnAscending.toString}&" +
          "index=1&" +
          "searchTerm=term&" +
          "draftHasErrors=true&" +
          s"destinationType=${DestinationType.TaxWarehouse.toString}&" +
          s"dateOfDispatchFrom=${dateOfDispatchFrom.toString}&" +
          s"dateOfDispatchTo=${dateOfDispatchTo.toString}&" +
          "exciseProductCode=exciseProductCode"

        val actualResult = GetDraftMovementsSearchOptions.queryStringBinder.unbind("", GetDraftMovementsSearchOptions(
          sortBy = LrnAscending,
          index = 1,
          maxRows = 10,
          searchTerm = Some("term"),
          draftHasErrors = Some(true),
          destinationTypes = Some(Seq(DestinationType.TaxWarehouse)),
          dateOfDispatchFrom = Some(dateOfDispatchFrom),
          dateOfDispatchTo = Some(dateOfDispatchTo),
          exciseProductCode = Some("exciseProductCode")
        ))

        actualResult mustBe expectedResult
      }

      "multiple destination type parameters are supplied" in {

        val expectedResult =
          s"sortBy=${Newest.toString}&" +
            "index=1&" +
            DestinationType.values.map(destinationType => s"destinationType=$destinationType").mkString("&")

        val actualResult = GetDraftMovementsSearchOptions.queryStringBinder.unbind("", GetDraftMovementsSearchOptions(
          destinationTypes = Some(DestinationType.values),
        ))

        actualResult mustBe expectedResult
      }
    }
  }
}
