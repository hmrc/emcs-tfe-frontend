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

import models.SelectOptionModel
import models.draftMovements.DraftMovementSortingSelectOption.Newest
import models.draftMovements.GetDraftMovementsSearchOptions.{DEFAULT_INDEX, DEFAULT_MAX_ROWS}
import play.api.mvc.QueryStringBindable
import utils.Logging
import viewmodels.draftMovements.DraftMovementsErrorsOption
import viewmodels.draftMovements.DraftMovementsErrorsOption.DraftHasErrors

import java.time.LocalDate

case class GetDraftMovementsSearchOptions(
                                           sortBy: DraftMovementSortingSelectOption = Newest,
                                           index: Int = DEFAULT_INDEX,
                                           maxRows: Int = DEFAULT_MAX_ROWS,
                                           searchValue: Option[String] = None,
                                           draftHasErrors: Option[Boolean] = None,
                                           destinationTypes: Option[Seq[DestinationTypeSearchOption]] = None,
                                           dateOfDispatchFrom: Option[LocalDate] = None,
                                           dateOfDispatchTo: Option[LocalDate] = None,
                                           exciseProductCode: Option[String] = None
                                         ) {

  val hasFilterApplied: Boolean = Seq(
    searchValue,
    draftHasErrors,
    destinationTypes,
    dateOfDispatchFrom,
    dateOfDispatchTo,
    exciseProductCode
  ).exists(_.isDefined)

  val startingPosition: Int = (index - 1) * maxRows

  val queryParams: Seq[(String, String)] = Seq(
    Some("search.sortField" -> sortBy.sortField),
    Some("search.sortOrder" -> sortBy.sortOrder),
    Some("search.startPosition" -> startingPosition.toString),
    Some("search.maxRows" -> maxRows.toString),
    searchValue.map(search => "search.searchTerm" -> search),
    draftHasErrors.map(hasErrors => "search.draftHasErrors" -> hasErrors.toString),
    destinationTypes.map(_.map(destinationType => "search.destinationType" -> destinationType.destinationType.toString)).getOrElse(Seq.empty),
    dateOfDispatchFrom.map(date => "search.dateOfDispatchFrom" -> date.toString),
    dateOfDispatchTo.map(date => "search.dateOfDispatchTo" -> date.toString),
    exciseProductCode.map(code => "search.exciseProductCode" -> code)
  ).flatten


}

object GetDraftMovementsSearchOptions extends Logging {

  val DEFAULT_INDEX: Int = 1
  val DEFAULT_MAX_ROWS: Int = 10

  object CHOOSE_PRODUCT_CODE extends SelectOptionModel {
    override val code: String = "chooseProductCode"
    override val displayName: String = "viewAllDraftMovements.filters.exciseProduct.chooseProductCode"
  }

