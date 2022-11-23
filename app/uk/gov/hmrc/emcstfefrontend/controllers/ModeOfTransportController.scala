/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.emcstfefrontend.services.ModeOfTransportService
import uk.gov.hmrc.emcstfefrontend.views.html.{ErrorTemplate, ModeOfTransportPage}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.ExecutionContext

@Singleton
class ModeOfTransportController @Inject()(
                                      mcc: MessagesControllerComponents,
                                      service: ModeOfTransportService,
                                      modeOfTransportPage: ModeOfTransportPage,
                                      errorPage: ErrorTemplate,
                                      implicit val executionContext: ExecutionContext)
  extends FrontendController(mcc) {

  def modeOfTransport(): Action[AnyContent] = Action.async { implicit request =>
    val response = service.getMessage map {
      case (referenceDataResponse) => Ok(modeOfTransportPage(referenceDataResponse))
    }

    response.leftMap(value => InternalServerError(errorPage("Something went wrong!", "Oh no!", value))).merge
  }

  def onSubmit: Action[AnyContent] = Action {
    Redirect(uk.gov.hmrc.emcstfefrontend.controllers.routes.ModeOfTransportController.modeOfTransport)
  }

}
