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

import models.requests.{DataRequest, UserAnswersRequest}
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Result}
import services.BaseUserAnswersService

import scala.concurrent.{ExecutionContext, Future}

trait BaseUserAnswersDataRetrievalAction extends ActionRefiner[DataRequest, UserAnswersRequest] {

  val userAnswersService: BaseUserAnswersService
  implicit val executionContext: ExecutionContext

  override protected def refine[A](request: DataRequest[A]): Future[Either[Result, UserAnswersRequest[A]]] =
    userAnswersService.get(request.ern).map {
      case Some(answers) => Right(UserAnswersRequest(request, answers))
      case _ => Left(Redirect(controllers.prevalidateTrader.routes.StartPrevalidateTraderController.onPageLoad(request.ern)))
    }
}