  //noinspection ScalaStyle
  implicit def queryStringBinder(implicit intBinder: QueryStringBindable[Int],
                                 stringBinder: QueryStringBindable[String],
                                 booleanBinder: QueryStringBindable[Boolean],
                                 destinationTypeBinder: QueryStringBindable[Seq[DestinationTypeSearchOption]]
                                ): QueryStringBindable[GetDraftMovementsSearchOptions] =
    new QueryStringBindable[GetDraftMovementsSearchOptions] {

      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, GetDraftMovementsSearchOptions]] = {
        Some(for {
          sortBy <- stringBinder.bind("sortBy", params).getOrElse(Right(Newest.code))
          index <- intBinder.bind("index", params).getOrElse(Right(DEFAULT_INDEX))
          searchValue <- stringBinder.bind("searchValue", params).map(_.map(Some(_))).getOrElse(Right(None))
          draftHasErrors <- booleanBinder.bind("draftHasErrors", params).map(_.map(Some(_))).getOrElse(Right(None))
          destinationTypes <- destinationTypeBinder.bind("destinationType", params).map(_.map(Some(_))).getOrElse(Right(None))
          dateOfDispatchFrom <- stringBinder.bind("dateOfDispatchFrom", params).map(_.map(Some(_))).getOrElse(Right(None))
          dateOfDispatchTo <- stringBinder.bind("dateOfDispatchTo", params).map(_.map(Some(_))).getOrElse(Right(None))
          exciseProductCode <- stringBinder.bind("exciseProductCode", params).map(_.map(Some(_))).getOrElse(Right(None))
        } yield {
          try {
            GetDraftMovementsSearchOptions(
              sortBy = DraftMovementSortingSelectOption(sortBy),
              index = index,
              maxRows = DEFAULT_MAX_ROWS,
              searchValue = searchValue,
              draftHasErrors = draftHasErrors,
              destinationTypes = destinationTypes,
              dateOfDispatchFrom = dateOfDispatchFrom.map(date => LocalDate.parse(date)),
              dateOfDispatchTo = dateOfDispatchTo.map(date => LocalDate.parse(date)),
              exciseProductCode = exciseProductCode
            )
          } catch {
            case iae: IllegalArgumentException =>
              logger.warn(s"[queryStringBinder] - ${iae.getMessage}")
              GetDraftMovementsSearchOptions()
            case e: Throwable =>
              throw e
          }
        })
      }

      override def unbind(key: String, searchOptions: GetDraftMovementsSearchOptions): String = {
        Seq(
          Some(stringBinder.unbind("sortBy", searchOptions.sortBy.code)),
          Some(intBinder.unbind("index", searchOptions.index)),
          searchOptions.searchValue.map(searchValue => stringBinder.unbind("searchValue", searchValue)),
          searchOptions.draftHasErrors.map(hasErrors => booleanBinder.unbind("draftHasErrors", hasErrors)),
          searchOptions.destinationTypes.map(destinationTypes => destinationTypeBinder.unbind("destinationType", destinationTypes)),
          searchOptions.dateOfDispatchFrom.map(date => stringBinder.unbind("dateOfDispatchFrom", date.toString)),
          searchOptions.dateOfDispatchTo.map(date => stringBinder.unbind("dateOfDispatchTo", date.toString)),
          searchOptions.exciseProductCode.map(code => stringBinder.unbind("exciseProductCode", code))
        ).flatten.mkString("&")
      }
    }

  def apply(
             sortBy: String,
             searchValue: Option[String],
             errors: Set[DraftMovementsErrorsOption],
             destinationTypes: Set[DestinationTypeSearchOption],
             exciseProductCode: Option[String],
             dateOfDispatchFrom: Option[LocalDate],
             dateOfDispatchTo: Option[LocalDate]
           ): GetDraftMovementsSearchOptions = {

    val exciseProductCodeWithoutDefault: Option[String] = exciseProductCode match {
      case Some(value) if value == CHOOSE_PRODUCT_CODE.code => None
      case value => value
    }

    GetDraftMovementsSearchOptions(
      sortBy = DraftMovementSortingSelectOption(sortBy),
      index = DEFAULT_INDEX,
      maxRows = DEFAULT_MAX_ROWS,
      searchValue = searchValue,
      draftHasErrors = errors.headOption.map(_ == DraftHasErrors),
      destinationTypes = Option.when(destinationTypes.nonEmpty)(destinationTypes.toSeq),
      dateOfDispatchFrom = dateOfDispatchFrom,
      dateOfDispatchTo = dateOfDispatchTo,
      exciseProductCode = exciseProductCodeWithoutDefault
    )
  }

  def unapply(options: GetDraftMovementsSearchOptions): Option[(
      String,
      Option[String],
      Set[DraftMovementsErrorsOption],
      Set[DestinationTypeSearchOption],
      Option[String],
      Option[LocalDate],
      Option[LocalDate]
    )] = Some(
    (
      options.sortBy.code,
      options.searchValue,
      options.draftHasErrors.fold[Set[DraftMovementsErrorsOption]](Set.empty){
        case true => Set(DraftMovementsErrorsOption.DraftHasErrors)
        case _ => Set()
      },
      options.destinationTypes.fold[Set[DestinationTypeSearchOption]](Set.empty)(_.toSet),
      options.exciseProductCode,
      options.dateOfDispatchFrom,
      options.dateOfDispatchTo
    )
  )
}
