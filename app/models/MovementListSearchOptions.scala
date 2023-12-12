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

case class MovementListSearchOptions(sortBy: MovementSortingSelectOption = ArcAscending,
                                     index: Int = DEFAULT_INDEX,
                                     maxRows: Int = DEFAULT_MAX_ROWS) {

  val startingPosition: Int = ((index-1) * maxRows) + 1

  val queryParams: Seq[(String, String)] = Seq(
    "search.sortOrder" -> sortBy.sortOrder,
    "search.sortField" -> sortBy.sortField,
    "search.startingPosition" -> startingPosition.toString,
    "search.maxRows" -> maxRows.toString
  )
}

object MovementListSearchOptions {

  val DEFAULT_INDEX: Int = 1
  val DEFAULT_MAX_ROWS: Int = 10

  implicit def queryStringBinder(implicit intBinder: QueryStringBindable[Int],
                                 stringBinder: QueryStringBindable[String]): QueryStringBindable[MovementListSearchOptions] =
    new QueryStringBindable[MovementListSearchOptions] {

      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, MovementListSearchOptions]] = {
        Some(for {
          sortOrder <- stringBinder.bind("sortBy", params).getOrElse(Right(ArcAscending.code))
          index <- intBinder.bind("index", params).getOrElse(Right(DEFAULT_INDEX))
        } yield {
          MovementListSearchOptions(MovementSortingSelectOption(sortOrder), index, DEFAULT_MAX_ROWS)
        })
      }

      override def unbind(key: String, searchOptions: MovementListSearchOptions): String =
        Seq(
          stringBinder.unbind("sortBy", searchOptions.sortBy.code),
          intBinder.unbind("index", searchOptions.index)
        ).mkString("&")
    }

  def apply(sortBy: String): MovementListSearchOptions = MovementListSearchOptions(
    sortBy = MovementSortingSelectOption(sortBy)
  )

  def unapply(options: MovementListSearchOptions): Option[String] =
    Some(options.sortBy.code)

}


