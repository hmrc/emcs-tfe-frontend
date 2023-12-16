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

package models.response.emcsTfe

import models.common.OriginType
import play.api.libs.json.{Json, OFormat}
import utils.DateUtils

import java.time.{LocalDate, LocalTime}

case class EadEsadModel(
                         localReferenceNumber: String,
                         invoiceNumber: String,
                         invoiceDate: Option[String],
                         originTypeCode: OriginType,
                         dateOfDispatch: String,
                         timeOfDispatch: Option[String],
                         upstreamArc: Option[String],
                         importSadNumber: Option[Seq[String]]
                       ) extends DateUtils {

  def formattedInvoiceDate: Option[String] =
    invoiceDate.map { dateAsString =>
      LocalDate.parse(dateAsString).formatDateForUIOutput()
    }

  def formattedTimeOfDispatch: Option[String] =
    timeOfDispatch.map { timeAsString =>
      LocalTime.parse(timeAsString).formatTimeForUIOutput()
    }
}

object EadEsadModel {

  implicit val fmt: OFormat[EadEsadModel] = Json.format
}
