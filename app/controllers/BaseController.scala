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

package controllers

import models.common.Enumerable
import models.requests.UserAnswersRequest
import pages.QuestionPage
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.libs.json.Format
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.Logging

import scala.concurrent.ExecutionContext

trait BaseController extends FrontendBaseController with I18nSupport with Enumerable.Implicits with Logging {

  implicit lazy val ec: ExecutionContext = controllerComponents.executionContext

  /**
   * @param page    - the question page to search user answers for
   * @param form    - the form to fill with user's previous answer
   * @param request - implicit request which contains user answers
   * @param format  - implicit JSON format for userAnswers.get(page)
   * @tparam A - generic type to ensure form and format have the same type parameter
   * @return pre-filled form if data exists for this page. If no data exists, return an empty form
   */
  def fillForm[A](page: QuestionPage[A], form: Form[A])
                 (implicit request: UserAnswersRequest[_], format: Format[A]): Form[A] =
    request.userAnswers.get(page).fold(form)(form.fill)
}
