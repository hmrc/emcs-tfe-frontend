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

package mocks.viewmodels

import models.requests.DataRequest
import models.response.emcsTfe.GetMovementResponse
import org.scalamock.handlers.CallHandler6
import org.scalamock.scalatest.MockFactory
import play.api.i18n.Messages
import play.twirl.api.Html
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels.SubNavigationTab
import viewmodels.helpers.ViewMovementHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

trait MockViewMovementHelper extends MockFactory {

  lazy val mockViewMovementHelper: ViewMovementHelper = mock[ViewMovementHelper]

  object MockViewMovementHelper {

    def movementCard(returns: Html = Html("")): CallHandler6[SubNavigationTab, GetMovementResponse, DataRequest[_], Messages, HeaderCarrier, ExecutionContext, Future[Html]] =
      (mockViewMovementHelper.movementCard(_ : SubNavigationTab, _ : GetMovementResponse)(_ :DataRequest[_], _: Messages, _: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *, *, *, *)
        .returns(Future(returns))
  }
}
