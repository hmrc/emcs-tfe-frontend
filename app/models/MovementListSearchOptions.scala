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
                                     index: Int = DEFAULT_INDEX,
                                     maxRows: Int = DEFAULT_MAX_ROWS) {

  val startingPosition: Int = ((index-1) * maxRows) + 1

  def getSearchFields: Option[(String, String)] = {
    (searchKey, searchValue) match {
      case (Some(key), Some(value)) => Some(s"search.$key" -> value)
      case _ => None
    }
  }

  val queryParams: Seq[(String, String)] = Seq(
    getSearchFields,
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
                                 stringBinder: QueryStringBindable[String]): QueryStringBindable[MovementListSearchOptions] =
    new QueryStringBindable[MovementListSearchOptions] {

      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, MovementListSearchOptions]] =
        Some(for {
          sortOrder <- stringBinder.bind("sortBy", params).getOrElse(Right(ArcAscending.code))
          index <- intBinder.bind("index", params).getOrElse(Right(DEFAULT_INDEX))
          searchKey <- stringBinder.bind("searchKey", params).map(_.map(Some(_))).getOrElse(Right(None))
          searchValue <- stringBinder.bind("searchValue", params).map(_.map(Some(_))).getOrElse(Right(None))
        } yield {
          MovementListSearchOptions(searchKey.map(MovementSearchSelectOption(_)), searchValue, MovementSortingSelectOption(sortOrder), index, DEFAULT_MAX_ROWS)
        })

      override def unbind(key: String, searchOptions: MovementListSearchOptions): String =
        Seq(
          searchOptions.searchKey.map(field => stringBinder.unbind("searchKey", field.code)),
          searchOptions.searchValue.map(field => stringBinder.unbind("searchValue", field)),
          Some(stringBinder.unbind("sortBy", searchOptions.sortBy.code)),
          Some(intBinder.unbind("index", searchOptions.index))
        ).flatten.mkString("&")
    }

  def apply(searchKey: Option[String], searchValue: Option[String], sortBy: String): MovementListSearchOptions =
    MovementListSearchOptions(
      searchKey = searchKey.map(MovementSearchSelectOption(_)),
      searchValue = searchValue,
      sortBy = MovementSortingSelectOption(sortBy)
    )

  def unapply(options: MovementListSearchOptions): Option[(Option[String], Option[String], String)] =
    Some((options.searchKey.map(_.code), options.searchValue, options.sortBy.code))

}


