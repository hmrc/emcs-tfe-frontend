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

package models.response.emcsTfe.messages.submissionFailure

object FunctionalErrorCodes {

  //May need to change
  val unRecoverableErrorCodes: Seq[String] = Seq("4403", "4411", "4422", "4424", "4454", "4457", "4485", "4488", "4494", "4495", "4496", "4497", "4498", "4501", "4505", "4513", "4517", "4518"," 9001", "9002", "9003", "9004", "9005", "9007", "9009", "9010")

  def isFixable(errorCode: String): Boolean = !unRecoverableErrorCodes.contains(errorCode)
}
