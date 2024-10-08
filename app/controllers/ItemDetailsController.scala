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

import controllers.predicates.{AuthAction, AuthActionHelper, DataRetrievalAction}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.GetMovementService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.Logging
import views.html.ItemDetailsView

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class ItemDetailsController @Inject()(mcc: MessagesControllerComponents,
                                      val auth: AuthAction,
                                      val getData: DataRetrievalAction,
                                      view: ItemDetailsView,
                                      movementService: GetMovementService
                                     )(implicit val executionContext: ExecutionContext)
  extends FrontendController(mcc) with AuthActionHelper with I18nSupport with Logging {

  def onPageLoad(ern: String, arc: String, idx: Int): Action[AnyContent] =
    authorisedDataRequestAsync(ern) { implicit request =>
      movementService.getLatestMovementForLoggedInUser(ern, arc).map { movement =>
        val item = movement.items.find(_.itemUniqueReference == idx).get // `get` is safe here because the recover block will catch and log any exceptions and safely redirect
        Ok(view(item))
      }.recover {
        case e =>
          logger.warn(s"[onPageLoad][$ern][$arc] Unexpected exception thrown of type ${e.getClass.getSimpleName.stripSuffix("$")}, redirecting back to ViewMovementController.viewMovementItems")
          Redirect(routes.ViewMovementController.viewMovementItems(ern, arc))
      }
    }

}
