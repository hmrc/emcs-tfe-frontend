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

package models.messages

import models.messages.MessagesSearchOptions.{DEFAULT_INDEX, DEFAULT_MAX_ROWS}
import models.messages.MessagesSortingSelectOption.DateReceivedD
import play.api.mvc.QueryStringBindable

case class MessagesSearchOptions(sortBy: MessagesSortingSelectOption = DateReceivedD,
                                 index: Int = DEFAULT_INDEX,
                                 maxRows: Int = DEFAULT_MAX_ROWS) {

  val queryParams: Seq[(String, String)] = Seq(
    Some("sortField" -> sortBy.sortField),
    Some("sortOrder" -> sortBy.sortOrder),
    Some("page" -> index.toString),
  ).flatten

}

object MessagesSearchOptions {

  val DEFAULT_INDEX: Int = 1
  val DEFAULT_MAX_ROWS: Int = 10

  implicit def queryStringBinder(implicit intBinder: QueryStringBindable[Int],
                                 stringBinder: QueryStringBindable[String]): QueryStringBindable[MessagesSearchOptions] =
    new QueryStringBindable[MessagesSearchOptions] {

      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, MessagesSearchOptions]] =
        Some(for {
          sortOrder <- stringBinder.bind("sortBy", params).getOrElse(Right(DateReceivedD.code))
          page <- intBinder.bind("index", params).getOrElse(Right(DEFAULT_INDEX))
        } yield {
          MessagesSearchOptions(MessagesSortingSelectOption(sortOrder), page, DEFAULT_MAX_ROWS)
        })

      override def unbind(key: String, searchOptions: MessagesSearchOptions): String =
        Seq(
          Some(stringBinder.unbind("sortBy", searchOptions.sortBy.code)),
          Some(intBinder.unbind("index", searchOptions.index))
        ).flatten.mkString("&")
    }

  def apply(sortBy: String): MessagesSearchOptions =
    MessagesSearchOptions(
      sortBy = MessagesSortingSelectOption(sortBy)
    )

  def unapply(options: MessagesSearchOptions): Option[String] =
    Some(options.sortBy.code)
}


