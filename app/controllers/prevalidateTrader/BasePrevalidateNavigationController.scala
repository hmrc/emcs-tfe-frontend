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

package controllers.prevalidateTrader

import controllers.BaseNavigationController
import models.Index
import models.requests.UserAnswersRequest
import play.api.mvc.Result
import queries.PreValidateTraderEPCCount

import scala.concurrent.Future

trait BasePrevalidateNavigationController extends BaseNavigationController {

  def validateIndex(index: Index)(onSuccess: => Result)(implicit request: UserAnswersRequest[_]): Result = {
    super.validateIndex(PreValidateTraderEPCCount, index)(
      onSuccess,
      Redirect(controllers.prevalidateTrader.routes.PrevalidateTraderStartController.onPageLoad(request.ern))
    )
  }

  def validateIndexAsync(index: Index)(onSuccess: => Future[Result])(implicit request: UserAnswersRequest[_]): Future[Result] = {
    super.validateIndex(PreValidateTraderEPCCount, index)(
      onSuccess,
      Future.successful(Redirect(controllers.prevalidateTrader.routes.PrevalidateTraderStartController.onPageLoad(request.ern)))
    )
  }
}
