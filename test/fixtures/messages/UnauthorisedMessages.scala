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

package fixtures.messages

object UnauthorisedMessages {

  sealed trait ViewMessages { _: i18n =>
    val title: String
    val heading: String
    val message: String
  }

  object English extends ViewMessages with EN {
    override val title: String = "Unauthorised"
    override val heading: String = "Unauthorised"
    override val message: String = "You are not authorised to access this service"
  }

  object Welsh extends ViewMessages with CY {
    override val title: String = "Unauthorised"
    override val heading: String = "Unauthorised"
    override val message: String = "You are not authorised to access this service"
  }
}
