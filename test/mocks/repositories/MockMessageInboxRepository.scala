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

package mocks.repositories

import models.messages.MessageCache
import org.scalamock.handlers.{CallHandler1, CallHandler2}
import org.scalamock.scalatest.MockFactory
import repositories.MessageInboxRepository

import scala.concurrent.Future

trait MockMessageInboxRepository extends MockFactory {

  lazy val mockMessageInboxSessionRepository: MessageInboxRepository = mock[MessageInboxRepository]

  object MockMessageInboxRepository {
    def set(aMessage: MessageCache): CallHandler1[MessageCache, Future[Boolean]] = {
      (mockMessageInboxSessionRepository.set(_: MessageCache))
        .expects(where[MessageCache](actual =>
          actual.ern == aMessage.ern &&
          actual.message == aMessage.message
        ))
    }

    def get(ern: String, uniqueMessageIdentifier: Long): CallHandler2[String, Long, Future[Option[MessageCache]]] = {
      (mockMessageInboxSessionRepository.get(_: String, _: Long))
        .expects(ern, uniqueMessageIdentifier)
    }
  }
}
