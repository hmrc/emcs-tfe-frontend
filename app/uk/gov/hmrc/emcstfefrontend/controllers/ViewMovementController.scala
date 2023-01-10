/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.emcstfefrontend.views.html.ViewMovementPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ViewMovementController @Inject()(
                                      mcc: MessagesControllerComponents,
                                      viewMovementPage: ViewMovementPage,
                                      implicit val executionContext: ExecutionContext)
  extends FrontendController(mcc) {

  def viewMovement(): Action[AnyContent] = Action.async {
    implicit request => {
      Future.successful(Ok(viewMovementPage()))
    }
  }
}
