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

import models._
import models.requests.UserAnswersRequest
import navigation.BaseNavigator
import pages.QuestionPage
import play.api.libs.json.{Format, Reads}
import play.api.mvc.Result
import queries.Derivable
import services.BaseUserAnswersService
import utils.Logging

import scala.concurrent.Future

trait BaseNavigationController extends BaseController with Logging {

  val userAnswersService: BaseUserAnswersService
  val navigator: BaseNavigator

  def saveAndRedirect[A](page: QuestionPage[A], answer: A, currentAnswers: UserAnswers, mode: Mode)
                        (implicit format: Format[A]): Future[Result] =
    save(page, answer, currentAnswers).map { updatedAnswers =>
      Redirect(navigator.nextPage(page, mode, updatedAnswers))
    }

  def saveAndRedirect[A](page: QuestionPage[A], answer: A, mode: Mode)
                        (implicit request: UserAnswersRequest[_], format: Format[A]): Future[Result] =
    save(page, answer, request.userAnswers).map { updatedAnswers =>
      Redirect(navigator.nextPage(page, mode, updatedAnswers))
    }

  private def save[A](page: QuestionPage[A], answer: A, currentAnswers: UserAnswers)
                     (implicit format: Format[A]): Future[UserAnswers] =
    if (currentAnswers.get[A](page).contains(answer)) {
      Future.successful(currentAnswers)
    } else {
      for {
        updatedAnswers <- Future.successful(currentAnswers.set(page, answer))
        _ <- userAnswersService.set(updatedAnswers)
      } yield updatedAnswers
    }

  def validateIndexForJourneyEntry[T, A](
                                          itemCount: Derivable[T, Int], idx: Index, max: Int = Int.MaxValue
                                        )(onSuccess: => A, onFailure: => A)(implicit request: UserAnswersRequest[_], reads: Reads[T]): A =
    request.userAnswers.get(itemCount) match {
      case Some(value) if (idx.position >= 0 && idx.position <= value) && idx.position < max => onSuccess
      case None if idx.position == 0 => onSuccess
      case _ => onFailure
    }
}
