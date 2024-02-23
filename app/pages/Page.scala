/*
 * Copyright 2024 HM Revenue & Customs
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

package pages

import play.api.mvc.QueryStringBindable

import scala.language.implicitConversions

trait Page

object Page {

  implicit def toString(page: Page): String = page.toString

  implicit def queryStringBinder(implicit stringBinder: QueryStringBindable[String]): QueryStringBindable[Page] =
    new QueryStringBindable[Page] {

      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, Page]] = {

        stringBinder.bind("fromPage", params).map(theEither => {
          theEither.map(theString => {
            if (theString == "viewMessage") ViewMessagePage else ViewAllMessagesPage
          })
        })
      }

      override def unbind(key: String, page: Page): String = {
        Seq(
          Some(stringBinder.unbind("fromPage", Page.toString(page))),
        ).flatten.mkString("&")
      }
    }

}

case object ViewMessagePage extends Page {
  override val toString: String = "viewMessage"
}

case object ViewAllMessagesPage extends Page {
  override val toString: String = "viewAllMessages"
}
