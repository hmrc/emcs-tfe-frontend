/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.config

import javax.inject.{Inject, Singleton}
import play.api.i18n.MessagesApi
import play.api.mvc.Request
import play.twirl.api.Html
import uk.gov.hmrc.emcstfefrontend.views.html.ErrorTemplate
import uk.gov.hmrc.play.bootstrap.frontend.http.FrontendErrorHandler

@Singleton
class ErrorHandler @Inject()(errorTemplate: ErrorTemplate, val messagesApi: MessagesApi)
    extends FrontendErrorHandler {

  override def standardErrorTemplate(pageTitle: String, heading: String, message: String)(implicit request: Request[_]): Html =
    errorTemplate(pageTitle, heading, message)
}
