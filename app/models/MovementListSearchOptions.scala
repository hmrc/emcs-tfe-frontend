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

import models.MovementListSearchOptions.{DEFAULT_INDEX, DEFAULT_MAX_ROWS}
import models.MovementSortingSelectOption.ArcAscending
import play.api.mvc.QueryStringBindable

import java.time.LocalDate
import java.time.format.DateTimeFormatter

case class MovementListSearchOptions(searchKey: Option[MovementSearchSelectOption] = None,
                                     searchValue: Option[String] = None,
                                     sortBy: MovementSortingSelectOption = ArcAscending,
                                     traderRole: Option[MovementFilterDirectionOption] = None,
                                     undischargedMovements: Option[MovementFilterUndischargedOption] = None,
                                     movementStatus: Option[MovementFilterStatusOption] = None,
                                     exciseProductCode: Option[String] = None,
                                     countryOfOrigin: Option[String] = None,
                                     dateOfDispatchFrom: Option[LocalDate] = None,
                                     dateOfDispatchTo: Option[LocalDate] = None,
                                     dateOfReceiptFrom: Option[LocalDate] = None,
                                     dateOfReceiptTo: Option[LocalDate] = None,
                                     index: Int = DEFAULT_INDEX,
                                     maxRows: Int = DEFAULT_MAX_ROWS) {

  val startingPosition: Int = (index - 1) * maxRows

  private[models] def getSearchFields: Option[(String, String)] = {
    (searchKey, searchValue) match {
      case (Some(key), Some(value)) => Some(s"search.$key" -> value)
      case _ => None
    }
  }

  private[models] def getTraderRole: Option[(String, String)] = {
    val key = "search.traderRole"
    traderRole.flatMap {
      case MovementFilterDirectionOption.GoodsIn => Some(key -> MovementFilterDirectionOption.GoodsIn.toString)
      case MovementFilterDirectionOption.GoodsOut => Some(key -> MovementFilterDirectionOption.GoodsOut.toString)
      case _ => None
    }
  }

  private[models] def getUndischargedMovementsFlag: Option[(String, String)] = {
    val key = "search.undischargedMovements"
    undischargedMovements match {
      case Some(MovementFilterUndischargedOption.Undischarged) => Some(key -> MovementFilterUndischargedOption.Undischarged.toString)
      case _ => None
    }
  }

  private[models] def getMovementStatus: Option[(String, String)] = {
    val key = "search.movementStatus"
    movementStatus match {
      case Some(value) if value != MovementFilterStatusOption.ChooseStatus => Some(key -> value.toString)
      case _ => None
    }
  }

  private[models] def getEpc: Option[(String, String)] = {
    val key = "search.exciseProductCode"
    exciseProductCode match {
      case Some(value) if value != MovementListSearchOptions.CHOOSE_PRODUCT_CODE.code => Some(key -> value)
      case _ => None
    }
  }

  private[models] def getCountryOfOrigin: Option[(String, String)] = {
    val key = "search.countryOfOrigin"
    countryOfOrigin match {
      case Some(value) if value != MovementListSearchOptions.CHOOSE_COUNTRY.code => Some(key -> value)
      case _ => None
    }
  }

  def queryParams: Seq[(String, String)] = Seq(
    getSearchFields,
    getTraderRole,
    getUndischargedMovementsFlag,
    getMovementStatus,
    getEpc,
    getCountryOfOrigin,
    dateOfDispatchFrom.map(date => "search.dateOfDispatchFrom" -> MovementListSearchOptions.localDateToString(date)),
    dateOfDispatchTo.map(date => "search.dateOfDispatchTo" -> MovementListSearchOptions.localDateToString(date)),
    dateOfReceiptFrom.map(date => "search.dateOfReceiptFrom" -> MovementListSearchOptions.localDateToString(date)),
    dateOfReceiptTo.map(date => "search.dateOfReceiptTo" -> MovementListSearchOptions.localDateToString(date)),
    Some("search.sortOrder" -> sortBy.sortOrder),
    Some("search.sortField" -> sortBy.sortField),
    Some("search.startPosition" -> startingPosition.toString),
    Some("search.maxRows" -> maxRows.toString)
  ).flatten

}

object MovementListSearchOptions {

  private[models] def localDateToString(ld: LocalDate): String =
    s"${f"${ld.getDayOfMonth}%02d"}/${f"${ld.getMonthValue}%02d"}/${ld.getYear}" // pad day and month with leading zeros as needed

