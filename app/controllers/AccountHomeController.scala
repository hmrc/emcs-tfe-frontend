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

import config.AppConfig
import controllers.helpers.BetaChecks
import controllers.predicates.{AuthAction, AuthActionHelper, BetaAllowListAction, DataRetrievalAction}
import models.common.RoleType
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.AccountHomeView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AccountHomeController @Inject()(mcc: MessagesControllerComponents,
                                      view: AccountHomeView,
                                      val auth: AuthAction,
                                      val getData: DataRetrievalAction,
                                      val betaAllowList: BetaAllowListAction
                                     )(implicit val executionContext: ExecutionContext, appConfig: AppConfig)
  extends FrontendController(mcc) with AuthActionHelper with I18nSupport with BetaChecks {

  def viewAccountHome(exciseRegistrationNumber: String): Action[AnyContent] = {
    authorisedDataRequestAsync(exciseRegistrationNumber, homeBetaGuard(exciseRegistrationNumber)) { implicit request =>
      Future.successful(
        Ok(
          view(
            exciseRegistrationNumber,
            RoleType.fromExciseRegistrationNumber(exciseRegistrationNumber)
          )
        )
      )
    }
  }

}

