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

package fixtures.messages

object NotOnBetaListMessages {

  sealed trait ViewMessages { _: i18n =>
    val title: String
    val heading: String
    val p1: String
    val alreadySignedUpH2: String
    val alreadySignedUpP1: String
    val alreadySignedUpP2: String
    val notSignedUpH2: String
    val notSignedUpP1: String
    val notSignedUpP2: String
  }

  object English extends ViewMessages with BaseEnglish {
    override val heading = "There is a problem"
    override val title = titleHelper("There is a problem")
    override val p1 = "You do not have permission to view this page."
    override val alreadySignedUpH2 = "If you have signed up for the Excise Movement and Control System (EMCS) private beta research"
    override val alreadySignedUpP1 = "If you typed the web address, check it is correct. If you pasted the web address, check you copied the entire address."
    override val alreadySignedUpP2 = "If you think you have signed in with the wrong details, sign out and check the details you have are correct."
    override val notSignedUpH2 = "If you have not signed up for the EMCS private beta research"
    override val notSignedUpP1 = "You can choose to take part in the EMCS private beta research if you are not already involved."
    override val notSignedUpP2 = "To take part you must currently submit EMCS receipts using the HMRC platform."
  }
}