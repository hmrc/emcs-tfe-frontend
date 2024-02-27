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

package viewmodels.helpers.messages

import base.SpecBase
import fixtures.messages.ViewMessageMessages
import fixtures.{GetMovementResponseFixtures, GetSubmissionFailureMessageFixtures, MessagesFixtures}
import models.messages.MessageCache
import models.requests.DataRequest
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases.{Key, SummaryListRow, Text, Value}
import uk.gov.hmrc.govukfrontend.views.html.components._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import views.html.components._

class ViewMessageHelperSpec extends SpecBase
  with MessagesFixtures
  with GetMovementResponseFixtures
  with GetSubmissionFailureMessageFixtures {

  import GetSubmissionFailureMessageResponseFixtures._

  implicit lazy val msgs: Messages = messages(Seq(ViewMessageMessages.English.lang))
  implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

  lazy val helper: ViewMessageHelper = app.injector.instanceOf[ViewMessageHelper]

  lazy val govukTable: GovukTable = app.injector.instanceOf[GovukTable]
  lazy val link: link = app.injector.instanceOf[link]
  lazy val list: list = app.injector.instanceOf[list]
  lazy val h2: h2 = app.injector.instanceOf[h2]
  lazy val p: p = app.injector.instanceOf[p]
  lazy val warningText: warning_text = app.injector.instanceOf[warning_text]
  lazy val govukSummaryList: GovukSummaryList = app.injector.instanceOf[GovukSummaryList]

  private def removeNewLines(input: String): String = {
    input.replaceAll("\n", "")
  }

  ".constructMovementInformation" when {

    "a message type description is present" in {
      val testMessageCache = MessageCache(testErn, ie819ReceivedAlert.message, None)
      val result: Html = helper.constructMovementInformation(testMessageCache)
      removeNewLines(result.toString()) mustBe removeNewLines(govukSummaryList(SummaryList(Seq(
        SummaryListRow(
          key = Key(Text(value = ViewMessageMessages.English.labelMessageType)),
          value = Value(Text(value = ViewMessageMessages.English.messageTypeDescriptionForIE819))
        ),
        SummaryListRow(
          key = Key(Text(value = ViewMessageMessages.English.labelArc)),
          value = Value(Text(value = testMessageCache.message.arc.get))
        ),
        SummaryListRow(
          key = Key(Text(value = ViewMessageMessages.English.labelLrn)),
          value = Value(Text(value = testMessageCache.message.lrn.get))
        )
      ))).toString())
    }

    "a message type description is not present" in {
      val testMessageCache = MessageCache(testErn, ie829ReceivedCustomsAcceptance.message, None)
      val result: Html = helper.constructMovementInformation(testMessageCache)
      removeNewLines(result.toString()) mustBe removeNewLines(govukSummaryList(SummaryList(Seq(
        SummaryListRow(
          key = Key(Text(value = ViewMessageMessages.English.labelArc)),
          value = Value(Text(value = testMessageCache.message.arc.get))
        ),
        SummaryListRow(
          key = Key(Text(value = ViewMessageMessages.English.labelLrn)),
          value = Value(Text(value = testMessageCache.message.lrn.get))
        )
      ))).toString())
    }

    "show a H2 when the message is a 704 and the error message relates to an IE815" in {
      val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
        relatedMessageType = Some("IE815")
      )
      val testMessageCache = MessageCache(testErn, ie704ErrorCreateMovementIE815.message, Some(failureMessageResponse))
      val result: Html = helper.constructMovementInformation(testMessageCache)
      removeNewLines(result.toString()) mustBe removeNewLines(
        HtmlFormat.fill(Seq(
          h2(ViewMessageMessages.English.movementInformationHeading),
          govukSummaryList(SummaryList(Seq(
            SummaryListRow(
              key = Key(Text(value = ViewMessageMessages.English.labelLrn)),
              value = Value(Text(value = testMessageCache.message.lrn.get))
            ))
          )))).toString())
    }

    "fallback to the LRN in the error message when the message itself doesn't have an LRN (4402 error)" in {
      val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
        relatedMessageType = Some("IE815")
      )
      val testMessageCache = MessageCache(testErn, ie704ErrorCreateMovementIE815.message.copy(lrn = None), Some(failureMessageResponse))
      val result: Html = helper.constructMovementInformation(testMessageCache)
      removeNewLines(result.toString()) mustBe removeNewLines(
        HtmlFormat.fill(Seq(
          h2(ViewMessageMessages.English.movementInformationHeading),
          govukSummaryList(SummaryList(Seq(
            SummaryListRow(
              key = Key(Text(value = ViewMessageMessages.English.labelLrn)),
              value = Value(Text(value = failureMessageResponse.ie704.body.attributes.get.lrn.get))
            ))
          )))).toString())
    }

  }

  ".constructActions" must {

    "return the correct action links" when {

      "when processing an IE813 notification" in {
        val testMessageCache = MessageCache(testErn, ie813ReceivedChangeDestination.message, None)

        val result: Html = helper.constructActions(testMessageCache, None)

        result mustBe
          HtmlFormat.fill(
            Seq(
              list(
                extraClasses = Some("govuk-!-display-none-print"),
                content = Seq(
                  link(
                    link = appConfig.emcsTfeReportAReceiptUrl(request.ern, testMessageCache.message.arc.getOrElse("")),
                    messageKey = "viewMessage.link.reportOfReceipt.description",
                    id = Some("submit-report-of-receipt")
                  ),
                  link(
                    link = appConfig.emcsTfeExplainDelayUrl(request.ern, testMessageCache.message.arc.getOrElse("")),
                    messageKey = "viewMessage.link.explainDelay.description",
                    id = Some("submit-explain-delay")
                  ),
                  link(
                    link = controllers.routes.ViewMovementController.viewMovementOverview(request.ern, testMessageCache.message.arc.getOrElse("")).url,
                    messageKey = "viewMessage.link.viewMovement.description",
                    id = Some("view-movement")
                  ),
                  link(
                    link = "#print-dialogue",
                    messageKey = "viewMessage.link.printMessage.description",
                    id = Some("print-link")
                  ),
                  link(
                    link = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
                    messageKey = "viewMessage.link.deleteMessage.description",
                    id = Some("delete-message")
                  )
                )
              )
            )
          )
      }
      "when processing an IE829 notification" in {
        val testMessageCache = MessageCache(testErn, ie829ReceivedCustomsAcceptance.message, None)

        val result: Html = helper.constructActions(testMessageCache, None)

        result mustBe
          HtmlFormat.fill(
            Seq(
              list(
                extraClasses = Some("govuk-!-display-none-print"),
                content = Seq(
                  link(
                    link = appConfig.emcsTfeChangeDestinationUrl(request.ern, testMessageCache.message.arc.getOrElse("")),
                    messageKey = "viewMessage.link.changeDestination.description",
                    id = Some("submit-change-destination")
                  ),
                  link(
                    link = controllers.routes.ViewMovementController.viewMovementOverview(request.ern, testMessageCache.message.arc.getOrElse("")).url,
                    messageKey = "viewMessage.link.viewMovement.description",
                    id = Some("view-movement")
                  ),
                  link(
                    link = "#print-dialogue",
                    messageKey = "viewMessage.link.printMessage.description",
                    id = Some("print-link")
                  ),
                  link(
                    link = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
                    messageKey = "viewMessage.link.deleteMessage.description",
                    id = Some("delete-message")
                  )
                )
              )
            )
          )
      }
      "when processing an IE819 notification" in {
        val testMessageCache = MessageCache(testErn, ie819SubmittedAlert.message, None)

        val result: Html = helper.constructActions(testMessageCache, None)

        result mustBe
          HtmlFormat.fill(
            Seq(
              list(
                extraClasses = Some("govuk-!-display-none-print"),
                content = Seq(
                  link(
                    link = controllers.routes.ViewMovementController.viewMovementOverview(request.ern, testMessageCache.message.arc.getOrElse("")).url,
                    messageKey = "viewMessage.link.viewMovement.description",
                    id = Some("view-movement")
                  ),
                  link(
                    link = "#print-dialogue",
                    messageKey = "viewMessage.link.printMessage.description",
                    id = Some("print-link")
                  ),
                  link(
                    link = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
                    messageKey = "viewMessage.link.deleteMessage.description",
                    id = Some("delete-message")
                  )
                )
              )
            )
          )
      }
      "when processing an IE837 for a report of receipt" in {
        val testMessageCache = MessageCache(testErn, ie837SubmittedExplainDelayROR.message, None)

        val result: Html = helper.constructActions(testMessageCache, None)

        result mustBe
          HtmlFormat.fill(
            Seq(
              list(
                extraClasses = Some("govuk-!-display-none-print"),
                content = Seq(
                  link(
                    link = appConfig.emcsTfeReportAReceiptUrl(request.ern, testMessageCache.message.arc.getOrElse("")),
                    messageKey = "viewMessage.link.reportOfReceipt.description",
                    id = Some("submit-report-of-receipt")
                  ),
                  link(
                    link = controllers.routes.ViewMovementController.viewMovementOverview(request.ern, testMessageCache.message.arc.getOrElse("")).url,
                    messageKey = "viewMessage.link.viewMovement.description",
                    id = Some("view-movement")
                  ),
                  link(
                    link = "#print-dialogue",
                    messageKey = "viewMessage.link.printMessage.description",
                    id = Some("print-link")
                  ),
                  link(
                    link = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
                    messageKey = "viewMessage.link.deleteMessage.description",
                    id = Some("delete-message")
                  )
                )
              )
            )
          )
      }
      "when processing an IE837 for a change of destination" in {
        val testMessageCache = MessageCache(testErn, ie837SubmittedExplainDelayCOD.message, None)

        val result: Html = helper.constructActions(testMessageCache, None)

        result mustBe
          HtmlFormat.fill(
            Seq(
              list(
                extraClasses = Some("govuk-!-display-none-print"),
                content = Seq(
                  link(
                    link = appConfig.emcsTfeChangeDestinationUrl(request.ern, testMessageCache.message.arc.getOrElse("")),
                    messageKey = "viewMessage.link.changeDestination.description",
                    id = Some("submit-change-destination")
                  ),
                  link(
                    link = controllers.routes.ViewMovementController.viewMovementOverview(request.ern, testMessageCache.message.arc.getOrElse("")).url,
                    messageKey = "viewMessage.link.viewMovement.description",
                    id = Some("view-movement")
                  ),
                  link(
                    link = "#print-dialogue",
                    messageKey = "viewMessage.link.printMessage.description",
                    id = Some("print-link")
                  ),
                  link(
                    link = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
                    messageKey = "viewMessage.link.deleteMessage.description",
                    id = Some("delete-message")
                  )
                )
              )
            )
          )
      }
      "when processing an IE871 for a shortage or excess" when {
        "the logged in user is the consignor of the movement" in {

          val testMessageCache = MessageCache(testErn, ie871SubmittedShortageExcessAsAConsignor.message, None)

          val result: Html = helper.constructActions(testMessageCache, Some(getMovementResponseModel))

          result mustBe
            HtmlFormat.fill(
              Seq(
                list(
                  extraClasses = Some("govuk-!-display-none-print"),
                  content = Seq(
                    link(
                      link = controllers.routes.ViewMovementController.viewMovementOverview(request.ern, testMessageCache.message.arc.getOrElse("")).url,
                      messageKey = "viewMessage.link.viewMovement.description",
                      id = Some("view-movement")
                    ),
                    link(
                      link = "#print-dialogue",
                      messageKey = "viewMessage.link.printMessage.description",
                      id = Some("print-link")
                    ),
                    link(
                      link = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
                      messageKey = "viewMessage.link.deleteMessage.description",
                      id = Some("delete-message")
                    )
                  )
                )
              )
            )
        }
        "the logged in user is the consignee of the movement" in {

          val testMessageCache = MessageCache(testErn, ie871SubmittedShortageExcessAsAConsignee.message, None)

          val movementWithLoggedInUserAsConsignee = Some(getMovementResponseModel
            .copy(consigneeTrader = getMovementResponseModel.consigneeTrader.map(_.copy(traderExciseNumber = testErn)))
          )

          val result: Html = helper.constructActions(testMessageCache, movementWithLoggedInUserAsConsignee)

          result mustBe
            HtmlFormat.fill(
              Seq(
                list(
                  extraClasses = Some("govuk-!-display-none-print"),
                  content = Seq(
                    link(
                      link = appConfig.emcsTfeReportAReceiptUrl(request.ern, testMessageCache.message.arc.getOrElse("")),
                      messageKey = "viewMessage.link.reportOfReceipt.description",
                      id = Some("submit-report-of-receipt")
                    ),
                    link(
                      link = controllers.routes.ViewMovementController.viewMovementOverview(request.ern, testMessageCache.message.arc.getOrElse("")).url,
                      messageKey = "viewMessage.link.viewMovement.description",
                      id = Some("view-movement")
                    ),
                    link(
                      link = "#print-dialogue",
                      messageKey = "viewMessage.link.printMessage.description",
                      id = Some("print-link")
                    ),
                    link(
                      link = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
                      messageKey = "viewMessage.link.deleteMessage.description",
                      id = Some("delete-message")
                    )
                  )
                )
              )
            )
        }
      }
      "when processing an IE801 (received) notification" in {
        val testMessageCache = MessageCache(testErn, ie801ReceivedMovement.message, None)

        val result: Html = helper.constructActions(testMessageCache, None)

        result mustBe
          HtmlFormat.fill(
            Seq(
              list(
                extraClasses = Some("govuk-!-display-none-print"),
                content = Seq(
                  link(
                    link = controllers.routes.ViewMovementController.viewMovementOverview(request.ern, testMessageCache.message.arc.getOrElse("")).url,
                    messageKey = "viewMessage.link.viewMovement.description",
                    id = Some("view-movement")
                  ),
                  link(
                    link = "#print-dialogue",
                    messageKey = "viewMessage.link.printMessage.description",
                    id = Some("print-link")
                  ),
                  link(
                    link = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
                    messageKey = "viewMessage.link.deleteMessage.description",
                    id = Some("delete-message")
                  )
                )
              )
            )
          )
      }
      "when processing an IE801 (submitted) submission" in {
        val testMessageCache = MessageCache(testErn, ie801SubmittedMovement.message, None)

        val result: Html = helper.constructActions(testMessageCache, None)

        result mustBe
          HtmlFormat.fill(
            Seq(
              list(
                extraClasses = Some("govuk-!-display-none-print"),
                content = Seq(
                  link(
                    link = controllers.routes.ViewMovementController.viewMovementOverview(request.ern, testMessageCache.message.arc.getOrElse("")).url,
                    messageKey = "viewMessage.link.viewMovement.description",
                    id = Some("view-movement")
                  ),
                  link(
                    link = appConfig.emcsTfeChangeDestinationUrl(request.ern, testMessageCache.message.arc.getOrElse("")),
                    messageKey = "viewMessage.link.changeDestination.description",
                    id = Some("submit-change-destination")
                  ),
                  link(
                    link = "#print-dialogue",
                    messageKey = "viewMessage.link.printMessage.description",
                    id = Some("print-link")
                  ),
                  link(
                    link = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
                    messageKey = "viewMessage.link.deleteMessage.description",
                    id = Some("delete-message")
                  )
                )
              )
            )
          )
      }
      "when processing an IE803 notification change destination" in {
        val testMessageCache = MessageCache(testErn, ie803ReceivedChangeDestination.message, None)

        val result: Html = helper.constructActions(testMessageCache, None)

        result mustBe
          HtmlFormat.fill(
            Seq(
              list(
                extraClasses = Some("govuk-!-display-none-print"),
                content = Seq(
                  link(
                    link = controllers.routes.ViewMovementController.viewMovementOverview(request.ern, testMessageCache.message.arc.getOrElse("")).url,
                    messageKey = "viewMessage.link.viewMovement.description",
                    id = Some("view-movement")
                  ),
                  link(
                    link = "#print-dialogue",
                    messageKey = "viewMessage.link.printMessage.description",
                    id = Some("print-link")
                  ),
                  link(
                    link = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
                    messageKey = "viewMessage.link.deleteMessage.description",
                    id = Some("delete-message")
                  )
                )
              )
            )
          )
      }
      "when processing an IE803 notification split" in {
        val testMessageCache = MessageCache(testErn, ie803ReceivedSplit.message, None)

        val result: Html = helper.constructActions(testMessageCache, None)

        result mustBe
          HtmlFormat.fill(
            Seq(
              list(
                extraClasses = Some("govuk-!-display-none-print"),
                content = Seq(
                  link(
                    link = controllers.routes.ViewMovementController.viewMovementOverview(request.ern, testMessageCache.message.arc.getOrElse("")).url,
                    messageKey = "viewMessage.link.viewMovement.description",
                    id = Some("view-movement")
                  ),
                  link(
                    link = "#print-dialogue",
                    messageKey = "viewMessage.link.printMessage.description",
                    id = Some("print-link")
                  ),
                  link(
                    link = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
                    messageKey = "viewMessage.link.deleteMessage.description",
                    id = Some("delete-message")
                  )
                )
              )
            )
          )
      }
      "when processing an IE818 (received) notification" in {
        val testMessageCache = MessageCache(testErn, ie818ReceivedReportOfReceipt.message, None)

        val result: Html = helper.constructActions(testMessageCache, None)

        result mustBe
          HtmlFormat.fill(
            Seq(
              list(
                extraClasses = Some("govuk-!-display-none-print"),
                content = Seq(
                  link(
                    link = controllers.routes.ViewMovementController.viewMovementOverview(request.ern, testMessageCache.message.arc.getOrElse("")).url,
                    messageKey = "viewMessage.link.viewMovement.description",
                    id = Some("view-movement")
                  ),
                  link(
                    link = "#print-dialogue",
                    messageKey = "viewMessage.link.printMessage.description",
                    id = Some("print-link")
                  ),
                  link(
                    link = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
                    messageKey = "viewMessage.link.deleteMessage.description",
                    id = Some("delete-message")
                  )
                )
              )
            )
          )
      }
      "when processing an IE818 (submitted) submission" in {
        val testMessageCache = MessageCache(testErn, ie818SubmittedReportOfReceipt.message, None)

        val result: Html = helper.constructActions(testMessageCache, None)

        result mustBe
          HtmlFormat.fill(
            Seq(
              list(
                extraClasses = Some("govuk-!-display-none-print"),
                content = Seq(
                  link(
                    link = appConfig.emcsTfeExplainShortageOrExcessUrl(request.ern, testMessageCache.message.arc.getOrElse("")),
                    messageKey = "viewMessage.link.explainShortageExcess.description",
                    id = Some("submit-shortage-excess")
                  ),
                  link(
                    link = controllers.routes.ViewMovementController.viewMovementOverview(request.ern, testMessageCache.message.arc.getOrElse("")).url,
                    messageKey = "viewMessage.link.viewMovement.description",
                    id = Some("view-movement")
                  ),
                  link(
                    link = "#print-dialogue",
                    messageKey = "viewMessage.link.printMessage.description",
                    id = Some("print-link")
                  ),
                  link(
                    link = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
                    messageKey = "viewMessage.link.deleteMessage.description",
                    id = Some("delete-message")
                  )
                )
              )
            )
          )
      }
    }
  }

  ".constructErrors" must {

    //scalastyle:off
    Seq(4401, 4402, 4403, 4404, 4405, 4406, 4407, 4408, 4409, 4410, 4411, 4412, 4413, 4414, 4415, 4416, 4417, 4418, 4419, 4420, 4421, 4422, 4423, 4424, 4425, 4426, 4427, 4428, 4429, 4430, 4431, 4432, 4433, 4434, 4435, 4436, 4437, 4438, 4439, 4440, 4441, 4442, 4443, 4444, 4445, 4446, 4448, 4449, 4451, 4452, 4453, 4454, 4455, 4456, 4457, 4458, 4459, 4460, 4461, 4466, 4467, 4468, 4469, 4470, 4471, 4472, 4473, 4474, 4475, 4476, 4477, 4478, 4479, 4480, 4481, 4482, 4483, 4484, 4485, 4486, 4487, 4488, 4490, 4492, 4493, 4494, 4495, 4496, 4497, 4498, 4499, 4500, 4501, 4502, 4503, 4504, 4505, 4506, 4509, 4510, 4511, 4513, 4516, 4517, 4518, 4519, 4520, 4521, 4522, 4523, 4524, 4525, 4526, 4527, 4528, 4529, 4530, 4531, 4601, 4602, 9005).foreach {
      errorCode => {
        "build a summary list of all the errors in the failure message (code -> description where the error is mapped to a custom value) - and a heading" when {
          s"the error code is $errorCode" in {
            val message = MessageCache(testErn, ie704ErrorCancellationIE810.message, Some(getSubmissionFailureMessageResponseModel.copy(IE704ModelFixtures.ie704ModelModel.copy(
              body = IE704BodyFixtures.ie704BodyModel.copy(
                functionalError = Seq(IE704FunctionalErrorFixtures.ie704FunctionalErrorModel.copy(s"$errorCode"))
              )),
              relatedMessageType = Some("IE810")
            )))
            val result = helper.constructErrors(message)
            removeNewLines(result.toString()) mustBe removeNewLines(HtmlFormat.fill(Seq(
              h2("Errors"),
              govukSummaryList(SummaryList(Seq(
                SummaryListRow(
                  key = Key(Text(value = message.errorMessage.get.ie704.body.functionalError.head.errorType)),
                  value = Value(Text(value = msgs(s"messages.IE704.error.${message.errorMessage.get.ie704.body.functionalError.head.errorType}")))
                )
              )))
            )).toString())
          }
        }
      }
    }

    "build a summary list of all the errors in the failure message (code -> description where the error is not mapped to a custom value) - and a heading" when {
      "the error code is unmatched" in {
        val message = MessageCache(testErn, ie704ErrorCancellationIE810.message, Some(getSubmissionFailureMessageResponseModel.copy(
          relatedMessageType = Some("IE810"),
          ie704 = IE704ModelFixtures.ie704ModelModel.copy(
            body = IE704BodyFixtures.ie704BodyModel.copy(
              functionalError = Seq(IE704FunctionalErrorFixtures.ie704FunctionalErrorModel.copy("12345"))))),
        ))
        val result = helper.constructErrors(message)
        removeNewLines(result.toString()) mustBe removeNewLines(HtmlFormat.fill(Seq(
          h2("Errors"),
          govukSummaryList(SummaryList(Seq(
            SummaryListRow(
              key = Key(Text(value = message.errorMessage.get.ie704.body.functionalError.head.errorType)),
              value = Value(Text(value = message.errorMessage.get.ie704.body.functionalError.head.errorReason))
            )
          )))
        )).toString())
      }
    }

    "show nothing for an IE815" when {
      "it's not a portal submission" in {
        val message = MessageCache(testErn, ie704ErrorCreateMovementIE815.message, Some(getSubmissionFailureMessageResponseModel.copy(IE704ModelFixtures.ie704ModelModel.copy(
          body = IE704BodyFixtures.ie704BodyModel.copy(
            functionalError = Seq(IE704FunctionalErrorFixtures.ie704FunctionalErrorModel.copy("4403")))))
        ))
        val result = helper.constructErrors(message)
        result mustBe Html("")
      }

      "there are no non-fixable errors" in {
        val message = MessageCache(testErn, ie704ErrorCreateMovementIE815.message, Some(getSubmissionFailureMessageResponseModel.copy(IE704ModelFixtures.ie704ModelModel.copy(
          header = IE704HeaderFixtures.ie704HeaderModel.copy(correlationIdentifier = Some("PORTAL1234")),
          body = IE704BodyFixtures.ie704BodyModel.copy(
            functionalError = Seq(IE704FunctionalErrorFixtures.ie704FunctionalErrorModel.copy("4404")))))
        ))
        val result = helper.constructErrors(message)
        result mustBe Html("")
      }
    }
  }

  ".contentForSubmittedVia3rdParty" must {

    "return the correct content when the submission was made via a 3rd party" in {
      helper.contentForSubmittedVia3rdParty(isPortalSubmission = false, relatedMessageType = "IE810", testErn) mustBe Seq(p() {
        Html("If you used commercial software for your submission, please correct these errors with the same software that you used for the submission.")
      })
    }

    "return the correct content when the submission was made via a 3rd party (IE815)" in {
      helper.contentForSubmittedVia3rdParty(isPortalSubmission = false, relatedMessageType = "IE815", testErn) mustBe Seq(p() {
        HtmlFormat.fill(Seq(
          Html("If you used commercial software for your submission, please correct these errors with the same software that you used for the submission, or"),
          link(appConfig.emcsTfeCreateMovementUrl(testErn), "create a new movement", id = Some("create-a-new-movement"), withFullStop = true)
        ))
      })
    }

    "return an empty list when the submission was not made by a 3rd party" in {
      helper.contentForSubmittedVia3rdParty(isPortalSubmission = true, relatedMessageType = "IE810", testErn) mustBe Seq.empty
    }

  }

  ".contentForContactingHelpdesk" must {

    "return the correct content" in {
      helper.contentForContactingHelpdesk() mustBe Seq(p() {
        HtmlFormat.fill(Seq(
          link(appConfig.exciseHelplineUrl, "Contact the HMRC excise helpline", id = Some("contactHmrc"), isExternal = true),
          Html("if you need more help or advice.")
        ))
      })
    }
  }

  ".contentForFixingError" must {

    def messageCache(testMessage: TestMessage): MessageCache = MessageCache(testErn, testMessage.message, Some(getSubmissionFailureMessageResponseModel))

    "return the correct content for an IE815 error - fixable (portal)" in {
      helper.contentForFixingError("IE815", numberOfErrors = 1, numberOfNonFixableErrors = 0, isPortalSubmission = true)(implicitly, messageCache(ie704ErrorCreateMovementIE815)) mustBe Seq(
        p()(HtmlFormat.fill(Seq(
          link(controllers.messages.routes.ViewMessageController.removeMessageAndRedirectToDraftMovement(testErn, ie704ErrorCreateMovementIE815.message.uniqueMessageIdentifier).url,
            "Update and resubmit the movement", id = Some("update-draft-movement"), withFullStop = true)
        )))
      )
    }

    "return the correct content for an IE815 error - fixable (3rd party)" in {
      //TODO: update when IE815 submission failure message is played (ETFE-3437)
      helper.contentForFixingError("IE815", numberOfErrors = 1, numberOfNonFixableErrors = 0, isPortalSubmission = false)(implicitly, messageCache(ie704ErrorCreateMovementIE815)) mustBe Seq()
    }

    "return the correct content for an IE815 error - non-fixable (singular - portal submission)" in {
      helper.contentForFixingError("IE815", numberOfErrors = 1, numberOfNonFixableErrors = 1, isPortalSubmission = true)(implicitly, messageCache(ie704ErrorCreateMovementIE815)) mustBe Seq(
        p()(HtmlFormat.fill(Seq(
          Html("The error cannot be fixed, so you need to"),
          link(appConfig.emcsTfeCreateMovementUrl(testErn), "create a new movement", id = Some("create-a-new-movement"), withFullStop = true)
        )))
      )
    }

    "return the correct content for an IE815 error - non-fixable (plural - portal submission)" in {
      helper.contentForFixingError("IE815", numberOfErrors = 3, numberOfNonFixableErrors = 2, isPortalSubmission = true)(implicitly, messageCache(ie704ErrorCreateMovementIE815)) mustBe Seq(
        p()(HtmlFormat.fill(Seq(
          Html("At least one of these errors cannot be fixed, so you need to"),
          link(appConfig.emcsTfeCreateMovementUrl(testErn), "create a new movement", id = Some("create-a-new-movement"), withFullStop = true)
        )))
      )
    }

    "return the correct content for an IE815 error - non-fixable (third party)" in {
      helper.contentForFixingError("IE815", numberOfErrors = 3, numberOfNonFixableErrors = 2, isPortalSubmission = false)(implicitly, messageCache(ie704ErrorCreateMovementIE815)) mustBe Seq()
    }

    "return the correct content for an IE810 error" in {
      helper.contentForFixingError("IE810", numberOfErrors = 1, numberOfNonFixableErrors = 1, isPortalSubmission = false)(implicitly, messageCache(ie704ErrorCancellationIE810)) mustBe Seq(
        p()(HtmlFormat.fill(Seq(
          Html("If you still want to"),
          link(appConfig.emcsTfeCancelMovementUrl(testErn, testArc), "cancel this movement", id = Some("cancel-movement")),
          Html(msgs("you can submit a new cancellation with the errors corrected."))
        ))),
        p()(HtmlFormat.fill(Seq(
          Html(msgs("However you can only cancel a movement up to the date and time recorded on the electronic administrative document (eAD). If the date and time on the eAD has passed, you can choose to")),
          link(appConfig.emcsTfeChangeDestinationUrl(testErn, testArc), "submit a change of destination", withFullStop = true, id = Some("submit-change-destination"))
        )))
      )
    }

    "return the correct content for an IE837 error" in {
      helper.contentForFixingError("IE837", numberOfErrors = 1, numberOfNonFixableErrors = 1, isPortalSubmission = false)(implicitly, messageCache(ie704ErrorExplainDelayIE837)) mustBe Seq(p()(HtmlFormat.fill(Seq(
        Html("You need to"),
        link(appConfig.emcsTfeExplainDelayUrl(testErn, testArc), "submit a new explanation of a delay", withFullStop = true, id = Some("submit-new-explanation-for-delay"))
      ))))
    }

    "return the correct content for an IE871 error" in {
      helper.contentForFixingError("IE871", numberOfErrors = 1, numberOfNonFixableErrors = 1, isPortalSubmission = false)(implicitly, messageCache(ie704ErrorExplainShortageOrExcessIE871)) mustBe Seq(p()(HtmlFormat.fill(Seq(
        Html("You need to"),
        link(appConfig.emcsTfeExplainShortageOrExcessUrl(testErn, testArc), "submit a new explanation of a shortage or excess", withFullStop = true, id = Some("submit-a-new-explanation-for-shortage-or-excess"))
      ))))
    }

    "return the correct content for an IE818 error" in {
      helper.contentForFixingError("IE818", numberOfErrors = 1, numberOfNonFixableErrors = 1, isPortalSubmission = false)(implicitly, messageCache(ie704ErrorReportOfReceiptIE818)) mustBe Seq(p()(HtmlFormat.fill(Seq(
        Html("You need to"),
        link(
          link = appConfig.emcsTfeReportAReceiptUrl(testErn, testArc),
          messageKey = "submit a new report of receipt",
          withFullStop = true,
          id = Some("submit-a-new-report-of-receipt")
        )
      ))))
    }

    "return the correct content for an IE819 error" in {
      helper.contentForFixingError("IE819", numberOfErrors = 1, numberOfNonFixableErrors = 1, isPortalSubmission = false)(implicitly, messageCache(ie704ErrorAlertRejectionIE819)) mustBe Seq(p()(HtmlFormat.fill(Seq(
        Html("To correct any errors you must"),
        link(appConfig.emcsTfeAlertOrRejectionUrl(testErn, testArc), "submit a new alert or rejection", id = Some("submit-a-new-alert-rejection")),
        Html("of this movement.")
      ))))
    }

    "return the correct content for an IE825 error" in {
      helper.contentForFixingError("IE825", numberOfErrors = 1, numberOfNonFixableErrors = 1, isPortalSubmission = false)(implicitly, messageCache(ie704ErrorSplitMovementIE825)) mustBe Seq.empty
    }

    "return the correct content for an IE813 error" in {
      helper.contentForFixingError("IE813", numberOfErrors = 1, numberOfNonFixableErrors = 1, isPortalSubmission = false)(implicitly, messageCache(ie704ErrorChangeDestinationIE813)) mustBe Seq(p()(HtmlFormat.fill(Seq(
        Html("You need to"),
        link(appConfig.emcsTfeChangeDestinationUrl(testErn, testArc), "submit a new change of destination", id = Some("submit-change-destination"), withFullStop = true)
      ))))
    }

    "return an empty list when the message type is not matched" in {
      helper.contentForFixingError("FAKE", numberOfErrors = 1, numberOfNonFixableErrors = 0, isPortalSubmission = false)(implicitly, messageCache(ie801ReceivedMovement)) mustBe Seq.empty
    }
  }

  ".constructFixErrorsContent" should {

    import IE704ModelFixtures._

    "for an IE810" must {

      "return the correct content when the errors are non-fixable, 3rd party submission" in {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          relatedMessageType = Some("IE810")
        )
        val result = helper.constructFixErrorsContent(MessageCache(testErn, ie704ErrorCancellationIE810.message, Some(failureMessageResponse)))
        removeNewLines(result.toString()) mustBe removeNewLines(HtmlFormat.fill(Seq(
          p()(HtmlFormat.fill(Seq(
            Html("If you still want to"),
            link(appConfig.emcsTfeCancelMovementUrl(testErn, testArc), "cancel this movement", id = Some("cancel-movement")),
            Html(msgs("you can submit a new cancellation with the errors corrected."))
          ))),
          p()(HtmlFormat.fill(Seq(
            Html(msgs("However you can only cancel a movement up to the date and time recorded on the electronic administrative document (eAD). If the date and time on the eAD has passed, you can choose to")),
            link(appConfig.emcsTfeChangeDestinationUrl(testErn, testArc), "submit a change of destination", withFullStop = true, id = Some("submit-change-destination"))
          ))),
          p() {
            Html("If you used commercial software for your submission, please correct these errors with the same software that you used for the submission.")
          },
          p() {
            HtmlFormat.fill(Seq(
              link(appConfig.exciseHelplineUrl, "Contact the HMRC excise helpline", id = Some("contactHmrc"), isExternal = true),
              Html("if you need more help or advice.")
            ))
          }
        )).toString())
      }

      "return the correct content when the errors are non-fixable, portal submission" in {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          ie704PortalSubmission,
          relatedMessageType = Some("IE810")
        )
        val result = helper.constructFixErrorsContent(MessageCache(testErn, ie704ErrorCancellationIE810.message, Some(failureMessageResponse)))
        removeNewLines(result.toString()) mustBe removeNewLines(HtmlFormat.fill(Seq(
          p()(HtmlFormat.fill(Seq(
            Html("If you still want to"),
            link(appConfig.emcsTfeCancelMovementUrl(testErn, testArc), "cancel this movement", id = Some("cancel-movement")),
            Html(msgs("you can submit a new cancellation with the errors corrected."))
          ))),
          p()(HtmlFormat.fill(Seq(
            Html(msgs("However you can only cancel a movement up to the date and time recorded on the electronic administrative document (eAD). If the date and time on the eAD has passed, you can choose to")),
            link(appConfig.emcsTfeChangeDestinationUrl(testErn, testArc), "submit a change of destination", withFullStop = true, id = Some("submit-change-destination"))
          ))),
          p() {
            HtmlFormat.fill(Seq(
              link(appConfig.exciseHelplineUrl, "Contact the HMRC excise helpline", id = Some("contactHmrc"), isExternal = true),
              Html("if you need more help or advice.")
            ))
          }
        )).toString())
      }

    }

    "for an IE837" must {

      "return the correct content when the errors are non-fixable, 3rd party submission" in {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          relatedMessageType = Some("IE837")
        )
        val result = helper.constructFixErrorsContent(MessageCache(testErn, ie704ErrorExplainDelayIE837.message, Some(failureMessageResponse)))
        removeNewLines(result.toString()) mustBe removeNewLines(HtmlFormat.fill(Seq(
          p() {
            HtmlFormat.fill(Seq(
              Html("You need to"),
              link(appConfig.emcsTfeExplainDelayUrl(testErn, testArc), "submit a new explanation of a delay", withFullStop = true, id = Some("submit-new-explanation-for-delay"))
            ))
          },
          p() {
            Html("If you used commercial software for your submission, please correct these errors with the same software that you used for the submission.")
          },
          p() {
            HtmlFormat.fill(Seq(
              link(appConfig.exciseHelplineUrl, "Contact the HMRC excise helpline", id = Some("contactHmrc"), isExternal = true),
              Html("if you need more help or advice.")
            ))
          }
        )).toString())
      }

      "return the correct content when the errors are non-fixable, portal submission" in {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          ie704PortalSubmission,
          relatedMessageType = Some("IE837")
        )
        val result = helper.constructFixErrorsContent(MessageCache(testErn, ie704ErrorExplainDelayIE837.message, Some(failureMessageResponse)))
        removeNewLines(result.toString()) mustBe removeNewLines(HtmlFormat.fill(Seq(
          p() {
            HtmlFormat.fill(Seq(
              Html("You need to"),
              link(appConfig.emcsTfeExplainDelayUrl(testErn, testArc), "submit a new explanation of a delay", withFullStop = true, id = Some("submit-new-explanation-for-delay"))
            ))
          },
          p() {
            HtmlFormat.fill(Seq(
              link(appConfig.exciseHelplineUrl, "Contact the HMRC excise helpline", id = Some("contactHmrc"), isExternal = true),
              Html("if you need more help or advice.")
            ))
          }
        )).toString())
      }

    }

    "for an IE871" must {

      "return the correct content when the errors are non-fixable, 3rd party submission" in {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          relatedMessageType = Some("IE871")
        )
        val result = helper.constructFixErrorsContent(MessageCache(testErn, ie704ErrorExplainShortageOrExcessIE871.message, Some(failureMessageResponse)))
        removeNewLines(result.toString()) mustBe removeNewLines(HtmlFormat.fill(Seq(
          p() {
            HtmlFormat.fill(Seq(
              Html("You need to"),
              link(appConfig.emcsTfeExplainShortageOrExcessUrl(testErn, testArc), "submit a new explanation of a shortage or excess", withFullStop = true, id = Some("submit-a-new-explanation-for-shortage-or-excess"))
            ))
          },
          p() {
            Html("If you used commercial software for your submission, please correct these errors with the same software that you used for the submission.")
          },
          p() {
            HtmlFormat.fill(Seq(
              link(appConfig.exciseHelplineUrl, "Contact the HMRC excise helpline", id = Some("contactHmrc"), isExternal = true),
              Html("if you need more help or advice.")
            ))
          }
        )).toString())
      }

      "return the correct content when the errors are non-fixable, portal submission" in {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          ie704PortalSubmission,
          relatedMessageType = Some("IE871")
        )
        val result = helper.constructFixErrorsContent(MessageCache(testErn, ie704ErrorExplainShortageOrExcessIE871.message, Some(failureMessageResponse)))
        removeNewLines(result.toString()) mustBe removeNewLines(HtmlFormat.fill(Seq(
          p() {
            HtmlFormat.fill(Seq(
              Html("You need to"),
              link(appConfig.emcsTfeExplainShortageOrExcessUrl(testErn, testArc), "submit a new explanation of a shortage or excess", withFullStop = true, id = Some("submit-a-new-explanation-for-shortage-or-excess"))
            ))
          },
          p() {
            HtmlFormat.fill(Seq(
              link(appConfig.exciseHelplineUrl, "Contact the HMRC excise helpline", id = Some("contactHmrc"), isExternal = true),
              Html("if you need more help or advice.")
            ))
          }
        )).toString())
      }

    }

    "for an IE818" must {

      "return the correct content when the errors are non-fixable, 3rd party submission" in {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          relatedMessageType = Some("IE818")
        )
        val result = helper.constructFixErrorsContent(MessageCache(testErn, ie704ErrorReportOfReceiptIE818.message, Some(failureMessageResponse)))
        removeNewLines(result.toString()) mustBe removeNewLines(HtmlFormat.fill(Seq(
          p() {
            HtmlFormat.fill(Seq(
              Html("You need to"),
              link(
                link = appConfig.emcsTfeReportAReceiptUrl(testErn, testArc),
                messageKey = "submit a new report of receipt",
                withFullStop = true,
                id = Some("submit-a-new-report-of-receipt")
              )
            ))
          },
          p() {
            Html("If you used commercial software for your submission, please correct these errors with the same software that you used for the submission.")
          },
          p() {
            HtmlFormat.fill(Seq(
              link(appConfig.exciseHelplineUrl, "Contact the HMRC excise helpline", id = Some("contactHmrc"), isExternal = true),
              Html("if you need more help or advice.")
            ))
          }
        )).toString())
      }

      "return the correct content when the errors are non-fixable, portal submission" in {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          ie704PortalSubmission,
          relatedMessageType = Some("IE818")
        )
        val result = helper.constructFixErrorsContent(MessageCache(testErn, ie704ErrorReportOfReceiptIE818.message, Some(failureMessageResponse)))
        removeNewLines(result.toString()) mustBe removeNewLines(HtmlFormat.fill(Seq(
          p() {
            HtmlFormat.fill(Seq(
              Html("You need to"),
              link(
                link = appConfig.emcsTfeReportAReceiptUrl(testErn, testArc),
                messageKey = "submit a new report of receipt",
                withFullStop = true,
                id = Some("submit-a-new-report-of-receipt")
              )
            ))
          },
          p() {
            HtmlFormat.fill(Seq(
              link(appConfig.exciseHelplineUrl, "Contact the HMRC excise helpline", id = Some("contactHmrc"), isExternal = true),
              Html("if you need more help or advice.")
            ))
          }
        )).toString())
      }

    }

    "for an IE819" must {

      "return the correct content when the errors are non-fixable, 3rd party submission" in {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          relatedMessageType = Some("IE819")
        )
        val result = helper.constructFixErrorsContent(MessageCache(testErn, ie704ErrorAlertRejectionIE819.message, Some(failureMessageResponse)))
        removeNewLines(result.toString()) mustBe removeNewLines(HtmlFormat.fill(Seq(
          p() {
            HtmlFormat.fill(Seq(
              Html("To correct any errors you must"),
              link(appConfig.emcsTfeAlertOrRejectionUrl(testErn, testArc), "submit a new alert or rejection", id = Some("submit-a-new-alert-rejection")),
              Html("of this movement.")
            ))
          },
          p() {
            Html("If you used commercial software for your submission, please correct these errors with the same software that you used for the submission.")
          },
          p() {
            HtmlFormat.fill(Seq(
              link(appConfig.exciseHelplineUrl, "Contact the HMRC excise helpline", id = Some("contactHmrc"), isExternal = true),
              Html("if you need more help or advice.")
            ))
          }
        )).toString())
      }

      "return the correct content when the errors are non-fixable, portal submission" in {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          ie704PortalSubmission,
          relatedMessageType = Some("IE819")
        )
        val result = helper.constructFixErrorsContent(MessageCache(testErn, ie704ErrorAlertRejectionIE819.message, Some(failureMessageResponse)))
        removeNewLines(result.toString()) mustBe removeNewLines(HtmlFormat.fill(Seq(
          p() {
            HtmlFormat.fill(Seq(
              Html("To correct any errors you must"),
              link(appConfig.emcsTfeAlertOrRejectionUrl(testErn, testArc), "submit a new alert or rejection", id = Some("submit-a-new-alert-rejection")),
              Html("of this movement.")
            ))
          },
          p() {
            HtmlFormat.fill(Seq(
              link(appConfig.exciseHelplineUrl, "Contact the HMRC excise helpline", id = Some("contactHmrc"), isExternal = true),
              Html("if you need more help or advice.")
            ))
          }
        )).toString())
      }

    }

    "for an IE825" must {

      "return the correct content when the errors are non-fixable, 3rd party submission" in {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          relatedMessageType = Some("IE825")
        )
        val result = helper.constructFixErrorsContent(MessageCache(testErn, ie704ErrorSplitMovementIE825.message, Some(failureMessageResponse)))
        removeNewLines(result.toString()) mustBe removeNewLines(HtmlFormat.fill(Seq(
          p() {
            Html("If you used commercial software for your submission, please correct these errors with the same software that you used for the submission.")
          },
          p() {
            HtmlFormat.fill(Seq(
              link(appConfig.exciseHelplineUrl, "Contact the HMRC excise helpline", id = Some("contactHmrc"), isExternal = true),
              Html("if you need more help or advice.")
            ))
          }
        )).toString())
      }

      "return the correct content when the errors are non-fixable, portal submission" in {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          ie704 = ie704PortalSubmission,
          relatedMessageType = Some("IE825")
        )
        val result = helper.constructFixErrorsContent(MessageCache(testErn, ie704ErrorSplitMovementIE825.message, Some(failureMessageResponse)))
        removeNewLines(result.toString()) mustBe removeNewLines(HtmlFormat.fill(Seq(
          p() {
            HtmlFormat.fill(Seq(
              link(appConfig.exciseHelplineUrl, "Contact the HMRC excise helpline", id = Some("contactHmrc"), isExternal = true),
              Html("if you need more help or advice.")
            ))
          }
        )).toString())
      }

    }

    "for an IE813" must {

      "return the correct content when the errors are non-fixable, 3rd party submission" in {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          relatedMessageType = Some("IE813")
        )
        val result = helper.constructFixErrorsContent(MessageCache(testErn, ie704ErrorChangeDestinationIE813.message, Some(failureMessageResponse)))
        removeNewLines(result.toString()) mustBe removeNewLines(HtmlFormat.fill(Seq(
          p() {
            HtmlFormat.fill(Seq(
              Html("You need to"),
              link(
                link = appConfig.emcsTfeChangeDestinationUrl(testErn, testArc),
                messageKey = "submit a new change of destination",
                withFullStop = true,
                id = Some("submit-change-destination")
              )
            ))
          },
          p() {
            Html("If you used commercial software for your submission, please correct these errors with the same software that you used for the submission.")
          },
          p() {
            HtmlFormat.fill(Seq(
              link(appConfig.exciseHelplineUrl, "Contact the HMRC excise helpline", id = Some("contactHmrc"), isExternal = true),
              Html("if you need more help or advice.")
            ))
          }
        )).toString())
      }

      "return the correct content when the errors are non-fixable, portal submission" in {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          ie704PortalSubmission,
          relatedMessageType = Some("IE813")
        )
        val result = helper.constructFixErrorsContent(MessageCache(testErn, ie704ErrorChangeDestinationIE813.message, Some(failureMessageResponse)))
        removeNewLines(result.toString()) mustBe removeNewLines(HtmlFormat.fill(Seq(
          p() {
            HtmlFormat.fill(Seq(
              Html("You need to"),
              link(
                link = appConfig.emcsTfeChangeDestinationUrl(testErn, testArc),
                messageKey = "submit a new change of destination",
                withFullStop = true,
                id = Some("submit-change-destination")
              )
            ))
          },
          p() {
            HtmlFormat.fill(Seq(
              link(appConfig.exciseHelplineUrl, "Contact the HMRC excise helpline", id = Some("contactHmrc"), isExternal = true),
              Html("if you need more help or advice.")
            ))
          }
        )).toString())
      }

    }

    "for an IE815" must {

      "return the correct content when the errors are non-fixable, 3rd party submission" in {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          ie704 = IE704ModelFixtures.ie704ModelModel.copy(
            body = IE704BodyFixtures.ie704BodyModel.copy(
              functionalError = Seq(
                IE704FunctionalErrorFixtures.ie704FunctionalErrorModel.copy(errorType = "4411", errorReason = "You are not approved on SEED to dispatch energy products. Please check that the correct excise product code is selected and amend your entry."),
              )
            )
          ),
          relatedMessageType = Some("IE815")
        )
        val result = helper.constructFixErrorsContent(MessageCache(testErn, ie704ErrorCreateMovementIE815.message, Some(failureMessageResponse)))
        removeNewLines(result.toString()) mustBe removeNewLines(HtmlFormat.fill(Seq(
          p() {
            HtmlFormat.fill(Seq(
              Html("If you used commercial software for your submission, please correct these errors with the same software that you used for the submission, or"),
              link(appConfig.emcsTfeCreateMovementUrl(testErn), "create a new movement", id = Some("create-a-new-movement"), withFullStop = true)
            ))
          },
          p() {
            Html("An ARC will only be created for a movement once it has been successfully submitted.")
          },
          p() {
            HtmlFormat.fill(Seq(
              link(appConfig.exciseHelplineUrl, "Contact the HMRC excise helpline", id = Some("contactHmrc"), isExternal = true),
              Html("if you need more help or advice.")
            ))
          }
        )).toString())
      }

      "return the correct content when the errors are non-fixable, portal submission (plural)" in {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          ie704 = ie704PortalSubmission.copy(
            body = IE704BodyFixtures.ie704BodyModel.copy(
              functionalError = Seq(
                IE704FunctionalErrorFixtures.ie704FunctionalErrorModel.copy(errorType = "4403", errorReason = "The consignor Excise Registration Number you have entered is not recognised by SEED. Please amend your entry."),
                IE704FunctionalErrorFixtures.ie704FunctionalErrorModel.copy(errorType = "4411", errorReason = "You are not approved on SEED to dispatch energy products. Please check that the correct excise product code is selected and amend your entry."),
              )
            )
          ),
          relatedMessageType = Some("IE815")
        )
        val result = helper.constructFixErrorsContent(MessageCache(testErn, ie704ErrorCreateMovementIE815.message, Some(failureMessageResponse)))
        removeNewLines(result.toString()) mustBe removeNewLines(HtmlFormat.fill(Seq(
          p() {
            HtmlFormat.fill(Seq(
              Html("At least one of these errors cannot be fixed, so you need to"),
              link(appConfig.emcsTfeCreateMovementUrl(testErn), "create a new movement", id = Some("create-a-new-movement"), withFullStop = true)
            ))
          },
          p() {
            Html("An ARC will only be created for a movement once it has been successfully submitted.")
          },
          p() {
            HtmlFormat.fill(Seq(
              link(appConfig.exciseHelplineUrl, "Contact the HMRC excise helpline", id = Some("contactHmrc"), isExternal = true),
              Html("if you need more help or advice.")
            ))
          }
        )).toString())
      }

      "return the correct content when the errors are non-fixable, portal submission (singular)" in {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          ie704 = ie704PortalSubmission.copy(
            body = IE704BodyFixtures.ie704BodyModel.copy(
              functionalError = Seq(
                IE704FunctionalErrorFixtures.ie704FunctionalErrorModel.copy(errorType = "4403", errorReason = "The consignor Excise Registration Number you have entered is not recognised by SEED. Please amend your entry.")
              )
            )
          ),
          relatedMessageType = Some("IE815")
        )
        val result = helper.constructFixErrorsContent(MessageCache(testErn, ie704ErrorCreateMovementIE815.message, Some(failureMessageResponse)))
        removeNewLines(result.toString()) mustBe removeNewLines(HtmlFormat.fill(Seq(
          p() {
            HtmlFormat.fill(Seq(
              Html("The error cannot be fixed, so you need to"),
              link(appConfig.emcsTfeCreateMovementUrl(testErn), "create a new movement", id = Some("create-a-new-movement"), withFullStop = true)
            ))
          },
          p() {
            Html("An ARC will only be created for a movement once it has been successfully submitted.")
          },
          p() {
            HtmlFormat.fill(Seq(
              link(appConfig.exciseHelplineUrl, "Contact the HMRC excise helpline", id = Some("contactHmrc"), isExternal = true),
              Html("if you need more help or advice.")
            ))
          }
        )).toString())
      }

      "return the correct content when the errors are fixable, 3rd party submission" in {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          ie704 = IE704ModelFixtures.ie704ModelModel.copy(
            body = IE704BodyFixtures.ie704BodyModel.copy(
              functionalError = Seq(
                IE704FunctionalErrorFixtures.ie704FunctionalErrorModel.copy(errorType = "4410", errorReason = "The excise warehouse you have entered is not approved on SEED to receive this product. Please check that the correct excise product code is input and amend your entry."),
              )
            )
          ),
          relatedMessageType = Some("IE815")
        )
        val result = helper.constructFixErrorsContent(MessageCache(testErn, ie704ErrorCreateMovementIE815.message, Some(failureMessageResponse)))
        removeNewLines(result.toString()) mustBe removeNewLines(HtmlFormat.fill(Seq(
          p() {
            HtmlFormat.fill(Seq(
              Html("If you used commercial software for your submission, please correct these errors with the same software that you used for the submission, or"),
              link(appConfig.emcsTfeCreateMovementUrl(testErn), "create a new movement", id = Some("create-a-new-movement"), withFullStop = true)
            ))
          },
          p() {
            Html("An ARC will only be created for a movement once it has been successfully submitted.")
          },
          p() {
            HtmlFormat.fill(Seq(
              link(appConfig.exciseHelplineUrl, "Contact the HMRC excise helpline", id = Some("contactHmrc"), isExternal = true),
              Html("if you need more help or advice.")
            ))
          }
        )).toString())
      }

      "return the correct content when the errors are fixable, portal submission" in {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          ie704 = ie704PortalSubmission.copy(
            body = IE704BodyFixtures.ie704BodyModel.copy(
              functionalError = Seq(
                IE704FunctionalErrorFixtures.ie704FunctionalErrorModel.copy(errorType = "4406", errorReason = "The place of delivery you have entered is not recognised by SEED. Please amend your entry."),
                IE704FunctionalErrorFixtures.ie704FunctionalErrorModel.copy(errorType = "4407", errorReason = "The quantitiy entered exceeds the amount approved for this Temporary Consignment Authorisation (TCA). Please check and amend your entry."),
              )
            )
          ),
          relatedMessageType = Some("IE815")
        )
        val result = helper.constructFixErrorsContent(MessageCache(testErn, ie704ErrorCreateMovementIE815.message, Some(failureMessageResponse)))
        removeNewLines(result.toString()) mustBe removeNewLines(HtmlFormat.fill(Seq(
          p() {
            link(controllers.messages.routes.ViewMessageController.removeMessageAndRedirectToDraftMovement(testErn, ie704ErrorCreateMovementIE815.message.uniqueMessageIdentifier).url,
              "Update and resubmit the movement", id = Some("update-draft-movement"), withFullStop = true)
          },
          p() {
            Html("An ARC will only be created for a movement once it has been successfully submitted.")
          },
          p() {
            HtmlFormat.fill(Seq(
              link(appConfig.exciseHelplineUrl, "Contact the HMRC excise helpline", id = Some("contactHmrc"), isExternal = true),
              Html("if you need more help or advice.")
            ))
          }
        )).toString())
      }

    }

    "return no content when the message doesn't relate to a 704" in {
      helper.constructFixErrorsContent(MessageCache(testErn, message1, None)) mustBe Html("")
    }

    "return no content when there is no related message type in the message" in {
      helper.constructFixErrorsContent(MessageCache(testErn, message2, Some(getSubmissionFailureMessageResponseModel.copy(relatedMessageType = None)))) mustBe Html("")
    }
  }

  ".constructAdditionalInformation" when {

    "processing a message with additional information" should {
      "return a paragraph of text" in {
        val result: Html = helper.constructAdditionalInformation(ie813ReceivedChangeDestination.message, None)

        result mustBe
          HtmlFormat.fill(
            Seq(
              p() {
                Html("The destination of the movement has been changed.")
              }
            )
          )
      }
    }

    "processing a message with no additional information" should {
      "be empty" in {
        val result: Html = helper.constructAdditionalInformation(ie819SubmittedAlert.message, None)

        result mustBe Empty.asHtml
      }
    }
  }

  ".showWarningTextIfFixableIE805" when {

    "there are no non-fixable errors" should {

      "render the correct warning text" in {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          ie704 = IE704ModelFixtures.ie704ModelModel.copy(
            body = IE704BodyFixtures.ie704BodyModel.copy(
              functionalError = Seq(
                IE704FunctionalErrorFixtures.ie704FunctionalErrorModel.copy(errorType = "4410", errorReason = "The excise warehouse you have entered is not approved on SEED to receive this product. Please check that the correct excise product code is input and amend your entry.")
              )
            )
          ),
          relatedMessageType = Some("IE815")
        )
        val testMessageCache = MessageCache(testErn, ie704ErrorCreateMovementIE815.message, Some(failureMessageResponse))
        helper.showWarningTextIfFixableIE805(testMessageCache) mustBe warningText(Html(msgs("messages.IE704.IE815.fixError.fixable.warning")))
      }

    }
  }

  "there are at least 1 non-fixable error" should {

    "return empty HTML" in {
      val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
        ie704 = IE704ModelFixtures.ie704ModelModel.copy(
          body = IE704BodyFixtures.ie704BodyModel.copy(
            functionalError = Seq(
              IE704FunctionalErrorFixtures.ie704FunctionalErrorModel.copy(errorType = "4410", errorReason = "The excise warehouse you have entered is not approved on SEED to receive this product. Please check that the correct excise product code is input and amend your entry."),
              IE704FunctionalErrorFixtures.ie704FunctionalErrorModel.copy(errorType = "4411", errorReason = "You are not approved on SEED to dispatch energy products. Please check that the correct excise product code is selected and amend your entry.")
            )
          )
        ),
        relatedMessageType = Some("IE815")
      )
      val testMessageCache = MessageCache(testErn, ie704ErrorCreateMovementIE815.message, Some(failureMessageResponse))
      helper.showWarningTextIfFixableIE805(testMessageCache) mustBe Html("")
    }
  }

  "no error message is defined" should {

    "return empty HTML" in {

      val testMessageCache = MessageCache(testErn, ie819ReceivedAlert.message, None)
      helper.showWarningTextIfFixableIE805(testMessageCache) mustBe Html("")
    }
  }
}
