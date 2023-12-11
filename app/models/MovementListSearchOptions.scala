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

package uk.gov.hmrc.emcstfefrontend.models

import play.api.mvc.QueryStringBindable
import uk.gov.hmrc.emcstfefrontend.models.MovementListSearchOptions.{DEFAULT_INDEX, DEFAULT_MAX_ROWS, DEFAULT_SORT_ORDER}
import uk.gov.hmrc.emcstfefrontend.models.MovementSortingSelectOption.Arc

case class MovementListSearchOptions(sortOrder: String = DEFAULT_SORT_ORDER,
                                     index: Int = DEFAULT_INDEX,
                                     maxCount: Int = DEFAULT_MAX_ROWS) {

  val startingPosition: Int = ((index-1) * maxCount) + 1

  val queryParams: Seq[(String, String)] = Seq(
    "search.sortOrder" -> sortOrder,
    "search.startingPosition" -> startingPosition.toString,
    "search.maxCount" -> maxCount.toString
  )
}

object MovementListSearchOptions {

  val DEFAULT_SORT_ORDER: String = Arc.code
  val DEFAULT_INDEX: Int = 1
  val DEFAULT_MAX_ROWS: Int = 10

  implicit def queryStringBinder(implicit intBinder: QueryStringBindable[Int],
                                 stringBinder: QueryStringBindable[String]): QueryStringBindable[MovementListSearchOptions] =
    new QueryStringBindable[MovementListSearchOptions] {

      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, MovementListSearchOptions]] = {
        Some(for {
          sortOrder <- stringBinder.bind("sortOrder", params).getOrElse(Right(DEFAULT_SORT_ORDER))
          index <- intBinder.bind("index", params).getOrElse(Right(DEFAULT_INDEX))
        } yield {
          MovementListSearchOptions(sortOrder, index, DEFAULT_MAX_ROWS)
        })
      }

      override def unbind(key: String, searchOptions: MovementListSearchOptions): String =
        Seq(
          stringBinder.unbind("sortOrder", searchOptions.sortOrder),
          intBinder.unbind("index", searchOptions.index)
        ).mkString("&")
    }
}


