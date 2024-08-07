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

package models.requests

import models.UserAnswers
import models.common.RoleType.RoleType
import play.api.mvc.WrappedRequest

case class UserAnswersRequest[A](request: DataRequest[A], userAnswers: UserAnswers) extends WrappedRequest[A](request) {
  val internalId: String = request.internalId
  val ern: String = request.ern
  val isWarehouseKeeper: Boolean = request.isWarehouseKeeper
  val isRegisteredConsignor: Boolean = request.isRegisteredConsignor
  val userTypeFromErn: RoleType = request.userTypeFromErn
  val messageStatistics = request.messageStatistics
}
