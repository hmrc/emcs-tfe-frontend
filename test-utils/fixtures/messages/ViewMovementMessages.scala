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

package fixtures.messages

object ViewMovementMessages {

  sealed trait ViewMessages { _: i18n =>
    val title: String = "Movement details"
    val arcSubheading: String = "Administrative reference code"
    val overviewTabHeading = "Overview"

    val overviewCardLrn = "Local Reference Number (LRN)"
    val overviewCardEadStatus = "Electronic administrative document (eAD) status"
    val overviewCardDateOfDispatch = "Date of dispatch"
    val overviewCardExpectedDate = "Expected date of arrival"
    val overviewCardConsignor = "Consignor"
    val overviewCardNumberOfItems = "Number of items"
    val overviewCardTransporting = "Transporting vehicle(s)"

    val actionLinkSubmitReportOfReceipt = "Submit report of receipt"
    val actionLinkExplainDelay = "Explain a delay"
    val actionLinkExplainShortageOrExcess = "Explain a shortage or excess"
    val actionLinCancelMovement = "Cancel this movement"
    val actionLinkChangeOfDestination = "Submit a change of destination"
    val actionLinkAlertOrRejection = "Submit alert or rejection"
  }

  object English extends ViewMessages with BaseEnglish
}
