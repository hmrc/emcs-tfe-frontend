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
import models._
import play.api.data.Form
import play.api.data.Forms.{mapping, optional, set, text => playText}

import javax.inject.Inject

class ViewAllMovementsFormProvider @Inject() extends Mappings {

  def apply(): Form[MovementListSearchOptions] =
    Form(
      mapping(
        ViewAllMovementsFormProvider.searchKey -> optional(playText()).transform[Option[String]](_.map(removeAnyNonAlphanumerics), identity),
        ViewAllMovementsFormProvider.searchValue ->
          optional(playText().verifying(regexpUnlessEmpty(XSS_REGEX, "error.invalidCharacter")))
            .transform[Option[String]](_.map(removeAnyQueryParamCharacters), identity),
        ViewAllMovementsFormProvider.sortByKey -> text().transform[String](removeAnyNonAlphanumerics, identity),
        ViewAllMovementsFormProvider.traderRole -> set(enumerable[MovementFilterDirectionOption]()),
        ViewAllMovementsFormProvider.undischarged -> set(enumerable[MovementFilterUndischargedOption]()),
        ViewAllMovementsFormProvider.status -> optional(enumerable[MovementFilterStatusOption]()),
        ViewAllMovementsFormProvider.exciseProductCode -> optional(text()).transform[Option[String]](_.map(removeAnyQueryParamCharacters), identity),
        ViewAllMovementsFormProvider.countryOfOrigin -> optional(text()).transform[Option[String]](_.map(removeAnyQueryParamCharacters), identity),
        ViewAllMovementsFormProvider.dateOfDispatchFrom -> optionalLocalDate(
          invalidKey     = "viewAllMovements.filters.dateOfDispatchFrom.error.invalid",
          twoRequiredKey = "viewAllMovements.filters.dateOfDispatchFrom.error.required.two",
          requiredKey    = "viewAllMovements.filters.dateOfDispatchFrom.error.required"
        ),
        ViewAllMovementsFormProvider.dateOfDispatchTo -> optionalLocalDate(
          invalidKey     = "viewAllMovements.filters.dateOfDispatchTo.error.invalid",
          twoRequiredKey = "viewAllMovements.filters.dateOfDispatchTo.error.required.two",
          requiredKey    = "viewAllMovements.filters.dateOfDispatchTo.error.required"
        ),
        ViewAllMovementsFormProvider.dateOfReceiptFrom -> optionalLocalDate(
          invalidKey     = "viewAllMovements.filters.dateOfReceiptFrom.error.invalid",
          twoRequiredKey = "viewAllMovements.filters.dateOfReceiptFrom.error.required.two",
          requiredKey    = "viewAllMovements.filters.dateOfReceiptFrom.error.required"
        ),
        ViewAllMovementsFormProvider.dateOfReceiptTo -> optionalLocalDate(
          invalidKey     = "viewAllMovements.filters.dateOfReceiptTo.error.invalid",
          twoRequiredKey = "viewAllMovements.filters.dateOfReceiptTo.error.required.two",
          requiredKey    = "viewAllMovements.filters.dateOfReceiptTo.error.required"
        )
      )(MovementListSearchOptions.apply)(MovementListSearchOptions.unapply)
    )

  /**
   * As these form values will be used as query parameters, we will need to silently guard against users entering `&` or `/` for example,
   * this function will replace any non-alphanumeric with a blank value e.g. "value&unexpected/parameter" -> "valueunexpectedparameter
   */
    // TODO: add to draft page too?
  private def removeAnyQueryParamCharacters(rawString: String): String = rawString.replaceAll("[/&?=]", "")

  private def removeAnyNonAlphanumerics(rawString: String): String = rawString.replaceAll("[^A-Za-z0-9]", "")
}

object ViewAllMovementsFormProvider {

  // search input
  val sortByKey = "sortBy"
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

}
