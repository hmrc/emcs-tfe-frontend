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

import models.auth.UserRequest
import models.common.TraderKnownFacts
import models.requests.DataRequest
import models.response.emcsTfe.GetMessageStatisticsResponse
import play.api.mvc.ActionTransformer

import scala.concurrent.{ExecutionContext, Future}

class FakeDataRetrievalAction(traderKnownFacts: Option[TraderKnownFacts],
                              messageStatisticsResponse: Option[GetMessageStatisticsResponse]) extends DataRetrievalAction {

  def apply(): ActionTransformer[UserRequest, DataRequest] =

    new ActionTransformer[UserRequest, DataRequest] {

      override def transform[A](request: UserRequest[A]): Future[DataRequest[A]] =
        Future(DataRequest(request, traderKnownFacts, messageStatisticsResponse))

      override protected implicit val executionContext: ExecutionContext =
        scala.concurrent.ExecutionContext.Implicits.global
    }
}
