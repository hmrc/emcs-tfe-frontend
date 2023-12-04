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

import play.api.mvc.Result
import uk.gov.hmrc.emcstfefrontend.models.common.TraderKnownFacts
import uk.gov.hmrc.emcstfefrontend.models.requests.{DataRequest, OptionalDataRequest}

import scala.concurrent.{ExecutionContext, Future}

class FakeDataRequiredAction (traderKnownFacts: TraderKnownFacts) extends DataRequiredAction {
  override protected def refine[A](request: OptionalDataRequest[A]): Future[Either[Result, DataRequest[A]]] = {
    Future.successful(Right(DataRequest(request.request, traderKnownFacts)))

  }

  override protected def executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
}
