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

package uk.gov.hmrc.emcstfefrontend.controllers.predicates

import play.api.mvc.ActionTransformer
import uk.gov.hmrc.emcstfefrontend.models.auth.UserRequest
import uk.gov.hmrc.emcstfefrontend.models.common.TraderKnownFacts
import uk.gov.hmrc.emcstfefrontend.models.requests.OptionalDataRequest

import scala.concurrent.{ExecutionContext, Future}

class FakeDataRetrievalAction(optTraderKnownFacts: Option[TraderKnownFacts]) extends DataRetrievalAction {

  def apply(): ActionTransformer[UserRequest, OptionalDataRequest] =

    new ActionTransformer[UserRequest, OptionalDataRequest] {

      override def transform[A](request: UserRequest[A]): Future[OptionalDataRequest[A]] =
        Future(OptionalDataRequest(request, optTraderKnownFacts))

      override protected implicit val executionContext: ExecutionContext =
        scala.concurrent.ExecutionContext.Implicits.global
    }
}
