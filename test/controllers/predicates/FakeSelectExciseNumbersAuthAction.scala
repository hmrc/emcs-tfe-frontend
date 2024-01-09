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

package controllers.predicates

import base.SpecBase
import fixtures.BaseFixtures
import models.auth.ExciseEnrolmentsRequest
import play.api.mvc._
import play.api.test.StubBodyParserFactory
import uk.gov.hmrc.auth.core.Enrolment

import scala.concurrent.{ExecutionContext, Future}

trait FakeSelectExciseNumbersAuthAction extends StubBodyParserFactory with BaseFixtures { _: SpecBase =>

  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  class FakeSuccessSelectExciseNumbersAuthAction(enrolments: Set[Enrolment]) extends SelectExciseNumberAuthAction {

    override def invokeBlock[A](request: Request[A], block: ExciseEnrolmentsRequest[A] => Future[Result]): Future[Result] =
      block(ExciseEnrolmentsRequest(request, enrolments, testInternalId, testCredId))

    override def parser: BodyParser[AnyContent] =
      stubBodyParser()

    override protected def executionContext: ExecutionContext =
      scala.concurrent.ExecutionContext.Implicits.global
  }
}