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
import models.requests.DataRequest
import play.api.mvc.ActionTransformer
import services.{GetMessageStatisticsService, GetTraderKnownFactsService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DataRetrievalActionImpl @Inject()(getTraderKnownFactsService: GetTraderKnownFactsService,
                                        getMessageStatisticsService: GetMessageStatisticsService)
                                       (implicit val ec: ExecutionContext) extends DataRetrievalAction {

  def apply(): ActionTransformer[UserRequest, DataRequest] = new ActionTransformer[UserRequest, DataRequest] {

    override val executionContext = ec

    override protected def transform[A](request: UserRequest[A]): Future[DataRequest[A]] = {

      implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

      for {
        traderKnownFacts <- getTraderKnownFactsService.getTraderKnownFacts(request.ern)
        messageStatistics <- getMessageStatisticsService.getMessageStatistics(request.ern)(hc, request)
      } yield {
        DataRequest(request, traderKnownFacts, messageStatistics)
      }
    }
  }
}

trait DataRetrievalAction {
  def apply(): ActionTransformer[UserRequest, DataRequest]
}
