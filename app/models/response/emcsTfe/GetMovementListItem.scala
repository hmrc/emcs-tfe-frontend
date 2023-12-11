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
import play.api.libs.json.{Json, Reads}
import uk.gov.hmrc.govukfrontend.views.html.components.Text
import play.api.mvc.Call
import controllers.routes
import uk.gov.hmrc.emcstfefrontend.utils.DateUtils
import uk.gov.hmrc.emcstfefrontend.viewmodels.govuk.TagFluency
import uk.gov.hmrc.govukfrontend.views.viewmodels.tag.Tag

import java.time.LocalDateTime

case class GetMovementListItem(arc: String,
                               dateOfDispatch: LocalDateTime,
                               movementStatus: String,
                               otherTraderID: String) extends TagFluency with DateUtils {

  def viewMovementUrl(ern: String): Call = routes.ViewMovementController.viewMovementOverview(ern, arc)

  val formattedDateOfDispatch: String = dateOfDispatch.toLocalDate.formatDateForUIOutput()

  val statusTag: Tag = (movementStatus match {
    case "Accepted" =>
      TagViewModel(Text(movementStatus)).blue()
    case "Deemed exported" | "Diverted" | "Exporting" =>
      TagViewModel(Text(movementStatus)).green()
    case "Partially refused" | "Refused" | "Rejected" =>
      TagViewModel(Text(movementStatus)).orange()
    case "Cancelled" | "Manually closed" | "Replaced" | "Stopped" =>
      TagViewModel(Text(movementStatus)).purple()
    case _ =>
      TagViewModel(Text(movementStatus))
  }).withCssClass("govuk-!-margin-top-5")
}

object GetMovementListItem {

  implicit val reads: Reads[GetMovementListItem] = Json.reads
}
