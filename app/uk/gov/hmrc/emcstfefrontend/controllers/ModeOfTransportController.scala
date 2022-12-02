/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.emcstfefrontend.models.response.{ModeOfTransportErrorResponse, ModeOfTransportListModel}
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

    service.getOtherDataReferenceList map {
      case referenceDataResponse: ModeOfTransportListModel =>
        Ok(modeOfTransportPage(referenceDataResponse.orderedOptions))
      case error: ModeOfTransportErrorResponse  => InternalServerError(errorPage("Something went wrong!", "Oh no!", error.reason))
    }
  }

  def onSubmit: Action[AnyContent] = Action {
    Redirect(uk.gov.hmrc.emcstfefrontend.controllers.routes.ModeOfTransportController.modeOfTransport)
  }

}
