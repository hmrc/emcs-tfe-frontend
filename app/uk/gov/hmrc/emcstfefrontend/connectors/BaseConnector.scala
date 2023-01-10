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

package uk.gov.hmrc.emcstfefrontend.connectors

import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess, JsValue, Reads}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

import scala.util.{Success, Try}

trait BaseConnector {

  lazy val logger: Logger = Logger(this.getClass)

  implicit val httpReads: HttpReads[HttpResponse] = (method: String, url: String, response: HttpResponse) => response

  implicit class KnownJsonResponse(response: HttpResponse) {

    def validateJson[T](implicit reads: Reads[T]): Option[T] = {
      Try(response.json) match {
        case Success(json: JsValue) => parseResult(json)
        case _ =>
          logger.warn("[KnownJsonResponse][validateJson] No JSON was returned")
          None
      }
    }

    private def parseResult[T](json: JsValue)(implicit reads: Reads[T]): Option[T] = json.validate[T] match {

      case JsSuccess(value, _) => Some(value)
      case JsError(error) =>
        logger.warn(s"[KnownJsonResponse][validateJson] Unable to parse JSON: $error")
        None
    }
  }

}
