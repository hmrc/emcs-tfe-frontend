/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.services


import javax.inject.{Inject, Singleton}
import play.api.mvc.Results.Redirect
import uk.gov.hmrc.emcstfefrontend.controllers.routes

import scala.concurrent.Future

@Singleton
class ViewMovementService @Inject()() {
  def getMovement: Future[Unit] = {
    Future.successful(Redirect(routes.ViewMovementController.viewMovement))
  }
}
