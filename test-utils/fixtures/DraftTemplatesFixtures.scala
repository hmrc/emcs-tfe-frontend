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

package fixtures

import models.draftTemplates.Template
import models.movementScenario.MovementScenario

trait DraftTemplatesFixtures {
  val templateList: Seq[Template] = Seq(
    Template("1", "Template 1", MovementScenario.UkTaxWarehouse.GB, Some("GB001234567890")),
    Template("2", "Template 2", MovementScenario.UkTaxWarehouse.GB, Some("GB001234567890")),
    Template("3", "Template 3", MovementScenario.UkTaxWarehouse.NI, Some("XI001234567890")),
    Template("4", "Template 4", MovementScenario.EuTaxWarehouse, Some("IE001234567890")),
    Template("5", "Template 5", MovementScenario.UkTaxWarehouse.GB, None),
    Template("6", "Template 6", MovementScenario.ExportWithCustomsDeclarationLodgedInTheUk, Some("GB001234567890")),
    Template("7", "Template 7", MovementScenario.ExportWithCustomsDeclarationLodgedInTheUk, Some("GB001234567890")),
    Template("8", "Template 8", MovementScenario.ExportWithCustomsDeclarationLodgedInTheUk, Some("XI001234567890")),
    Template("9", "Template 9", MovementScenario.ExportWithCustomsDeclarationLodgedInTheUk, Some("IE001234567890")),
    Template("10", "Template 10", MovementScenario.ExportWithCustomsDeclarationLodgedInTheUk, None)
  )
}
