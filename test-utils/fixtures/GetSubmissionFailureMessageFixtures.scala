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

package fixtures

import models.response.emcsTfe.messages.submissionFailure._
import play.api.libs.json.{JsArray, JsValue, Json}

trait GetSubmissionFailureMessageFixtures extends BaseFixtures {

  object IE704HeaderFixtures {

    val ie704HeaderModel: IE704Header = IE704Header(
      messageSender = "NDEA.XI",
      messageRecipient = "NDEA.XI",
      dateOfPreparation = "2001-01-01",
      timeOfPreparation = "12:00:00",
      messageIdentifier = "XI000001",
      correlationIdentifier = Some("corr123")
    )

    val ie704HeaderJson: JsValue = Json.obj(
      "messageSender" -> "NDEA.XI",
      "messageRecipient" -> "NDEA.XI",
      "dateOfPreparation" -> "2001-01-01",
      "timeOfPreparation" -> "12:00:00",
      "messageIdentifier" -> "XI000001",
      "correlationIdentifier" -> "corr123"
    )
  }

  object IE704AttributesFixtures {

    val ie704AttributesModel: IE704Attributes = IE704Attributes(
      arc = Some("22XI00000000000366000"),
      sequenceNumber = Some(1),
      lrn = Some(testLrn)
    )

    val ie704AttributesJson: JsValue = Json.obj(
      "arc" -> "22XI00000000000366000",
      "sequenceNumber" -> 1,
      "lrn" -> testLrn,
    )
  }

  object IE704FunctionalErrorFixtures {

    val ie704FunctionalErrorModel: IE704FunctionalError = IE704FunctionalError(
      errorType = "4401",
      errorReason = "Incorrect (code) value. Value of an element in a message is outside the predefined domain or not part of the applicable code list.",
      errorLocation = Some("/IE813[1]/Body[1]/SubmittedDraftOfEADESAD[1]/EadEsadDraft[1]/LocalReferenceNumber[1]"),
      originalAttributeValue = Some("lrnie8155639253")
    )

    val ie704FunctionalErrorJson: JsValue = Json.obj(
      "errorType" -> "4401",
      "errorReason" -> "Incorrect (code) value. Value of an element in a message is outside the predefined domain or not part of the applicable code list.",
      "errorLocation" -> "/IE813[1]/Body[1]/SubmittedDraftOfEADESAD[1]/EadEsadDraft[1]/LocalReferenceNumber[1]",
      "originalAttributeValue" -> "lrnie8155639253"
    )
  }

  object IE704BodyFixtures {
        
    val ie704BodyModel: IE704Body = IE704Body(
      attributes = Some(IE704AttributesFixtures.ie704AttributesModel),
      functionalError = Seq(
        IE704FunctionalErrorFixtures.ie704FunctionalErrorModel,
        IE704FunctionalError(
          errorType = "4402",
          errorReason = "Duplicate LRN The LRN is already known and is therefore not unique according to the specified rules.",
          errorLocation = Some("/IE813[1]/Body[1]/SubmittedDraftOfEADESAD[1]/EadEsadDraft[1]/LocalReferenceNumber[1]"),
          originalAttributeValue = Some("lrnie8155639254")
        )
      )
    )
    
    val ie704BodyJson: JsValue = Json.obj(
      "attributes" -> IE704AttributesFixtures.ie704AttributesJson,
      "functionalError" -> JsArray(Seq(
        IE704FunctionalErrorFixtures.ie704FunctionalErrorJson,
        Json.obj(
          "errorType" -> "4402",
          "errorReason" -> "Duplicate LRN The LRN is already known and is therefore not unique according to the specified rules.",
          "errorLocation" -> "/IE813[1]/Body[1]/SubmittedDraftOfEADESAD[1]/EadEsadDraft[1]/LocalReferenceNumber[1]",
          "originalAttributeValue" -> "lrnie8155639254"
        )
      ))
    )
  }

  object IE704ModelFixtures {

    val ie704ModelModel: IE704Model = IE704Model(
      header = IE704HeaderFixtures.ie704HeaderModel,
      body = IE704BodyFixtures.ie704BodyModel
    )
        
    val ie704ModelJson: JsValue = Json.obj(
      "header" -> IE704HeaderFixtures.ie704HeaderJson,
      "body" -> IE704BodyFixtures.ie704BodyJson
    )

    val ie704PortalSubmission: IE704Model = ie704ModelModel.copy(
      header = IE704HeaderFixtures.ie704HeaderModel.copy(correlationIdentifier = Some("PORTAL12345")),
      body = IE704BodyFixtures.ie704BodyModel.copy(
        functionalError = Seq(
          IE704FunctionalErrorFixtures.ie704FunctionalErrorModel,
          IE704FunctionalErrorFixtures.ie704FunctionalErrorModel.copy(errorType = "4402", errorReason = "Duplicate LRN The LRN is already known and is therefore not unique according to the specified rules.", originalAttributeValue = Some("lrnie8155639254")),
        )
      )
    )
  }

  object GetSubmissionFailureMessageResponseFixtures {

    val getSubmissionFailureMessageResponseModel: GetSubmissionFailureMessageResponse = GetSubmissionFailureMessageResponse(
      ie704 = IE704ModelFixtures.ie704ModelModel.copy(
        body = IE704BodyFixtures.ie704BodyModel.copy(
          functionalError = Seq(
            IE704FunctionalErrorFixtures.ie704FunctionalErrorModel,
            IE704FunctionalErrorFixtures.ie704FunctionalErrorModel.copy(errorType = "4402", errorReason = "Duplicate LRN The LRN is already known and is therefore not unique according to the specified rules.", originalAttributeValue = Some("lrnie8155639254")),
          )
        )
      ),
      relatedMessageType = Some("IE815")
    )

    val getSubmissionFailureMessageResponseJson: JsValue = Json.obj(
      "ie704" -> IE704ModelFixtures.ie704ModelJson,
      "relatedMessageType" -> "IE815"
    )
  }
}
