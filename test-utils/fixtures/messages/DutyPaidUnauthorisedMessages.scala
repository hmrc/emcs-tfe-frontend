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

object DutyPaidUnauthorisedMessages {

  sealed trait ViewMessages { _: i18n =>
    val title: String
    val heading: String
    val message1: String
    val message2: String
    val message3: String
  }

  object English extends ViewMessages with EN {
    override val title: String = "There is a problem"
    override val heading: String = "There is a problem"
    override val message1: String = "Duty paid traders cannot yet use the Excise Movement and Control System (EMCS)."
    override val message2: String = "HMRC will notify you when you are able to use the service."
    override val message3: String = "If you think you have signed in with the wrong details, sign out and check the details you have are correct."
  }

}
