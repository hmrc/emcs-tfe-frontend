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

package models.messages

import models.response.emcsTfe.messages.Message
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json._
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant

case class MessageCache (
                          ern:String,
                          message: Message,
                          lastUpdated: Instant = Instant.now
                        )

object MessageCache {
   val reads: Reads[MessageCache] = (
      (JsPath \ "ern").read[String] and
      (JsPath \ "message").read[Message] and
      (JsPath \ "lastUpdated").read(MongoJavatimeFormats.instantFormat)
    )(MessageCache.apply _)

   val writes: Writes[MessageCache] = (
    (JsPath \ "ern").write[String] and
      (JsPath \ "message").write[Message] and
      (JsPath \ "lastUpdated").write(MongoJavatimeFormats.instantFormat)
    )(unlift(MessageCache.unapply))

  implicit val format: Format[MessageCache] = Format(reads, writes)

}