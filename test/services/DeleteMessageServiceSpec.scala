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

package services

import base.SpecBase
import fixtures.ItemFixtures
import mocks.connectors.MockDeleteMessageConnector
import mocks.repositories.MockMessageInboxRepository
import models.response.emcsTfe.messages.DeleteMessageResponse
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}


class DeleteMessageServiceSpec extends SpecBase with ItemFixtures with MockDeleteMessageConnector with MockMessageInboxRepository {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global

  lazy val testService = new DeleteMessageService(mockDeleteMessageConnector, mockMessageInboxSessionRepository)

  ".deleteMessage" must {
    "return DeleteMessageResponse " when {
      "Connector returns success from..." in {

        MockMessageInboxRepository.delete(testErn, testUniqueMessageIdentifier).returns(
          Future.successful(true)
        )

        MockDeleteMessageConnector.deleteMessage(testErn, testUniqueMessageIdentifier).returns(
          Future.successful(Right(DeleteMessageResponse(recordsAffected = 1)))
        )

        testService.deleteMessage(testErn, testUniqueMessageIdentifier)(hc).futureValue mustBe DeleteMessageResponse(1)

      }
    }

    "throw" when {
      "..." in {

        MockMessageInboxRepository.delete(testErn, testUniqueMessageIdentifier).returns(
          Future.successful(true)
        )

        MockDeleteMessageConnector.deleteMessage(testErn, testUniqueMessageIdentifier).returns(
          Future.successful(Right(DeleteMessageResponse(recordsAffected = 1)))
        )

        testService.deleteMessage(testErn, testUniqueMessageIdentifier)(hc).futureValue mustBe DeleteMessageResponse(1)

      }
    }
  }


}
