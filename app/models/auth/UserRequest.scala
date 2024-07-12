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

package models.auth

import models.common.RoleType
import models.common.RoleType._
import play.api.i18n.MessagesApi
import play.api.mvc.{MessagesRequestHeader, PreferredMessagesProvider, Request, WrappedRequest}

case class UserRequest[A](request: Request[A],
                          ern: String,
                          internalId: String,
                          credId: String,
                          hasMultipleErns: Boolean)(implicit val messagesApi: MessagesApi) extends WrappedRequest[A](request) with PreferredMessagesProvider with MessagesRequestHeader {

  lazy val userTypeFromErn: RoleType = RoleType.fromExciseRegistrationNumber(ern)
  lazy val isWarehouseKeeper: Boolean = (userTypeFromErn == GBWK) || (userTypeFromErn == XIWK)
  lazy val isRegisteredConsignor: Boolean = (userTypeFromErn == GBRC) || (userTypeFromErn == XIRC)
  lazy val isCertifiedConsignor: Boolean = (userTypeFromErn == XIPA) || (userTypeFromErn == XIPC)
}
