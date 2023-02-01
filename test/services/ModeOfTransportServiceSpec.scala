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

package services

import fixtures.ModeOfTransportListFixture
import mocks.config.MockAppConfig
import mocks.connectors.{MockEmcsTfeConnector, MockReferenceDataConnector}
import models.response.UnexpectedDownstreamResponseError
import models.response.referenceData.{ModeOfTransportListModel, ModeOfTransportModel}
import support.UnitSpec
import services.ModeOfTransportService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class ModeOfTransportServiceSpec extends UnitSpec with ModeOfTransportListFixture {

  trait Test extends MockReferenceDataConnector with MockEmcsTfeConnector with MockAppConfig {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

    val service: ModeOfTransportService = new ModeOfTransportService(
      mockGetOtherReferenceDataListConnector,
      mockAppConfig
    )
  }

  "getOtherDataReferenceList" should {
    "with the emcs reference data stub on" should {
      "return a successful other reference data list" when {
        "connector returns a success" in new Test {

          MockedAppConfig.getReferenceDataStubFeatureSwitch.returns(true)
          MockReferenceDataConnector.getOtherReferenceDataList().returns(Future.successful(Right(validModeOfTransportResponseListModel)))

          await(service.getOtherDataReferenceList(hc, ec)) shouldBe Right(validModeOfTransportResponseListModel)
        }
      }
      "return an unsuccessful other reference data list" when {
        "reference data connector returns a failure" in new Test {
          MockedAppConfig.getReferenceDataStubFeatureSwitch.returns(true)
          MockReferenceDataConnector.getOtherReferenceDataList().returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

          await(service.getOtherDataReferenceList(hc, ec)) shouldBe Left(UnexpectedDownstreamResponseError)
        }
      }
    }
    "with the emcs reference data stub off" should {
      "return a successful other reference data list" when {
        "connector returns a success" in new Test {
          MockedAppConfig.getReferenceDataStubFeatureSwitch.returns(false)
          await(service.getOtherDataReferenceList(hc, ec)) shouldBe Right(ModeOfTransportListModel(List(ModeOfTransportModel("TRANSPORTMODE", "999", "hard coded response" ))))
        }
      }
    }
  }
}
