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

package uk.gov.hmrc.emcstfefrontend.services

import play.api.http.Status.INTERNAL_SERVER_ERROR
import uk.gov.hmrc.emcstfefrontend.mocks.config.MockAppConfig
import uk.gov.hmrc.emcstfefrontend.mocks.connectors.{MockEmcsTfeConnector, MockReferenceDataConnector}
import uk.gov.hmrc.emcstfefrontend.models.response.{ModeOfTransportErrorResponse, ModeOfTransportListModel, ModeOfTransportModel}
import uk.gov.hmrc.emcstfefrontend.support.ModeOfTransportListFixture.validModeOfTransportResponseListModel
import uk.gov.hmrc.emcstfefrontend.support.UnitSpec
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class ModeOfTransportServiceSpec extends UnitSpec with MockReferenceDataConnector with MockEmcsTfeConnector with MockAppConfig {

  trait Test {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

    val service: ModeOfTransportService = new ModeOfTransportService(
      mockReferenceDataConnector,
      mockAppConfig
    )
  }

  "getOtherDataReferenceList" should {
    "with the emcs reference data stub on" should {
      "return a successful other reference data list" when {
        "connector returns a success" in new Test {

          MockedAppConfig.getReferenceDataStubFeatureSwitch.returns(true)
          MockReferenceDataConnector.getOtherReferenceDataList().returns(Future.successful(validModeOfTransportResponseListModel))

          await(service.getOtherDataReferenceList(hc, ec)) shouldBe validModeOfTransportResponseListModel
        }
      }
      "return a un - successful other reference data list" when {
        "reference data connector returns a failure" in new Test {
          MockedAppConfig.getReferenceDataStubFeatureSwitch.returns(true)
          MockReferenceDataConnector.getOtherReferenceDataList().returns(Future.successful(ModeOfTransportErrorResponse(INTERNAL_SERVER_ERROR, "issue encountered")))

          await(service.getOtherDataReferenceList(hc, ec)) shouldBe ModeOfTransportErrorResponse(INTERNAL_SERVER_ERROR, "issue encountered")
        }
      }
    }
    "with the emcs reference data stub off" should {
      "return a successful other reference data list" when {
        "connector returns a success" in new Test {
          MockedAppConfig.getReferenceDataStubFeatureSwitch.returns(false)
          await(service.getOtherDataReferenceList(hc, ec)) shouldBe ModeOfTransportListModel(List(ModeOfTransportModel("TRANSPORTMODE", "999", "hard coded response" )))
        }
      }
    }
  }
}
