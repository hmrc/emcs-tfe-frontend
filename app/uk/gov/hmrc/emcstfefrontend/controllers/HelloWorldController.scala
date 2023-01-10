/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.emcstfefrontend.services.HelloWorldService
import uk.gov.hmrc.emcstfefrontend.views.html.{ErrorTemplate, HelloWorldPage}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.ExecutionContext

@Singleton
class HelloWorldController @Inject()(
                                      mcc: MessagesControllerComponents,
                                      service: HelloWorldService,
                                      helloWorldPage: HelloWorldPage,
                                      errorPage: ErrorTemplate,
                                      implicit val executionContext: ExecutionContext)
  extends FrontendController(mcc) {

  def helloWorld(): Action[AnyContent] = Action.async { implicit request =>
    val response = service.getMessage map {
      case (referenceDataResponse, emcsTfeResponse) => Ok(helloWorldPage(referenceDataResponse, emcsTfeResponse))
    }

    response.leftMap(value => InternalServerError(errorPage("Something went wrong!", "Oh no!", value))).merge
  }

}
