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

package base

import config.{AppConfig, ErrorHandler}
import fixtures.BaseFixtures
import models.UserAnswers
import models.auth.UserRequest
import models.requests.{DataRequest, UserAnswersRequest}
import org.scalamock.scalatest.MockFactory
import org.scalatest.OptionValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{Lang, Messages, MessagesApi}
import play.api.mvc.{MessagesControllerComponents, Request}
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}

trait SpecBase extends AnyWordSpecLike with Matchers with MockFactory with OptionValues with ScalaFutures with BaseFixtures with FutureAwaits with DefaultAwaitTimeout with GuiceOneAppPerSuite {

  lazy val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  lazy implicit val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]
  lazy val messagesControllerComponents: MessagesControllerComponents = app.injector.instanceOf[MessagesControllerComponents]
  lazy val errorHandler: ErrorHandler = app.injector.instanceOf[ErrorHandler]

  def messages(request: Request[_]): Messages = app.injector.instanceOf[MessagesApi].preferred(request)

  def messages(candidates: Seq[Lang]): Messages = app.injector.instanceOf[MessagesApi].preferred(candidates)

  def userRequest[A](request: Request[A], ern: String = testErn): UserRequest[A] =
    UserRequest(request, ern, testInternalId, testCredId, hasMultipleErns = false)

  def dataRequest[A](request: Request[A], ern: String = testErn): DataRequest[A] =
    DataRequest(userRequest(request, ern), testMinTraderKnownFacts, testMessageStatistics)

  def userAnswersRequest[A](request: Request[A], userAnswers: UserAnswers = emptyUserAnswers): UserAnswersRequest[A] =
    UserAnswersRequest(dataRequest(request, userAnswers.ern), userAnswers)

}
