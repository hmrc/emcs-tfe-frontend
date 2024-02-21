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
import views.html.components.{h2, link, list, p}

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
  lazy val govukSummaryList: GovukSummaryList = app.injector.instanceOf[GovukSummaryList]

  private def removeNewLines(input: String): String = {
    input.replaceAll("\n", "")
  }

  ".constructMovementInformation" when {

    "a message type description is present" in {
      val testMessage = ie819ReceivedAlert.message
      val result: Html = helper.constructMovementInformation(testMessage)
      removeNewLines(result.toString()) mustBe removeNewLines(govukSummaryList(SummaryList(Seq(
        SummaryListRow(
          key = Key(Text(value = ViewMessageMessages.English.labelMessageType)),
          value = Value(Text(value = ViewMessageMessages.English.messageTypeDescriptionForIE819))
        ),
        SummaryListRow(
          key = Key(Text(value = ViewMessageMessages.English.labelArc)),
          value = Value(Text(value = testMessage.arc.get))
        ),
        SummaryListRow(
          key = Key(Text(value = ViewMessageMessages.English.labelLrn)),
          value = Value(Text(value = testMessage.lrn.get))
        )
      ))).toString())
    }

    "a message type description is not present" in {
      val testMessage = ie829ReceivedCustomsAcceptance.message
      val result: Html = helper.constructMovementInformation(testMessage)
      removeNewLines(result.toString()) mustBe removeNewLines(govukSummaryList(SummaryList(Seq(
        SummaryListRow(
          key = Key(Text(value = ViewMessageMessages.English.labelArc)),
          value = Value(Text(value = testMessage.arc.get))
        ),
        SummaryListRow(
          key = Key(Text(value = ViewMessageMessages.English.labelLrn)),
          value = Value(Text(value = testMessage.lrn.get))
        )
      ))).toString())
    }



  }

  ".constructActions" must {

    "return the correct action links" when {

      "when processing an IE813 notification" in {
        val testMessage = ie813ReceivedChangeDestination.message

        val result: Html = helper.constructActions(testMessage, None)

        result mustBe
          HtmlFormat.fill(
            Seq(
              list(
                extraClasses = Some("govuk-!-display-none-print"),
                content = Seq(
                  link(
                    link = appConfig.emcsTfeReportAReceiptUrl(request.ern, testMessage.arc.getOrElse("")),
                    messageKey = "viewMessage.link.reportOfReceipt.description",
                    id = Some("submit-report-of-receipt")
                  ),
                  link(
                    link = appConfig.emcsTfeExplainDelayUrl(request.ern, testMessage.arc.getOrElse("")),
                    messageKey = "viewMessage.link.explainDelay.description",
                    id = Some("submit-explain-delay")
                  ),
                  link(
                    link = controllers.routes.ViewMovementController.viewMovementOverview(request.ern, testMessage.arc.getOrElse("")).url,
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
        val testMessage = ie829ReceivedCustomsAcceptance.message

        val result: Html = helper.constructActions(testMessage, None)

        result mustBe
          HtmlFormat.fill(
            Seq(
              list(
                extraClasses = Some("govuk-!-display-none-print"),
                content = Seq(
                  link(
                    link = appConfig.emcsTfeChangeDestinationUrl(request.ern, testMessage.arc.getOrElse("")),
                    messageKey = "viewMessage.link.changeDestination.description",
                    id = Some("submit-change-destination")
                  ),
                  link(
                    link = controllers.routes.ViewMovementController.viewMovementOverview(request.ern, testMessage.arc.getOrElse("")).url,
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
        val testMessage = ie819SubmittedAlert.message

        val result: Html = helper.constructActions(testMessage, None)

        result mustBe
          HtmlFormat.fill(
            Seq(
              list(
                extraClasses = Some("govuk-!-display-none-print"),
                content = Seq(
                  link(
                    link = controllers.routes.ViewMovementController.viewMovementOverview(request.ern, testMessage.arc.getOrElse("")).url,
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
        val testMessage = ie837SubmittedExplainDelayROR.message

        val result: Html = helper.constructActions(testMessage, None)

        result mustBe
          HtmlFormat.fill(
            Seq(
              list(
                extraClasses = Some("govuk-!-display-none-print"),
                content = Seq(
                  link(
                    link = appConfig.emcsTfeReportAReceiptUrl(request.ern, testMessage.arc.getOrElse("")),
                    messageKey = "viewMessage.link.reportOfReceipt.description",
                    id = Some("submit-report-of-receipt")
                  ),
                  link(
                    link = controllers.routes.ViewMovementController.viewMovementOverview(request.ern, testMessage.arc.getOrElse("")).url,
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
        val testMessage = ie837SubmittedExplainDelayCOD.message

        val result: Html = helper.constructActions(testMessage, None)

        result mustBe
          HtmlFormat.fill(
            Seq(
              list(
                extraClasses = Some("govuk-!-display-none-print"),
                content = Seq(
                  link(
                    link = appConfig.emcsTfeChangeDestinationUrl(request.ern, testMessage.arc.getOrElse("")),
                    messageKey = "viewMessage.link.changeDestination.description",
                    id = Some("submit-change-destination")
                  ),
                  link(
                    link = controllers.routes.ViewMovementController.viewMovementOverview(request.ern, testMessage.arc.getOrElse("")).url,
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

          val testMessage = ie871SubmittedShortageExcessAsAConsignor.message

          val result: Html = helper.constructActions(testMessage, Some(getMovementResponseModel))

          result mustBe
            HtmlFormat.fill(
              Seq(
                list(
                  extraClasses = Some("govuk-!-display-none-print"),
                  content = Seq(
                    link(
                      link = controllers.routes.ViewMovementController.viewMovementOverview(request.ern, testMessage.arc.getOrElse("")).url,
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

          val testMessage = ie871SubmittedShortageExcessAsAConsignee.message

          val movementWithLoggedInUserAsConsignee = Some(getMovementResponseModel
            .copy(consigneeTrader = getMovementResponseModel.consigneeTrader.map(_.copy(traderExciseNumber = testErn)))
          )

          val result: Html = helper.constructActions(testMessage, movementWithLoggedInUserAsConsignee)

          result mustBe
            HtmlFormat.fill(
              Seq(
                list(
                  extraClasses = Some("govuk-!-display-none-print"),
                  content = Seq(
                    link(
                      link = appConfig.emcsTfeReportAReceiptUrl(request.ern, testMessage.arc.getOrElse("")),
                      messageKey = "viewMessage.link.reportOfReceipt.description",
                      id = Some("submit-report-of-receipt")
                    ),
                    link(
                      link = controllers.routes.ViewMovementController.viewMovementOverview(request.ern, testMessage.arc.getOrElse("")).url,
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
      "when processing an IE801 notification" in {
        val testMessage = ie801ReceivedMovement.message

        val result: Html = helper.constructActions(testMessage, None)

        result mustBe
          HtmlFormat.fill(
            Seq(
              list(
                extraClasses = Some("govuk-!-display-none-print"),
                content = Seq(
                  link(
                    link = controllers.routes.ViewMovementController.viewMovementOverview(request.ern, testMessage.arc.getOrElse("")).url,
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
      "when processing an IE801 submission" in {
        val testMessage = ie801SubmittedMovement.message

        val result: Html = helper.constructActions(testMessage, None)

        result mustBe
          HtmlFormat.fill(
            Seq(
              list(
                extraClasses = Some("govuk-!-display-none-print"),
                content = Seq(
                  link(
                    link = controllers.routes.ViewMovementController.viewMovementOverview(request.ern, testMessage.arc.getOrElse("")).url,
                    messageKey = "viewMessage.link.viewMovement.description",
                    id = Some("view-movement")
                  ),
                  link(
                    link = appConfig.emcsTfeChangeDestinationUrl(request.ern, testMessage.arc.getOrElse("")),
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
        val testMessage = ie803ReceivedChangeDestination.message

        val result: Html = helper.constructActions(testMessage, None)

        result mustBe
          HtmlFormat.fill(
            Seq(
              list(
                extraClasses = Some("govuk-!-display-none-print"),
                content = Seq(
                  link(
                    link = controllers.routes.ViewMovementController.viewMovementOverview(request.ern, testMessage.arc.getOrElse("")).url,
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
        val testMessage = ie803ReceivedSplit.message

        val result: Html = helper.constructActions(testMessage, None)

        result mustBe
          HtmlFormat.fill(
            Seq(
              list(
                extraClasses = Some("govuk-!-display-none-print"),
                content = Seq(
                  link(
                    link = controllers.routes.ViewMovementController.viewMovementOverview(request.ern, testMessage.arc.getOrElse("")).url,
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
      "when processing an IE818 notification" in {
        val testMessage = ie818ReceivedReportOfReceipt.message

        val result: Html = helper.constructActions(testMessage, None)

        result mustBe
          HtmlFormat.fill(
            Seq(
              list(
                extraClasses = Some("govuk-!-display-none-print"),
                content = Seq(
                  link(
                    link = controllers.routes.ViewMovementController.viewMovementOverview(request.ern, testMessage.arc.getOrElse("")).url,
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
      "when processing an IE818 submission" in {
        val testMessage = ie818SubmittedReportOfReceipt.message

        val result: Html = helper.constructActions(testMessage, None)

        result mustBe
          HtmlFormat.fill(
            Seq(
              list(
                extraClasses = Some("govuk-!-display-none-print"),
                content = Seq(
                  link(
                    link = appConfig.emcsTfeExplainShortageOrExcessUrl(request.ern, testMessage.arc.getOrElse("")),
                    messageKey = "viewMessage.link.explainShortageExcess.description",
                    id = Some("submit-shortage-excess")
                  ),
                  link(
                    link = controllers.routes.ViewMovementController.viewMovementOverview(request.ern, testMessage.arc.getOrElse("")).url,
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
    Seq(4401, 4402, 4403, 4404, 4405, 4406, 4407, 4408, 4409, 4410, 4411, 4412, 4413, 4414, 4415, 4416, 4417, 4418, 4419, 4420, 4421, 4422, 4423, 4424, 4425, 4426, 4427, 4428, 4429, 4430, 4431, 4432, 4433, 4434, 4435, 4436, 4437, 4438, 4439, 4440, 4441, 4442, 4443, 4444, 4445, 4446, 4447, 4448, 4449, 4451, 4452, 4453, 4454, 4455, 4456, 4457, 4458, 4459, 4460, 4461, 4466, 4467, 4468, 4469, 4470, 4471, 4472, 4473, 4474, 4475, 4476, 4477, 4478, 4479, 4480, 4481, 4482, 4483, 4484, 4485, 4486, 4487, 4488, 4490, 4492, 4493, 4494, 4495, 4496, 4497, 4498, 4499, 4500, 4501, 4502, 4503, 4504, 4505, 4506, 4509, 4510, 4511, 4512, 4513, 4516, 4517, 4518, 4519, 4520, 4521, 4522, 4523, 4524, 4525, 4526, 4527, 4528, 4529, 4530, 4531, 4601, 4602, 9005, 4600).foreach {
      errorCode => {
        "build a summary list of all the errors in the failure message (code -> description where the error is mapped to a custom value) - and a heading" when {
          s"the error code is $errorCode" in {
            val message = MessageCache(testErn, ie704ErrorCancellationIE810.message, Some(getSubmissionFailureMessageResponseModel.copy(IE704ModelFixtures.ie704ModelModel.copy(
              body = IE704BodyFixtures.ie704BodyModel.copy(
                functionalError = Seq(IE704FunctionalErrorFixtures.ie704FunctionalErrorModel.copy(s"$errorCode")))))
            ))
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
        val message = MessageCache(testErn, ie704ErrorCancellationIE810.message, Some(getSubmissionFailureMessageResponseModel.copy(IE704ModelFixtures.ie704ModelModel.copy(
          body = IE704BodyFixtures.ie704BodyModel.copy(
            functionalError = Seq(IE704FunctionalErrorFixtures.ie704FunctionalErrorModel.copy("12345")))))
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
  }

  ".contentForSubmittedVia3rdParty" must {

    "return the correct content when the submission was made via a 3rd party" in {
      helper.contentForSubmittedVia3rdParty(hasBeenSubmittedVia3rdParty = true) mustBe Seq(p() {
        Html("If you used commercial software for your submission, please correct these errors with the same software that you used for the submission.")
      })
    }

    "return an empty list when the submission was not made by a 3rd party" in {
      helper.contentForSubmittedVia3rdParty(hasBeenSubmittedVia3rdParty = false) mustBe Seq.empty
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
    "return the correct content for an IE815 error" in {
      //TODO: update when IE815 submission failure message is played (ETFE-2737)
      helper.contentForFixingError("IE815", hasFixableError = true, testErn, testArc) mustBe Seq(Html("placeholder"))
    }

    "return the correct content for an IE810 error" in {
      helper.contentForFixingError("IE810", hasFixableError = true, testErn, testArc) mustBe Seq(
        p()(HtmlFormat.fill(Seq(
          Html("If you still want to"),
          link(appConfig.emcsTfeCancelMovementUrl(testErn, testArc), "cancel this movement"),
          Html(msgs("you can submit a new cancellation with the errors corrected."))
        ))),
        p()(HtmlFormat.fill(Seq(
          Html(msgs("However you can only cancel a movement up to the date and time recorded on the electronic administrative document (eAD). If the date and time on the eAD has passed, you can choose to")),
          link(appConfig.emcsTfeChangeDestinationUrl(testErn, testArc), "submit a change of destination", withFullStop = true)
        )))
      )
    }

    "return the correct content for an IE837 error" in {
      helper.contentForFixingError("IE837", hasFixableError = false, testErn, testArc) mustBe Seq(p()(HtmlFormat.fill(Seq(
        Html("You need to"),
        link(appConfig.emcsTfeExplainDelayUrl(testErn, testArc), "submit a new explanation of a delay", withFullStop = true)
      ))))
    }

    "return the correct content for an IE871 error" in {
      helper.contentForFixingError("IE871", hasFixableError = false, testErn, testArc) mustBe Seq(p()(HtmlFormat.fill(Seq(
        Html("You need to"),
        link(appConfig.emcsTfeExplainShortageOrExcessUrl(testErn, testArc), "submit a new explanation of a shortage or excess", withFullStop = true)
      ))))
    }

    "return the correct content for an IE818 error" in {
      helper.contentForFixingError("IE818", hasFixableError = false, testErn, testArc) mustBe Seq(p()(HtmlFormat.fill(Seq(
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
      helper.contentForFixingError("IE819", hasFixableError = false, testErn, testArc) mustBe Seq(p()(HtmlFormat.fill(Seq(
        Html("To correct any errors you must"),
        link(appConfig.emcsTfeAlertOrRejectionUrl(testErn, testArc), "submit a new alert or rejection"),
        Html("of this movement.")
      ))))
    }

    "return an empty list when the message type is not matched" in {
      helper.contentForFixingError("FAKE", hasFixableError = true, testErn, testArc) mustBe Seq.empty
    }

    "return an empty list when there are no fixable errors" in {
      helper.contentForFixingError("IE815", hasFixableError = false, testErn, testArc) mustBe Seq.empty
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
            link(appConfig.emcsTfeCancelMovementUrl(testErn, testArc), "cancel this movement"),
            Html(msgs("you can submit a new cancellation with the errors corrected."))
          ))),
          p()(HtmlFormat.fill(Seq(
            Html(msgs("However you can only cancel a movement up to the date and time recorded on the electronic administrative document (eAD). If the date and time on the eAD has passed, you can choose to")),
            link(appConfig.emcsTfeChangeDestinationUrl(testErn, testArc), "submit a change of destination", withFullStop = true)
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
            link(appConfig.emcsTfeCancelMovementUrl(testErn, testArc), "cancel this movement"),
            Html(msgs("you can submit a new cancellation with the errors corrected."))
          ))),
          p()(HtmlFormat.fill(Seq(
            Html(msgs("However you can only cancel a movement up to the date and time recorded on the electronic administrative document (eAD). If the date and time on the eAD has passed, you can choose to")),
            link(appConfig.emcsTfeChangeDestinationUrl(testErn, testArc), "submit a change of destination", withFullStop = true)
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
              link(appConfig.emcsTfeExplainDelayUrl(testErn, testArc), "submit a new explanation of a delay", withFullStop = true)
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
              link(appConfig.emcsTfeExplainDelayUrl(testErn, testArc), "submit a new explanation of a delay", withFullStop = true)
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
              link(appConfig.emcsTfeExplainShortageOrExcessUrl(testErn, testArc), "submit a new explanation of a shortage or excess", withFullStop = true)
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
              link(appConfig.emcsTfeExplainShortageOrExcessUrl(testErn, testArc), "submit a new explanation of a shortage or excess", withFullStop = true)
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
              link(appConfig.emcsTfeAlertOrRejectionUrl(testErn, testArc), "submit a new alert or rejection"),
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
              link(appConfig.emcsTfeAlertOrRejectionUrl(testErn, testArc), "submit a new alert or rejection"),
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


}
