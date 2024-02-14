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
import fixtures.{GetSubmissionFailureMessageFixtures, MessagesFixtures}
import models.messages.MessageCache
import models.requests.DataRequest
import models.response.emcsTfe.messages.submissionFailure.{GetSubmissionFailureMessageResponse, IE704FunctionalError}
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases.{Key, SummaryListRow, Text, Value}
import uk.gov.hmrc.govukfrontend.views.html.components._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import views.html.components.{h2, link, list, p}

class ViewMessageHelperSpec extends SpecBase with MessagesFixtures with GetSubmissionFailureMessageFixtures {

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

  ".constructMovementInformation" in {
    val result: Html = helper.constructMovementInformation(message1)
    removeNewLines(result.toString()) mustBe removeNewLines(govukSummaryList(SummaryList(Seq(
      SummaryListRow(
        key = Key(Text(value = ViewMessageMessages.English.labelMessageType)),
        value = Value(Text(value = ViewMessageMessages.English.messageTypeDescriptionForIE819))
      ),
      SummaryListRow(
        key = Key(Text(value = ViewMessageMessages.English.labelArc)),
        value = Value(Text(value = message1.arc.get))
      ),
      SummaryListRow(
        key = Key(Text(value = ViewMessageMessages.English.labelLrn)),
        value = Value(Text(value = message1.lrn.get))
      )
    ))).toString())

  }

  ".constructActions" must {

    "return the correct action links" when {

      "the message is an IE819" in {
        val testMessage = createMessage("IE819")
        val result: Html = helper.constructActions(testMessage)

        result mustBe
          HtmlFormat.fill(
            Seq(
              list(
                Seq(
                  link(
                    link = controllers.routes.ViewMovementController.viewMovementOverview(request.ern, testMessage.arc.getOrElse("")).url,
                    messageKey = "View movement",
                    id = Some("view-movement"),
                    classes = "govuk-link"
                  ),
                  link(
                    link = "#print-dialogue",
                    messageKey = "Print message",
                    id = Some("print-link"),
                    classes = "govuk-link"
                  ),
                  link(
                    link = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
                    messageKey = "Delete message",
                    id = Some("delete-message"),
                    classes = "govuk-link"
                  )
                ),
                extraClasses = Some("govuk-!-display-none-print")
              )
            )
          )
      }
    }
  }

  ".constructErrors" must {

    "build a summary list of all the errors in the failure message (code -> description) - and a heading" in {
      val message = MessageCache(testErn, ie704ErrorCancellationIE810.message, Some(getSubmissionFailureMessageResponseModel))
      val result = helper.constructErrors(message)
      removeNewLines(result.toString()) mustBe removeNewLines(HtmlFormat.fill(Seq(
        h2("Errors"),
        govukSummaryList(SummaryList(Seq(
          SummaryListRow(
            key = Key(Text(value = IE704FunctionalErrorFixtures.ie704FunctionalErrorModel.errorType)),
            value = Value(Text(value = msgs(s"messages.IE704.error.${IE704FunctionalErrorFixtures.ie704FunctionalErrorModel.errorType}")))
          ),
          SummaryListRow(
            key = Key(Text(value = "1235")),
            value = Value(Text(value = msgs("messages.IE704.error.1235")))
          )
        )))
      )).toString())
    }
  }

  ".contentForSubmittedVia3rdParty" must {

    "return the correct content when the submission was made via a 3rd party" in {
      helper.contentForSubmittedVia3rdParty("IE810", hasBeenSubmittedVia3rdParty = true) mustBe Seq(p() {
        Html("If you used commercial software for your submission, please correct these errors with the same software that you used for the submission.")
      })
    }

    "return an empty list when the submission was not made by a 3rd party" in {
      helper.contentForSubmittedVia3rdParty("IE810", hasBeenSubmittedVia3rdParty = false) mustBe Seq.empty
    }

  }

