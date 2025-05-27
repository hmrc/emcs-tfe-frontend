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
import models.MovementSearchSelectOption
import models._
import play.api.data.Form
import play.api.data.Forms.{mapping, optional, set, text => playText}
import play.api.data.validation.{Constraint, Invalid, Valid}

import javax.inject.Inject

class ViewAllMovementsFormProvider @Inject() extends Mappings {

  def apply(): Form[MovementListSearchOptions] =
    Form(
      mapping(
        ViewAllMovementsFormProvider.searchKey -> optional(playText()).transform[Option[String]](_.map(removeAnyNonAlphanumerics), identity),
        ViewAllMovementsFormProvider.searchValue -> optional(playText().transform[String](_.trim, identity)),
        ViewAllMovementsFormProvider.sortBy -> enumerable[MovementSortingSelectOption](),
        ViewAllMovementsFormProvider.traderRole -> set(enumerable[MovementFilterDirectionOption]()),
        ViewAllMovementsFormProvider.undischarged -> set(enumerable[MovementFilterUndischargedOption]()),
        ViewAllMovementsFormProvider.status -> optional(enumerable[MovementFilterStatusOption]()),
        ViewAllMovementsFormProvider.exciseProductCode -> optional(text()).transform[Option[String]](_.map(removeAnyNonAlphanumerics), identity),
        ViewAllMovementsFormProvider.countryOfOrigin -> optional(text()).transform[Option[String]](_.map(removeAnyNonAlphanumerics), identity),
        ViewAllMovementsFormProvider.dateOfDispatchFrom -> optionalLocalDate(
          notARealDateKey = "viewAllMovements.filters.dateOfDispatchFrom.error.notARealDate",
          twoRequiredKey = "viewAllMovements.filters.dateOfDispatchFrom.error.required.two",
          oneRequiredKey = "viewAllMovements.filters.dateOfDispatchFrom.error.required.one",
          oneInvalidKey = "viewAllMovements.filters.dateOfDispatchFrom.error.invalid.one",
          allRequiredKey = ""
        ),
        ViewAllMovementsFormProvider.dateOfDispatchTo -> optionalLocalDate(
          notARealDateKey = "viewAllMovements.filters.dateOfDispatchTo.error.notARealDate",
          twoRequiredKey = "viewAllMovements.filters.dateOfDispatchTo.error.required.two",
          oneRequiredKey = "viewAllMovements.filters.dateOfDispatchTo.error.required.one",
          oneInvalidKey = "viewAllMovements.filters.dateOfDispatchTo.error.invalid.one",
          allRequiredKey = ""
        ),
        ViewAllMovementsFormProvider.dateOfReceiptFrom -> optionalLocalDate(
          notARealDateKey = "viewAllMovements.filters.dateOfReceiptFrom.error.notARealDate",
          twoRequiredKey = "viewAllMovements.filters.dateOfReceiptFrom.error.required.two",
          oneRequiredKey = "viewAllMovements.filters.dateOfReceiptFrom.error.required.one",
          oneInvalidKey = "viewAllMovements.filters.dateOfReceiptFrom.error.invalid.one",
          allRequiredKey = ""
        ),
        ViewAllMovementsFormProvider.dateOfReceiptTo -> optionalLocalDate(
          notARealDateKey = "viewAllMovements.filters.dateOfReceiptTo.error.notARealDate",
          twoRequiredKey = "viewAllMovements.filters.dateOfReceiptTo.error.required.two",
          oneRequiredKey = "viewAllMovements.filters.dateOfReceiptTo.error.required.one",
          oneInvalidKey = "viewAllMovements.filters.dateOfReceiptTo.error.invalid.one",
          allRequiredKey = ""
        )
      )(MovementListSearchOptions.apply)(MovementListSearchOptions.unapply).verifying(ViewAllMovementsFormProvider.validateSearchValue())
    )
}

object ViewAllMovementsFormProvider {

  // search input
  val sortBy = "sortBy"
  val searchKey = "searchKey"
  val searchValue = "searchValue"

  // filters
  val traderRole = "traderRole"
  val undischarged = "undischargedMovements"
  val status = "movementStatus"
  val exciseProductCode = "exciseProductCode"
  val countryOfOrigin = "countryOfOrigin"
  val dateOfDispatchFrom = "dateOfDispatchFrom"
  val dateOfDispatchTo = "dateOfDispatchTo"
  val dateOfReceiptFrom = "dateOfReceiptFrom"
  val dateOfReceiptTo = "dateOfReceiptTo"

  val ARC_MAX_LENGTH = 21
  val ERN_MAX_LENGTH = 16
  val LRN_MAX_LENGTH = 22
  val TRANSPORTER_MAX_LENGTH = 35

  val searchKeyRequiredMessage = "viewAllMovements.error.searchKey.required"
  val arcMaxLengthMessage = "viewAllMovements.error.searchValue.maxLength.arc"
  val ernMaxLengthMessage = "viewAllMovements.error.searchValue.maxLength.ern"
  val lrnMaxLengthMessage = "viewAllMovements.error.searchValue.maxLength.lrn"
  val transporterMaxLengthMessage = "viewAllMovements.error.searchValue.maxLength.transporter"

  def validateSearchValue(): Constraint[MovementListSearchOptions] = {
    import MovementSearchSelectOption._
    Constraint {
      case MovementListSearchOptions(searchKey, Some(searchValue), _, _, _, _, _, _, _, _, _, _) if searchKey.isEmpty || searchKey.contains(ChooseSearch.code) =>
        if(searchValue.trim.isEmpty) Valid else Invalid(searchKeyRequiredMessage)
      case MovementListSearchOptions(Some(ARC.code), Some(searchValue), _, _, _, _, _, _, _, _, _, _) if searchValue.length > ARC_MAX_LENGTH =>
        Invalid(arcMaxLengthMessage)
      case MovementListSearchOptions(Some(ERN.code), Some(searchValue), _, _, _, _, _, _, _, _, _, _) if searchValue.length > ERN_MAX_LENGTH =>
        Invalid(ernMaxLengthMessage)
      case MovementListSearchOptions(Some(LRN.code), Some(searchValue), _, _, _, _, _, _, _, _, _, _) if searchValue.length > LRN_MAX_LENGTH =>
        Invalid(lrnMaxLengthMessage)
      case MovementListSearchOptions(Some(Transporter.code), Some(searchValue), _, _, _, _, _, _, _, _, _, _) if searchValue.length > TRANSPORTER_MAX_LENGTH =>
        Invalid(transporterMaxLengthMessage)
      case _ =>
        Valid
    }
  }

}
