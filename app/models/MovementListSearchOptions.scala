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

case class MovementListSearchOptions(searchKey: Option[MovementSearchSelectOption] = None,
                                     searchValue: Option[String] = None,
                                     sortBy: MovementSortingSelectOption = ArcAscending,
                                     traderRole: Option[MovementFilterDirectionOption] = None,
                                     undischargedMovements: Option[MovementFilterUndischargedOption] = None,
                                     index: Int = DEFAULT_INDEX,
                                     maxRows: Int = DEFAULT_MAX_ROWS) {

  val startingPosition: Int = (index - 1) * maxRows

  private def getSearchFields: Option[(String, String)] = {
    (searchKey, searchValue) match {
      case (Some(key), Some(value)) => Some(s"search.$key" -> value)
      case _ => None
    }
  }

  private def getTraderRole: Option[(String, String)] = {
    val key = "search.traderRole"
    traderRole.flatMap {
      case MovementFilterDirectionOption.GoodsIn => Some(key -> MovementFilterDirectionOption.GoodsIn.toString)
      case MovementFilterDirectionOption.GoodsOut => Some(key -> MovementFilterDirectionOption.GoodsOut.toString)
      case _ => None
    }
  }

  private def getUndischargedMovementsFlag: Option[(String, String)] = {
    val key = "search.undischargedMovements"
    undischargedMovements match {
      case Some(MovementFilterUndischargedOption.Undischarged) => Some(key -> MovementFilterUndischargedOption.Undischarged.toString)
      case _ => None
    }
  }

  def queryParams: Seq[(String, String)] = Seq(
    getSearchFields,
    getTraderRole,
    getUndischargedMovementsFlag,
    Some("search.sortOrder" -> sortBy.sortOrder),
    Some("search.sortField" -> sortBy.sortField),
    Some("search.startPosition" -> startingPosition.toString),
    Some("search.maxRows" -> maxRows.toString)
  ).flatten

}

object MovementListSearchOptions {

  val DEFAULT_INDEX: Int = 1
  val DEFAULT_MAX_ROWS: Int = 10

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
          _ = println(scala.Console.YELLOW + "undischargedMovements in queryStringBinder = " + undischargedMovements + scala.Console.RESET)
          _ = println(scala.Console.YELLOW + "params in queryStringBinder = " + params + scala.Console.RESET)
        } yield {
          MovementListSearchOptions(
            searchKey = searchKey.map(MovementSearchSelectOption(_)),
            searchValue = searchValue,
            sortBy = MovementSortingSelectOption(sortOrder),
            traderRole = traderRole.map(MovementFilterDirectionOption(_)),
            undischargedMovements = undischargedMovements.map(MovementFilterUndischargedOption(_)),
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
        ).flatten.mkString("&")
    }

  def apply(searchKey: Option[String], searchValue: Option[String], sortBy: String, traderRoleOptions: Set[MovementFilterDirectionOption], undischargedMovementsOptions: Set[MovementFilterUndischargedOption]): MovementListSearchOptions = {

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

    println(scala.Console.YELLOW + "undischargedMovementsOptions in apply = " + undischargedMovementsOptions + scala.Console.RESET)
    println(scala.Console.YELLOW + "undischargedMovements in apply = " + undischargedMovements + scala.Console.RESET)

    MovementListSearchOptions(
      searchKey = searchKey.map(MovementSearchSelectOption(_)),
      searchValue = searchValue,
      sortBy = MovementSortingSelectOption(sortBy),
      traderRole = traderRole,
      undischargedMovements = undischargedMovements
    )
  }

  def unapply(options: MovementListSearchOptions): Option[(Option[String], Option[String], String, Set[MovementFilterDirectionOption], Set[MovementFilterUndischargedOption])] = {
    println(scala.Console.YELLOW + "options in unapply = " + options + scala.Console.RESET)

    Some((
      options.searchKey.map(_.code),
      options.searchValue,
      options.sortBy.code,
      options.traderRole.map(MovementFilterDirectionOption.toOptions).getOrElse(Set()),
      options.undischargedMovements.map(Set(_)).getOrElse(Set())
    ))
  }

}


