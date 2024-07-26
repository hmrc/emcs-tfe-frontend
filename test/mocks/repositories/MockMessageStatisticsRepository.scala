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

import models.messages.MessageStatisticsCache
import org.scalamock.handlers.CallHandler1
import org.scalamock.scalatest.MockFactory
import repositories.MessageStatisticsRepository

import scala.concurrent.Future

trait MockMessageStatisticsRepository extends MockFactory {

  lazy val mockMessageStatisticsRepository: MessageStatisticsRepository = mock[MessageStatisticsRepository]

  object MockMessageStatisticsRepository {
    def set(stats: MessageStatisticsCache): CallHandler1[MessageStatisticsCache, Future[Boolean]] =
      (mockMessageStatisticsRepository.set(_: MessageStatisticsCache))
        .expects(where[MessageStatisticsCache](actual =>
          actual.ern == stats.ern &&
          actual.statistics == stats.statistics
        ))

    def get(ern: String): CallHandler1[String, Future[Option[MessageStatisticsCache]]] =
      (mockMessageStatisticsRepository.get(_: String)).expects(ern)
  }
}
