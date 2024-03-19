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

import play.api.libs.functional.syntax._
import play.api.libs.json._
import queries.{Derivable, Gettable, Settable}
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant

final case class UserAnswers(ern: String,
                             data: JsObject,
                             lastUpdated: Instant) {

  def get[A](page: Gettable[A])(implicit rds: Reads[A]): Option[A] =
    Reads.optionNoError(Reads.at(page.path)).reads(data).asOpt.flatten

  def get[A, B](query: Derivable[A, B])(implicit rds: Reads[A]): Option[B] =
    get(query.asInstanceOf[Gettable[A]]).map(query.derive)

  def set[A](page: Settable[A], value: A)(implicit writes: Writes[A]): UserAnswers =
    handleResult {
      data.setObject(page.path, Json.toJson(value))
    }

  def remove[A](page: Settable[A]): UserAnswers =
    handleResult {
      data.removeObject(page.path)
    }

  private[models] def handleResult: JsResult[JsObject] => UserAnswers = {
    case JsSuccess(updatedAnswers, _) =>
      copy(data = updatedAnswers)
    case JsError(errors) =>
      throw JsResultException(errors)
  }
}

object UserAnswers {

  val ernKey: String = "ern"
  val dataKey: String = "data"
  val lastUpdatedKey: String = "lastUpdated"

  val reads: Reads[UserAnswers] = (
    (__ \ ernKey).read[String] and
      (__ \ dataKey).read[JsObject] and
      (__ \ lastUpdatedKey).read(MongoJavatimeFormats.instantFormat)
    )(UserAnswers.apply _)

  val writes: OWrites[UserAnswers] = (
    (__ \ ernKey).write[String] and
      (__ \ dataKey).write[JsObject] and
      (__ \ lastUpdatedKey).write(MongoJavatimeFormats.instantFormat)
    )(unlift(UserAnswers.unapply))

  implicit val format: OFormat[UserAnswers] = OFormat(reads, writes)
}
