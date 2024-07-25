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

package forms

import forms.mappings.Mappings
import models.draftMovements.{DestinationTypeSearchOption, GetDraftMovementsSearchOptions}
import play.api.data.Form
import play.api.data.Forms.{mapping, optional, set, text => playText}
import viewmodels.draftMovements.DraftMovementsErrorsOption

import javax.inject.Inject

class ViewAllDraftMovementsFormProvider @Inject() extends Mappings {

  def apply(): Form[GetDraftMovementsSearchOptions] =
    Form(
      mapping(
        ViewAllDraftMovementsFormProvider.sortByKey -> text().transform[String](removeAnyNonAlphanumerics, identity),
        ViewAllDraftMovementsFormProvider.searchValue -> optional(playText().verifying(regexpUnlessEmpty(XSS_REGEX, "error.invalidCharacter"))),
        ViewAllDraftMovementsFormProvider.draftHasErrors -> set(enumerable[DraftMovementsErrorsOption]()),
        ViewAllDraftMovementsFormProvider.destinationTypes -> set(enumerable[DestinationTypeSearchOption]()),
        ViewAllDraftMovementsFormProvider.exciseProductCode -> optional(text()).transform[Option[String]](_.map(removeAnyNonAlphanumerics), identity),
        ViewAllDraftMovementsFormProvider.dateOfDispatchFrom -> optionalLocalDate(
          invalidKey = "viewAllDraftMovements.filters.dateOfDispatchFrom.error.invalid",
          twoRequiredKey = "viewAllDraftMovements.filters.dateOfDispatchFrom.error.required.two",
          requiredKey = "viewAllDraftMovements.filters.dateOfDispatchFrom.error.required"
        ),
        ViewAllDraftMovementsFormProvider.dateOfDispatchTo -> optionalLocalDate(
          invalidKey = "viewAllDraftMovements.filters.dateOfDispatchTo.error.invalid",
          twoRequiredKey = "viewAllDraftMovements.filters.dateOfDispatchTo.error.required.two",
          requiredKey = "viewAllDraftMovements.filters.dateOfDispatchTo.error.required"
        )
      )(GetDraftMovementsSearchOptions.apply)(GetDraftMovementsSearchOptions.unapply)
  )
}

object ViewAllDraftMovementsFormProvider {

  val sortByKey = "sortBy"
  val searchValue = "searchValue"
  val draftHasErrors = "draftHasErrors"
  val destinationTypes = "destinationTypes"
  val exciseProductCode = "exciseProductCode"
  val dateOfDispatchFrom = "dateOfDispatchFrom"
  val dateOfDispatchTo = "dateOfDispatchTo"
}
