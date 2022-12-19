/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.emcstfefrontend.services.ViewMovementService
import uk.gov.hmrc.emcstfefrontend.views.html.ViewMovementPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.ExecutionContext

@Singleton
class ViewMovementController @Inject()(
                                      mcc: MessagesControllerComponents,
                                      service: ViewMovementService,
                                      viewMovementPage: ViewMovementPage,
                                      implicit val executionContext: ExecutionContext)
  extends FrontendController(mcc) {

  def viewMovement(): Action[AnyContent] = Action.async { implicit request =>
    service.getMovement map {
      case() => Ok(viewMovementPage())
    }
  }

}
