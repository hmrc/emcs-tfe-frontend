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

package support

import base.SpecBase
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.http.HeaderNames
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.{Application, Environment, Mode}

import scala.concurrent.ExecutionContext

trait IntegrationBaseSpec extends SessionCookieBaker with SpecBase with WireMockHelper with GuiceOneServerPerSuite
  with BeforeAndAfterEach with BeforeAndAfterAll {

  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  lazy val client: WSClient = app.injector.instanceOf[WSClient]

  def servicesConfig: Map[String, _] = Map(
    "microservice.services.auth.port" -> WireMockHelper.wireMockPort,
    "microservice.services.emcs-tfe.port" -> WireMockHelper.wireMockPort,
    "microservice.services.emcs-tfe-reference-data.port" -> WireMockHelper.wireMockPort,
    "auditing.consumer.baseUri.port" -> WireMockHelper.wireMockPort,
    "feature-switch.enable-reference-data-stub-source" -> "true",
    "features.stub-get-trader-known-facts" -> "false"
  )

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Dev))
    .configure(servicesConfig)
    .build()

  override def beforeAll(): Unit = {
    super.beforeAll()
    startWireMock()
  }

  override def afterAll(): Unit = {
    stopWireMock()
    super.afterAll()
  }

  def buildRequest(path: String, additionalCookieData: Map[String, String] = Map()): WSRequest = client
    .url(s"http://localhost:$port/emcs/account$path")
    .withHttpHeaders(HeaderNames.COOKIE -> bakeSessionCookie(additionalCookieData))
    .withFollowRedirects(false)

  def document(response: WSResponse): JsValue = Json.parse(response.body)
}
