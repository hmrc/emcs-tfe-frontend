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

import models.UserAnswers
import org.scalamock.handlers.CallHandler1
import org.scalamock.scalatest.MockFactory
import services.{BaseUserAnswersService, PrevalidateTraderUserAnswersService}

import scala.concurrent.Future

trait MockPreValidateUserAnswersService extends MockUserAnswersService {
  override lazy val mockUserAnswersService: PrevalidateTraderUserAnswersService = mock[PrevalidateTraderUserAnswersService]
}

trait MockUserAnswersService extends MockFactory {


  lazy val mockUserAnswersService: BaseUserAnswersService = mock[BaseUserAnswersService]

  object MockUserAnswersService {

    def get(ern: String): CallHandler1[String, Future[Option[UserAnswers]]] =
      (mockUserAnswersService.get(_: String)).expects(ern)

    def set(userAnswers: UserAnswers): CallHandler1[UserAnswers, Future[UserAnswers]] =
      (mockUserAnswersService.set(_: UserAnswers))
        .expects(where[UserAnswers] { actualAnswers =>
          actualAnswers.ern == userAnswers.ern &&
            actualAnswers.data == userAnswers.data
        })

    def set(): CallHandler1[UserAnswers, Future[UserAnswers]] =
      (mockUserAnswersService.set(_: UserAnswers)).expects(*)

    def clear(userAnswers: UserAnswers): CallHandler1[UserAnswers, Future[Boolean]] =
      (mockUserAnswersService.remove(_: UserAnswers)).expects(userAnswers)
  }
}
