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

package fixtures.messages.draftTemplates

import fixtures.messages.{BaseEnglish, i18n}

object ConfirmTemplateMessages {

  sealed trait ConfirmTemplateMessages extends BaseEnglish { _: i18n =>
    val heading = "Confirm if you want to use this template"
    val title = titleHelper(heading)
    val h1 = heading
    val summaryRowKeyMovementType = "Movement type"
    val summaryRowKeyConsignee = "Consignee"
    val summaryRowKeyConsigneeERN = "Consignee excise registration number (ERN)"
    val summaryRowKeyExportOffice = "Export office"
    val summaryRowKeyImportOffice = "Import office"
    val summaryRowKeyExemptedOrganisationOffice = "Exempted organisation office"
    val summaryRowKeyGuarantor = "Guarantor"
    val summaryRowKeyJourneyType = "Journey type"
    val summaryRowKeyTransportArranger = "Transport arranger"
    val summaryRowKeyFirstTransporter = "First transporter"
    val summaryRowKeyItems = "Items"
    val radioButton1 = "Yes"
    val radioButton2 = "No"
    val button = "Continue"
  }

  object English extends ConfirmTemplateMessages
}
