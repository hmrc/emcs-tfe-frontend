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

package viewmodels.helpers

import config.AppConfig
import models.MovementEadStatus._
import models.common.DestinationType.Export
import models.requests.DataRequest
import models.response.emcsTfe.GetMovementResponse
import play.api.i18n.Messages
import play.twirl.api.Html
import utils.Logging
import viewmodels.helpers.ViewMovementActionsHelper._
import views.html.components.{link, list}

import java.time.LocalDate
import javax.inject.Inject

class ViewMovementActionsHelper @Inject()(
                                           list: list,
                                           link: link,
                                           appConfig: AppConfig
                                         ) extends Logging {

  def movementActions(movement: GetMovementResponse)(implicit request: DataRequest[_], messages: Messages): Html = {
    val isConsignor = movement.consignorTrader.traderExciseNumber.fold(false)(_ == request.ern)

    if (isConsignor) {
      list(
        content = Seq(
          cancelMovementLink(movement),
          changeDestinationLink(movement),
          explainADelayLink(movement),
          shortageOrExcessLink(movement)
          // ETFE-2556 will re-enable this link
          // printLink()
        ).flatten,
        extraClasses = Some("govuk-list--spaced")
      )

    } else {
      list(
        content = Seq(
          alertOrRejectionLink(movement),
          reportOfReceiptLink(movement),
          explainADelayLink(movement),
          shortageOrExcessLink(movement)
          // ETFE-2556 will re-enable this link
          // printLink()
        ).flatten,
        extraClasses = Some("govuk-list--spaced")
      )
    }
  }

  def cancelMovementLink(movement: GetMovementResponse)(implicit request: DataRequest[_], messages: Messages): Option[Html] = {
    val splitMovement: Boolean = isASplitMovement(movement)
    val movementStatusValid: Boolean = cancelMovementValidStatuses.contains(movement.eadStatus)
    val dispatchDateValid: Boolean = dateOfDispatchTodayOrInTheFuture(movement.dateOfDispatch)

    Option.when(!splitMovement && movementStatusValid && dispatchDateValid) {
      link(appConfig.emcsTfeCancelMovementUrl(request.ern, movement.arc), "viewMovement.cancelMovement", Some("cancel-this-movement"), hintKey = Some("viewMovement.cancelMovement.info"))
    }
  }

  def changeDestinationLink(movement: GetMovementResponse)(implicit request: DataRequest[_], messages: Messages): Option[Html] = {
    Option.when(changeDestinationValidStatuses.contains(movement.eadStatus)) {
      link(appConfig.emcsTfeChangeDestinationUrl(request.ern, movement.arc), "viewMovement.changeDestination", Some("submit-a-change-of-destination"), hintKey = Some("viewMovement.changeDestination.info"))
    }
  }

  def alertOrRejectionLink(movement: GetMovementResponse)(implicit request: DataRequest[_], messages: Messages): Option[Html] = {
    Option.when(alertOrRejectValidStatuses.contains(movement.eadStatus)) {
      link(appConfig.emcsTfeAlertOrRejectionUrl(request.ern, movement.arc), "viewMovement.alertOrRejection", Some("submit-alert-or-rejection"), hintKey = Some("viewMovement.alertOrRejection.info"))
    }
  }

  def reportOfReceiptLink(movement: GetMovementResponse)(implicit request: DataRequest[_], messages: Messages): Option[Html] = {
    Option.when(reportOfReceiptValidStatuses.contains(movement.eadStatus)) {
      link(appConfig.emcsTfeReportAReceiptUrl(request.ern, movement.arc), "viewMovement.reportAReceipt", Some("submit-report-of-receipt"), hintKey = Some("viewMovement.reportAReceipt.info"))
    }
  }

  def explainADelayLink(movement: GetMovementResponse)(implicit request: DataRequest[_], messages: Messages): Option[Html] = {
    Option.when(cond = true) {
      link(appConfig.emcsTfeExplainDelayUrl(request.ern, movement.arc), "viewMovement.explainDelay", Some("explain-a-delay"), hintKey = Some("viewMovement.explainDelay.info"))
    }
  }

  def shortageOrExcessLink(movement: GetMovementResponse)(implicit request: DataRequest[_], messages: Messages): Option[Html] = {
    val linkValid = movement.destinationType match {
      case Export => shortageOrExcessExportValidStatuses.contains(movement.eadStatus)
      case _      => shortageOrExcessValidStatuses.contains(movement.eadStatus)
    }

    Option.when(linkValid) {
      link(appConfig.emcsTfeExplainShortageOrExcessUrl(request.ern, movement.arc), "viewMovement.explainShortageOrExcess", Some("explain-shortage-or-excess"), hintKey = Some("viewMovement.explainShortageOrExcess.info"))
    }
  }

  def printLink()(implicit messages: Messages): Option[Html] =
    Option.when(cond = true) {
      link("print", "viewMovement.printOrSaveEad", Some("print-or-save-ead"), hintKey = Some("viewMovement.printOrSaveEad.info"))
    }
}

object ViewMovementActionsHelper {


  private def dateOfDispatchTodayOrInTheFuture(dateOfDispatch: LocalDate): Boolean =
    dateOfDispatch.isEqual(LocalDate.now()) || dateOfDispatch.isAfter(LocalDate.now())

  private def isASplitMovement(movement: GetMovementResponse): Boolean =
    movement.eadEsad.upstreamArc.isDefined
}
