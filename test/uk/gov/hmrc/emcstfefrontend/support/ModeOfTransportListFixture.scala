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

package uk.gov.hmrc.emcstfefrontend.support

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.emcstfefrontend.models.response.{ModeOfTransportErrorResponse, ModeOfTransportListModel, ModeOfTransportModel}

object ModeOfTransportListFixture {

  val validModeOfTransportJson: JsValue = Json.parse(
    """
      |{
      |   "typeName":"TransportMode",
      |   "code":"0",
      |   "description":"Other"
      |}
			|""".stripMargin)

  val modeOfTransportErrorJson: JsValue = Json.parse(
    """
      |{
      |   "status":404,
      |   "reason":"error"
      |}
			|""".stripMargin)

  val validModeOfTransportResponseModel1: ModeOfTransportModel =
    ModeOfTransportModel(typeName = "TransportMode", code = "0", description = "Other")

  val modeOfTransportError: ModeOfTransportErrorResponse =
    ModeOfTransportErrorResponse(status = 404, reason = "error")


  val validModeOfTransportResponseModel2: ModeOfTransportModel =
    ModeOfTransportModel(typeName = "TransportMode", code = "5", description = "Postal consignment")


  val validModeOfTransportListJson: JsValue = Json.parse(
    """
      | {
      |   "otherRefdata":
      |     [
      |       {
      |          "typeName":"TransportMode",
      |          "code":"0",
      |          "description":"Other"
      |       },
      |       {
      |          "typeName":"TransportMode",
      |          "code":"5",
      |          "description":"Postal consignment"
      |       }
      |     ]
      | }
			|""".stripMargin)

  val validModeOfTransportResponseListModel: ModeOfTransportListModel = ModeOfTransportListModel(
    List(validModeOfTransportResponseModel1,validModeOfTransportResponseModel2)
  )

}