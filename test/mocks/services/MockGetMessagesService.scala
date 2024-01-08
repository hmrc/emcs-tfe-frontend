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

package mocks.services

import models.messages.MessagesSearchOptions
import models.response.emcsTfe.messages.GetMessagesResponse
import org.scalamock.handlers.CallHandler3
import org.scalamock.scalatest.MockFactory
import services.GetMessagesService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future


trait MockGetMessagesService extends MockFactory {

  lazy val mockGetMessagesService: GetMessagesService = mock[GetMessagesService]

  object MockGetMessagesService {
    def getMessages(ern: String, search: Option[MessagesSearchOptions]): CallHandler3[String, Option[MessagesSearchOptions], HeaderCarrier, Future[GetMessagesResponse]] =
      (mockGetMessagesService.getMessages(_: String, _: Option[MessagesSearchOptions])(_: HeaderCarrier))
        .expects(ern, search, *)
  }
}
