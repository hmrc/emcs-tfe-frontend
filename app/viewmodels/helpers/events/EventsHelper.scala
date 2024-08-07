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

package viewmodels.helpers.events

import models.DocumentType
import models.EventTypes._
import models.common.DestinationType
import models.requests.DataRequest
import models.response.emcsTfe.CancellationReasonType.Other
import models.response.emcsTfe.getMovementHistoryEvents.MovementHistoryEvent
import models.response.emcsTfe.reportOfReceipt.IE818ItemModelWithCnCodeInformation
import models.response.emcsTfe._
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Empty
import uk.gov.hmrc.http.BadRequestException
import utils.DateUtils
import viewmodels.helpers.TimelineHelper
import views.html.components.{bullets, link, p, summary_list}

import java.time.{LocalDateTime, ZoneOffset}
import javax.inject.Inject

class EventsHelper @Inject()(
                              timelineHelper: TimelineHelper,
                              movementEventHelper: MovementEventHelper,
                              link: link,
                              p: p,
                              summary_list: summary_list,
                              bullets: bullets) extends DateUtils {

  def getEventTitle(event: MovementHistoryEvent, movementResponse: GetMovementResponse)(implicit messages: Messages): String =
    (event.eventType, event.messageRole) match {
      case (IE819, _) => messages(s"${timelineHelper.getEventBaseKey(event)}.${getIE819EventDetail(event, movementResponse).notificationType}.label")
      case (IE871, _) => messages(s"${timelineHelper.getEventBaseKey(event)}.title")
      case _ => messages(timelineHelper.getEventTitleKey(event, movementResponse))
    }

  def constructEventInformation(
                                 event: MovementHistoryEvent,
                                 movement: GetMovementResponse,
                                 ie818ItemModelWithCnCodeInformation: Seq[IE818ItemModelWithCnCodeInformation] = Seq.empty,
                                 documentTypes: Seq[DocumentType] = Seq.empty,
                                 ie881ItemModelWithCnCodeInformation: Seq[IE881ItemModelWithCnCodeInformation] = Seq.empty
                               )(implicit request: DataRequest[_], messages: Messages): Html = {
    (event.eventType, event.messageRole) match {
      case (IE801, _) => ie801Html(event, movement)
      case (IE802, _) => ie802Html(event)
      case (IE803, _) => ie803Html(event, movement)
      case (IE807, _) => ie807Html(event, movement)
      case (IE810, _) => ie810Html(event, movement)
      case (IE813, _) => ie813Html(event, movement)
      case (IE818, _) => ie818Html(event, movement, ie818ItemModelWithCnCodeInformation)
      case (IE819, _) => ie819Html(event, movement)
      case (IE829, _) => ie829Html(event, movement)
      case (IE839, _) => ie839Html(event, movement)
      case (IE905, _) => ie905Html(event)
      case (IE837, _) => ie837Html(event, movement)
      case (IE871, _) => ie871Html(event, movement)
      case (IE881, _) => ie881Html(event, movement, documentTypes, ie881ItemModelWithCnCodeInformation)
      case _ => Empty.asHtml
    }
  }

  private def ie801Html(event: MovementHistoryEvent, movement: GetMovementResponse)(implicit request: DataRequest[_], messages: Messages): Html = {
    implicit val _movement: GetMovementResponse = movement

    HtmlFormat.fill(
      Seq(
        p(classes = "govuk-body-l")(Html(
          messages(s"${timelineHelper.getEventBaseKey(event)}.p1", event.sequenceNumber)
        )),
        printOrSaveThisMovement(movement),
        printThisPage(linkContentKey = "movementHistoryEvent.printLink", linkTrailingMessageKey = "movementHistoryEvent.printMessage"),
        movementEventHelper.movementInformationCard(),
        movementEventHelper.consignorInformationCard(),
        movementEventHelper.placeOfDispatchInformationCard(),
        movementEventHelper.importInformationCard(),
        movementEventHelper.consigneeInformationCard(),
        movementEventHelper.exemptedOrganisationInformationCard(),
        movementEventHelper.placeOfDestinationInformationCard(),
        movementEventHelper.exportInformationCard(),
        movementEventHelper.guarantorInformationCard(),
        movementEventHelper.journeyInformationCard(),
        movementEventHelper.transportArrangerInformationCard(),
        movementEventHelper.firstTransporterInformationCard(),
        movementEventHelper.transportUnitsInformationCard(),
        movementEventHelper.itemsInformationCard(),
        movementEventHelper.sadInformationCard(),
        movementEventHelper.documentsInformationCard()
      )
    )
  }

  private def printOrSaveThisMovement(movement: GetMovementResponse)(implicit request: DataRequest[_], messages: Messages) = {
    p()(HtmlFormat.fill(Seq(
      link(
        controllers.routes.ViewMovementController.printMovement(request.ern, movement.arc).url,
        messages("movementHistoryEvent.printOrSaveEad.link"),
      ),
      Html(messages("movementHistoryEvent.printOrSaveEad.message"))
    )))
  }

  private def ie802Html(event: MovementHistoryEvent)(implicit messages: Messages): Html =
    HtmlFormat.fill(
      Seq(
        p(classes = "govuk-body-l")(Html(messages(s"${timelineHelper.getEventBaseKey(event)}.p1"))),
        printThisPage(linkContentKey = "movementHistoryEvent.printLink", linkTrailingMessageKey = "movementHistoryEvent.printMessage")
      )
    )

  private def ie803Html(event: MovementHistoryEvent, movement: GetMovementResponse)(implicit messages: Messages): Html =
    HtmlFormat.fill(
      Seq(
        movement.notificationOfDivertedMovement.map { notificationOfDivertedMovement =>
          if (event.messageRole == 2) {
            //Split movement
            HtmlFormat.fill(Seq(
              p(classes = "govuk-body-l")(Html(messages(s"${timelineHelper.getEventBaseKey(event)}.p1", notificationOfDivertedMovement.notificationDateAndTime.toLocalDate.formatDateForUIOutput()))),
              p()(Html(messages(s"${timelineHelper.getEventBaseKey(event)}.p2"))),
              bullets(notificationOfDivertedMovement.downstreamArcs.map(Html(_)))
            ))
          } else {
            //Diverted movement
            HtmlFormat.fill(Seq(
              p(classes = "govuk-body-l")(Html(messages(s"${timelineHelper.getEventBaseKey(event)}.p1"))),
              p()(Html(messages(s"${timelineHelper.getEventBaseKey(event)}.p2", notificationOfDivertedMovement.notificationDateAndTime.toLocalDate.formatDateForUIOutput())))
            ))
          }
        },
        Some(printThisPage(linkContentKey = "movementHistoryEvent.printLink", linkTrailingMessageKey = "movementHistoryEvent.printMessage"))
      ).flatten
    )

  private def ie807Html(event: MovementHistoryEvent, movement: GetMovementResponse)(implicit messages: Messages): Html = {
    HtmlFormat.fill(
      Seq(
        movement.interruptedMovement.map { interruptedMovement =>
          HtmlFormat.fill(Seq(
            p(classes = "govuk-body-l")(Html(messages(s"${timelineHelper.getEventBaseKey(event)}.p1", movement.interruptedMovement.get.referenceNumberOfExciseOffice))),
            printThisPage(linkContentKey = "movementHistoryEvent.printLink", linkTrailingMessageKey = "movementHistoryEvent.printMessage")
          ) ++ Seq(
            Some(summary_list(Seq(
              Some(messages(s"${timelineHelper.getEventBaseKey(event)}.interruptionReason") -> messages(s"${timelineHelper.getEventBaseKey(event)}.reason.${interruptedMovement.reasonCode.toString}")),
              if (movement.interruptedMovement.get.reasonCode == InterruptionReasonType.Other) Some(messages(s"${timelineHelper.getEventBaseKey(event)}.interruptionExplanation") -> interruptedMovement.complementaryInformation.getOrElse("")) else None
            ).flatten))
          ).flatten)
        }
      ).flatten)
  }

  private def ie810Html(event: MovementHistoryEvent, movement: GetMovementResponse)(implicit messages: Messages): Html = {
    HtmlFormat.fill(
      Seq(
        movement.cancelMovement.map { cancelMovement =>
          HtmlFormat.fill(Seq(
            p(classes = "govuk-body-l")(Html(messages(s"${timelineHelper.getEventBaseKey(event)}.p1"))),
            printThisPage(linkContentKey = "movementHistoryEvent.printLink", linkTrailingMessageKey = "movementHistoryEvent.printMessage")
          ) ++ Seq(
            Some(summary_list(Seq(
              Some(messages(s"${timelineHelper.getEventBaseKey(event)}.cancellationReason") -> messages(s"${timelineHelper.getEventBaseKey(event)}.reason.${cancelMovement.reason.toString}")),
              if (movement.cancelMovement.get.reason == Other) Some(messages(s"${timelineHelper.getEventBaseKey(event)}.cancellationExplanation") -> cancelMovement.complementaryInformation.getOrElse("")) else None
            ).flatten))
          ).flatten)
        }
      ).flatten)
  }

  private def ie813Html(event: MovementHistoryEvent, movement: GetMovementResponse)(implicit request: DataRequest[_], messages: Messages): Html = {
    implicit val _movement: GetMovementResponse = movement

    HtmlFormat.fill(
      Seq(
        p(classes = "govuk-body-l")(Html(
          messages(s"${timelineHelper.getEventBaseKey(event)}.p1", event.sequenceNumber)
        )),
        printOrSaveThisMovement(movement),
        printThisPage(linkContentKey = "movementHistoryEvent.printLink", linkTrailingMessageKey = "movementHistoryEvent.printMessage"),
        movementEventHelper.movementInformationCard(),
        movementEventHelper.consigneeInformationCard(),
        movementEventHelper.placeOfDestinationInformationCard(),
        movementEventHelper.exportInformationCard(),
        movementEventHelper.guarantorInformationCard(),
        movementEventHelper.journeyInformationCard(),
        movementEventHelper.transportArrangerInformationCard(),
        movementEventHelper.firstTransporterInformationCard(),
        movementEventHelper.transportUnitsInformationCard()
      )
    )
  }

  private def ie818Html(
                         event: MovementHistoryEvent,
                         movement: GetMovementResponse,
                         ie818ItemModelWithCnCodeInformation: Seq[IE818ItemModelWithCnCodeInformation]
                       )(implicit request: DataRequest[_], messages: Messages): Html = {
    implicit val _movement: GetMovementResponse = movement

    val lede: String = if (movement.destinationType == DestinationType.Export) {
      messages(s"${timelineHelper.getEventBaseKey(event)}.lede.export")
    } else {
      messages(s"${timelineHelper.getEventBaseKey(event)}.lede")
    }

    HtmlFormat.fill(
      Seq(
        p(classes = "govuk-body-l")(Html(lede)),
        p()(Html(messages(s"${timelineHelper.getEventBaseKey(event)}.p1"))),
        printThisPage(linkContentKey = "movementHistoryEvent.printLink", linkTrailingMessageKey = "movementHistoryEvent.printMessage"),
        movementEventHelper.rorDetailsCard(event),
        movementEventHelper.consigneeInformationCard(),
        movementEventHelper.placeOfDestinationInformationCard(),
        movementEventHelper.exportInformationCard(),
        movementEventHelper.rorItemsCard(event, ie818ItemModelWithCnCodeInformation)
      )
    )
  }

  private def ie819Html(event: MovementHistoryEvent, movement: GetMovementResponse)(implicit messages: Messages): Html = {
    implicit val _movement: GetMovementResponse = movement
    val eventDetails = getIE819EventDetail(event, movement)

    HtmlFormat.fill(
      Seq(
        p(classes = "govuk-body-l")(Html(
          messages(s"${timelineHelper.getEventBaseKey(event)}.${eventDetails.notificationType}.p1", event.sequenceNumber)
        )),
        printThisPage(linkContentKey = "movementHistoryEvent.printLink", linkTrailingMessageKey = "movementHistoryEvent.printMessage"),
        movementEventHelper.alertRejectInformationCard(eventDetails),
        movementEventHelper.consigneeInformationCard()
      )
    )
  }

  private def ie829Html(event: MovementHistoryEvent, movement: GetMovementResponse)(implicit messages: Messages): Html = {
    implicit val _movement: GetMovementResponse = movement

    HtmlFormat.fill(
      Seq(
        movement.notificationOfAcceptedExport.map { notificationOfAcceptedExport =>
          HtmlFormat.fill(Seq(
            p(classes = "govuk-body-l")(Html(messages(s"${timelineHelper.getEventBaseKey(event)}.p1", notificationOfAcceptedExport.customsOfficeNumber))),
            printThisPage(linkContentKey = "movementHistoryEvent.printLink", linkTrailingMessageKey = "movementHistoryEvent.printMessage"),
            movementEventHelper.ie829AcceptedExportDetails(notificationOfAcceptedExport)
          ))
        }
      ).flatten
    )
  }

  private def ie839Html(event: MovementHistoryEvent, movement: GetMovementResponse)(implicit messages: Messages): Html = {
    implicit val _movement: GetMovementResponse = movement

    val rejectedMessage = movement.notificationOfCustomsRejection.flatMap(_.customsOfficeReferenceNumber).fold(
      messages(s"${timelineHelper.getEventBaseKey(event)}.p1")
    )(
      customsOfficeRefNumber => messages(s"${timelineHelper.getEventBaseKey(event)}.p1.customsOffice", customsOfficeRefNumber)
    )
    movement.notificationOfCustomsRejection.map { notificationOfCustomsRejection =>
      HtmlFormat.fill(
        Seq(
          p(classes = "govuk-body-l")(Html(rejectedMessage)),
          printThisPage(linkContentKey = "movementHistoryEvent.printLink", linkTrailingMessageKey = "movementHistoryEvent.printMessage"),
          movementEventHelper.customsRejectionInformationCard(notificationOfCustomsRejection),
          movementEventHelper.customsRejectionDiagnosisCards(notificationOfCustomsRejection),
          movementEventHelper.consigneeInformationCard(notificationOfCustomsRejection.consignee)
        )
      )
    }.getOrElse(Empty.asHtml)
  }

  private def ie905Html(event: MovementHistoryEvent)(implicit messages: Messages): Html = {
    HtmlFormat.fill(
      Seq(
        p(classes = "govuk-body-l")(Html(messages(s"${timelineHelper.getEventBaseKey(event)}.p1"))),
        printThisPage(linkContentKey = "movementHistoryEvent.printLink", linkTrailingMessageKey = "movementHistoryEvent.printMessage")
      )
    )
  }

  private def ie837Html(event: MovementHistoryEvent, movement: GetMovementResponse)(implicit messages: Messages): Html =
    HtmlFormat.fill(
      Seq(
        p(classes = "govuk-body-l")(Html(
          messages(s"${timelineHelper.getEventBaseKey(event)}.p1", event.sequenceNumber)
        )),
        printThisPage(linkContentKey = "movementHistoryEvent.printLink", linkTrailingMessageKey = "movementHistoryEvent.printMessage"),
        movementEventHelper.delayInformationCard(getIE837EventDetail(event, movement), event.messageRole)
      )
    )

  private def ie871Html(event: MovementHistoryEvent, movement: GetMovementResponse)
                       (implicit messages: Messages, request: DataRequest[_]): Html = {
    implicit val _movement = movement
    movement.notificationOfShortageOrExcess.map { notificationOfShortageOrExcess =>
      HtmlFormat.fill(
        Seq(
          p(classes = "govuk-body-l")(Html(
            messages(s"${timelineHelper.getEventBaseKey(event)}.p1", event.sequenceNumber)
          )),
          printThisPage(linkContentKey = "movementHistoryEvent.printLink", linkTrailingMessageKey = "movementHistoryEvent.printMessage"),
          movementEventHelper.ie871GlobalDetails(notificationOfShortageOrExcess),
          movementEventHelper.consignorInformationCard(),
          movementEventHelper.consigneeInformationCard(),
          movementEventHelper.ie871IndividualItemDetails(notificationOfShortageOrExcess, movement)
        )
      )
    }.getOrElse(Empty.asHtml)
  }

  private def ie881Html(event: MovementHistoryEvent, movement: GetMovementResponse, documentTypes: Seq[DocumentType], ie881ItemModelWithCnCodeInformation: Seq[IE881ItemModelWithCnCodeInformation])
                       (implicit request: DataRequest[_], messages: Messages): Html = {
    implicit val _movement = movement
    HtmlFormat.fill(
      Seq(
        p(classes = "govuk-body-l")(Html(
          messages(s"${timelineHelper.getEventBaseKey(event)}.p1", event.sequenceNumber)
        )),
        printThisPage(linkContentKey = "movementHistoryEvent.printLink", linkTrailingMessageKey = "movementHistoryEvent.printMessage"),
        movementEventHelper.responseInformation(),
        movementEventHelper.closureDocumentsInformationCard(documentTypes),
        movementEventHelper.manualClosureItemsCard(event, ie881ItemModelWithCnCodeInformation)
      )
    )
  }

  private def printThisPage(linkContentKey: String, linkTrailingMessageKey: String)(implicit messages: Messages): Html = {
    p(classes = "govuk-body js-visible govuk-!-display-none-print")(
      HtmlFormat.fill(
        Seq(
          link(classes = "govuk-link", link = "#", id = Some("print-link"), messageKey = linkContentKey),
          Html(messages(linkTrailingMessageKey))
        )
      )
    )
  }

  private def getIE819EventDetail(event: MovementHistoryEvent, movement: GetMovementResponse): NotificationOfAlertOrRejectionModel = {
    for {
      alertRejections <- movement.notificationOfAlertOrRejection
      dateTime = findClosestDate(alertRejections.map(_.notificationDateAndTime), event.eventDate)
      event <- alertRejections.find(_.notificationDateAndTime == dateTime)
    } yield event
  }.getOrElse(throw new BadRequestException(s"Unable to find the IE819 event details for the event for the date: ${event.eventDate}}"))

  private def getIE837EventDetail(event: MovementHistoryEvent, movement: GetMovementResponse): NotificationOfDelayModel = {
    for {
      notificationOfDelay <- movement.notificationOfDelay
      dateTime = findClosestDate(notificationOfDelay.map(_.dateTime), event.eventDate)
      event <- notificationOfDelay.find(_.dateTime == dateTime)
    } yield event
  }.getOrElse(throw new BadRequestException(s"Unable to find the IE837 event details for the event for the date: ${event.eventDate}}"))


  private[events] def findClosestDate(dateTimes: Seq[LocalDateTime], targetDateTime: LocalDateTime): LocalDateTime =
    dateTimes.minBy(date => math.abs(date.toEpochSecond(ZoneOffset.UTC) - targetDateTime.toEpochSecond(ZoneOffset.UTC)))


}
