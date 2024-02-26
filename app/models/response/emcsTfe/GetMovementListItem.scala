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

import controllers.routes
import play.api.i18n.Messages
import play.api.libs.json.{Json, Reads}
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.html.components.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.tag.Tag
import utils.DateUtils
import viewmodels.govuk.TagFluency

import java.time.LocalDateTime

case class GetMovementListItem(arc: String,
                               dateOfDispatch: LocalDateTime,
                               movementStatus: String,
                               otherTraderID: String) extends TagFluency with DateUtils {

  def viewMovementUrl(ern: String): Call = routes.ViewMovementController.viewMovementOverview(ern, arc)

  val formattedDateOfDispatch: String = dateOfDispatch.toLocalDate.formatDateForUIOutput()

  def statusTag()(implicit messages: Messages): Tag = {

    val displayName = messages(s"viewAllMovements.filters.status.${movementStatus.toLowerCase}")

    (movementStatus.toLowerCase match {
      case "accepted" =>
        TagViewModel(Text(displayName)).blue()
      case "deemedexported" | "diverted" | "exporting" =>
        TagViewModel(Text(displayName)).green()
      case "partiallyrefused" | "refused" | "rejected" =>
        TagViewModel(Text(displayName)).orange()
      case "cancelled" | "manuallyclosed" | "replaced" | "stopped" =>
        TagViewModel(Text(displayName)).purple()
      case _ =>
        TagViewModel(Text(movementStatus))
    }).withCssClass("govuk-!-margin-top-5")
  }
}

object GetMovementListItem {

  implicit val reads: Reads[GetMovementListItem] = Json.reads
}