  private[models] def stringToLocalDate(s: String): LocalDate =
    LocalDate.parse(s, DateTimeFormatter.ofPattern("dd/MM/yyyy"))

  val DEFAULT_INDEX: Int = 1
  val DEFAULT_MAX_ROWS: Int = 10

  object CHOOSE_PRODUCT_CODE extends SelectOptionModel {
    override val code: String = "chooseProductCode"
    override val displayName: String = "viewAllMovements.filters.exciseProductCode.chooseProductCode"
  }

  object CHOOSE_COUNTRY extends SelectOptionModel {
    override val code: String = "chooseCountry"
    override val displayName: String = "viewAllMovements.filters.countryOfOrigin.chooseCountry"
  }

  implicit def queryStringBinder(implicit intBinder: QueryStringBindable[Int],
                                 stringBinder: QueryStringBindable[String]
                                ): QueryStringBindable[MovementListSearchOptions] =
    new QueryStringBindable[MovementListSearchOptions] {

      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, MovementListSearchOptions]] = {
        Some(for {
          sortOrder <- stringBinder.bind("sortBy", params).getOrElse(Right(ArcAscending.code))
          index <- intBinder.bind("index", params).getOrElse(Right(DEFAULT_INDEX))
          searchKey <- stringBinder.bind("searchKey", params).map(_.map(Some(_))).getOrElse(Right(None))
          searchValue <- stringBinder.bind("searchValue", params).map(_.map(Some(_))).getOrElse(Right(None))
          traderRole <- stringBinder.bind("traderRole", params).map(_.map(Some(_))).getOrElse(Right(None))
          undischargedMovements <- stringBinder.bind("undischargedMovements", params).map(_.map(Some(_))).getOrElse(Right(None))
          movementStatus <- stringBinder.bind("movementStatus", params).map(_.map(Some(_))).getOrElse(Right(None))
          exciseProductCode <- stringBinder.bind("exciseProductCode", params).map(_.map(Some(_))).getOrElse(Right(None))
          countryOfOrigin <- stringBinder.bind("countryOfOrigin", params).map(_.map(Some(_))).getOrElse(Right(None))
          dateOfDispatchFrom <- stringBinder.bind("dateOfDispatchFrom", params).map(_.map(Some(_))).getOrElse(Right(None))
          dateOfDispatchTo <- stringBinder.bind("dateOfDispatchTo", params).map(_.map(Some(_))).getOrElse(Right(None))
          dateOfReceiptFrom <- stringBinder.bind("dateOfReceiptFrom", params).map(_.map(Some(_))).getOrElse(Right(None))
          dateOfReceiptTo <- stringBinder.bind("dateOfReceiptTo", params).map(_.map(Some(_))).getOrElse(Right(None))
        } yield {
          MovementListSearchOptions(
            searchKey = searchKey.map(MovementSearchSelectOption(_)),
            searchValue = searchValue,
            sortBy = MovementSortingSelectOption(sortOrder),
            traderRole = traderRole.map(MovementFilterDirectionOption(_)),
            undischargedMovements = undischargedMovements.map(MovementFilterUndischargedOption(_)),
            movementStatus = movementStatus.map(MovementFilterStatusOption(_)),
            exciseProductCode = exciseProductCode,
            countryOfOrigin = countryOfOrigin,
            dateOfDispatchFrom = dateOfDispatchFrom.map(stringToLocalDate),
            dateOfDispatchTo = dateOfDispatchTo.map(stringToLocalDate),
            dateOfReceiptFrom = dateOfReceiptFrom.map(stringToLocalDate),
            dateOfReceiptTo = dateOfReceiptTo.map(stringToLocalDate),
            index = index,
            maxRows = DEFAULT_MAX_ROWS
          )
        })
      }

      override def unbind(key: String, searchOptions: MovementListSearchOptions): String =
        Seq(
          searchOptions.searchKey.map(field => stringBinder.unbind("searchKey", field.code)),
          searchOptions.searchValue.map(field => stringBinder.unbind("searchValue", field)),
          Some(stringBinder.unbind("sortBy", searchOptions.sortBy.code)),
          Some(intBinder.unbind("index", searchOptions.index)),
          searchOptions.traderRole.map(field => stringBinder.unbind("traderRole", field.code)),
          searchOptions.undischargedMovements.map(field => stringBinder.unbind("undischargedMovements", field.code)),
          searchOptions.movementStatus.map(field => stringBinder.unbind("movementStatus", field.code)),
          searchOptions.exciseProductCode.map(field => stringBinder.unbind("exciseProductCode", field)),
          searchOptions.countryOfOrigin.map(field => stringBinder.unbind("countryOfOrigin", field)),
          searchOptions.dateOfDispatchFrom.map(field => stringBinder.unbind("dateOfDispatchFrom", localDateToString(field))),
          searchOptions.dateOfDispatchTo.map(field => stringBinder.unbind("dateOfDispatchTo", localDateToString(field))),
          searchOptions.dateOfReceiptFrom.map(field => stringBinder.unbind("dateOfReceiptFrom", localDateToString(field))),
          searchOptions.dateOfReceiptTo.map(field => stringBinder.unbind("dateOfReceiptTo", localDateToString(field)))
        ).flatten.mkString("&")
    }

  //noinspection ScalaStyle
  def apply(
             searchKey: Option[String],
             searchValue: Option[String],
             sortBy: String,
             traderRoleOptions: Set[MovementFilterDirectionOption],
             undischargedMovementsOptions: Set[MovementFilterUndischargedOption],
             movementStatusOption: Option[MovementFilterStatusOption],
             exciseProductCodeOption: Option[String],
             countryOfOriginOption: Option[String],
             dateOfDispatchFrom: Option[LocalDate],
             dateOfDispatchTo: Option[LocalDate],
             dateOfReceiptFrom: Option[LocalDate],
             dateOfReceiptTo: Option[LocalDate]
           ): MovementListSearchOptions = {

    val traderRole: Option[MovementFilterDirectionOption] = {
      (traderRoleOptions.contains(MovementFilterDirectionOption.GoodsIn), traderRoleOptions.contains(MovementFilterDirectionOption.GoodsOut)) match {
        case (true, true) => Some(MovementFilterDirectionOption.All)
        case (true, _) => Some(MovementFilterDirectionOption.GoodsIn)
        case (_, true) => Some(MovementFilterDirectionOption.GoodsOut)
        case _ => None
      }
    }

    val undischargedMovements = undischargedMovementsOptions.toSeq match {
      case Seq(MovementFilterUndischargedOption.Undischarged) => Some(MovementFilterUndischargedOption.Undischarged)
      case _ => None
    }

    val movementStatus = movementStatusOption match {
      case Some(value) if value == MovementFilterStatusOption.ChooseStatus => None
      case value => value
    }

    val exciseProductCode = exciseProductCodeOption match {
      case Some(value) if value == CHOOSE_PRODUCT_CODE.code => None
      case value => value
    }

    val countryOfOrigin = countryOfOriginOption match {
      case Some(value) if value == CHOOSE_COUNTRY.code => None
      case value => value
    }

    MovementListSearchOptions(
      searchKey = searchKey.map(MovementSearchSelectOption(_)),
      searchValue = searchValue,
      sortBy = MovementSortingSelectOption(sortBy),
      traderRole = traderRole,
      undischargedMovements = undischargedMovements,
      movementStatus = movementStatus,
      exciseProductCode = exciseProductCode,
      countryOfOrigin = countryOfOrigin,
      dateOfDispatchFrom = dateOfDispatchFrom,
      dateOfDispatchTo = dateOfDispatchTo,
      dateOfReceiptFrom = dateOfReceiptFrom,
      dateOfReceiptTo = dateOfReceiptTo
    )
  }

  def unapply(options: MovementListSearchOptions): Option[(
    Option[String],
      Option[String],
      String,
      Set[MovementFilterDirectionOption],
      Set[MovementFilterUndischargedOption],
      Option[MovementFilterStatusOption],
      Option[String],
      Option[String],
      Option[LocalDate],
      Option[LocalDate],
      Option[LocalDate],
      Option[LocalDate]
    )] = {
    println(scala.Console.YELLOW + "options in unapply = " + options + scala.Console.RESET)

    Some((
      options.searchKey.map(_.code),
      options.searchValue,
      options.sortBy.code,
      options.traderRole.map(MovementFilterDirectionOption.toOptions).getOrElse(Set()),
      options.undischargedMovements.map(Set(_)).getOrElse(Set()),
      options.movementStatus,
      options.exciseProductCode,
      options.countryOfOrigin,
      options.dateOfDispatchFrom,
      options.dateOfDispatchTo,
      options.dateOfReceiptFrom,
      options.dateOfReceiptTo
    ))
  }

}


