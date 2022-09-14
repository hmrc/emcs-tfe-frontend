/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.connectors

import uk.gov.hmrc.http.{HttpReads, HttpResponse}

trait RawResponseReads {

  implicit val httpReads: HttpReads[HttpResponse] = (method: String, url: String, response: HttpResponse) => response

}