  ".contentForContactingHelpdesk" must {

    "return the correct content for an IE810 error" in {
      helper.contentForContactingHelpdesk("IE810") mustBe Seq(p() {
        HtmlFormat.fill(Seq(
          link(appConfig.exciseHelplineUrl, "Contact the HMRC excise helpline"),
          Html("if you need more help or advice.")
        ))
      })
    }

    "return an empty list when the message type is not matched" in {
      helper.contentForContactingHelpdesk("FAKE") mustBe Seq.empty
    }

  }

  ".contentForFixableError" must {
    "return the correct content for an IE815 error" in {
      //TODO: update when IE815 submission failure message is played (ETFE-2737)
      helper.contentForFixableError("IE815", hasFixableError = true, testErn, testArc) mustBe Seq(Html("placeholder"))
    }

    "return an empty list when the message type is not matched" in {
      helper.contentForFixableError("FAKE", hasFixableError = true, testErn, testArc) mustBe Seq.empty
    }

    "return an empty list when there are no fixable errors" in {
      helper.contentForFixableError("IE815", hasFixableError = false, testErn, testArc) mustBe Seq.empty
    }
  }

  ".constructFixErrorsContent" should {

    "for an IE810" must {

      val ie810Message = message2.copy(relatedMessageType = Some("IE810"), arc = Some(testArc))

      //IE810 submissions are always non-fixable
      "return the correct content when the errors are non-fixable, 3rd party submission" in {
        val failureMessageResponse = GetSubmissionFailureMessageResponse(
          ie704 = IE704ModelFixtures.ie704ModelModel.copy(
            body = IE704BodyFixtures.ie704BodyModel.copy(functionalError = Seq(
              IE704FunctionalError(
                errorType = "4403",
                errorReason = "Oh no! Duplicate LRN The LRN is already known and is therefore not unique according to the specified rules",
                errorLocation = Some("/IE813[1]/Body[1]/SubmittedDraftOfEADESAD[1]/EadEsadDraft[1]/LocalReferenceNumber[1]"),
                originalAttributeValue = Some("lrnie8155639254")
              )
            ))
          ),
          relatedMessageType = Some("IE810")
        )
        val result = helper.constructFixErrorsContent(MessageCache(testErn, ie810Message, Some(failureMessageResponse)))
        removeNewLines(result.toString()) mustBe removeNewLines(HtmlFormat.fill(Seq(
          p() {
            Html("If you used commercial software for your submission, please correct these errors with the same software that you used for the submission.")
          },
          p() {
            HtmlFormat.fill(Seq(
              link(appConfig.exciseHelplineUrl, "Contact the HMRC excise helpline"),
              Html("if you need more help or advice.")
            ))
          }
        )).toString())
      }

      "return the correct content when the errors are non-fixable, portal submission" in {
        val failureMessageResponse = GetSubmissionFailureMessageResponse(
          ie704 = IE704ModelFixtures.ie704ModelModel.copy(
            header = IE704HeaderFixtures.ie704HeaderModel.copy(correlationIdentifier = Some("PORTAL12345")),
            body = IE704BodyFixtures.ie704BodyModel.copy(functionalError = Seq(
              IE704FunctionalError(
                errorType = "4403",
                errorReason = "Oh no! Duplicate LRN The LRN is already known and is therefore not unique according to the specified rules",
                errorLocation = Some("/IE813[1]/Body[1]/SubmittedDraftOfEADESAD[1]/EadEsadDraft[1]/LocalReferenceNumber[1]"),
                originalAttributeValue = Some("lrnie8155639254")
              )
            ))
          ),
          relatedMessageType = Some("IE810")
        )
        val result = helper.constructFixErrorsContent(MessageCache(testErn, ie810Message, Some(failureMessageResponse)))
        removeNewLines(result.toString()) mustBe removeNewLines(HtmlFormat.fill(Seq(
          p() {
            HtmlFormat.fill(Seq(
              link(appConfig.exciseHelplineUrl, "Contact the HMRC excise helpline"),
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


}
