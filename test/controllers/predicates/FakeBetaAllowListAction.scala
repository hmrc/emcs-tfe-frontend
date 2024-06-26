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

package controllers.predicates

import fixtures.BaseFixtures
import models.auth.UserRequest
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class FakeBetaAllowListAction extends BetaAllowListAction with BaseFixtures {

  def apply(betaGuard: (String, Result)): ActionRefiner[UserRequest, UserRequest] = new ActionRefiner[UserRequest, UserRequest] {

      override implicit val executionContext: ExecutionContext =
        scala.concurrent.ExecutionContext.Implicits.global

      override protected def refine[A](request: UserRequest[A]): Future[Either[Result, UserRequest[A]]] =
        Future.successful(Right(request))
  }
}