/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.controllers

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.emcstfefrontend.services.HelloWorldService
import uk.gov.hmrc.emcstfefrontend.views.html.{ErrorTemplate, HelloWorldPage}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
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
    service.getMessage map {
      case Left(value) => InternalServerError(errorPage("Something went wrong!", "Oh no!", value))
      case Right(value) => Ok(helloWorldPage(value))
    }

  }

}
