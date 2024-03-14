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

import controllers.helpers.BetaChecks
import models.auth.UserRequest
import models.requests.DataRequest
import play.api.mvc.{Action, ActionBuilder, AnyContent, Result}

import scala.concurrent.Future

trait AuthActionHelper extends BetaChecks {

  val auth: AuthAction
  val getData: DataRetrievalAction
  val betaAllowList: BetaAllowListAction

  private def authorised(ern: String): ActionBuilder[UserRequest, AnyContent] =
    auth(ern) andThen betaAllowList(navigationHubBetaGuard())

  def authorisedWithData(ern: String): ActionBuilder[DataRequest, AnyContent] =
    authorised(ern) andThen getData()

  def authorisedWithBetaGuardData(ern: String, betaGuard: (String, Result)): ActionBuilder[DataRequest, AnyContent] =
    authorised(ern) andThen betaAllowList(betaGuard) andThen getData()

  def authorisedDataRequest(ern: String, betaGuard: Option[(String, Result)] = None)(block: DataRequest[_] => Result): Action[AnyContent] =
    if (betaGuard.isEmpty) {
      authorisedWithData(ern)(block)
    } else {
      authorisedWithBetaGuardData(ern, betaGuard.get)(block)
    }

  def authorisedDataRequestAsync(ern: String, betaGuard: Option[(String, Result)] = None)(block: DataRequest[_] => Future[Result]): Action[AnyContent] = {
    if (betaGuard.isEmpty) {
      authorisedWithData(ern).async(block)
    } else {
      authorisedWithBetaGuardData(ern, betaGuard.get).async(block)
    }
  }

}
