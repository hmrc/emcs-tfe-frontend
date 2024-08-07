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

package config

import uk.gov.hmrc.auth.core.{Enrolment, EnrolmentIdentifier}

object EnrolmentKeys {

  val EMCS_ENROLMENT = "HMRC-EMCS-ORG"
  val ERN = "ExciseNumber"
  val ACTIVATED = "activated"
  val INACTIVE = "inactive"

  def withActiveEmcsEnrolment(ern: String): Enrolment = Enrolment(
    EnrolmentKeys.EMCS_ENROLMENT,
    Seq(EnrolmentIdentifier(EnrolmentKeys.ERN, ern)),
    EnrolmentKeys.ACTIVATED
  )

}
