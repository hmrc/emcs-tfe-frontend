/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.support

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.{Application, Environment, Mode}

trait IntegrationBaseSpec extends UnitSpec with WireMockHelper with GuiceOneServerPerSuite
  with BeforeAndAfterEach with BeforeAndAfterAll {

  val mockHost: String = WireMockHelper.host
  val mockPort: String = WireMockHelper.wireMockPort.toString

  lazy val client: WSClient = app.injector.instanceOf[WSClient]

  def servicesConfig: Map[String, String] = Map(
    "microservice.services.reference-data.host" -> mockHost,
    "microservice.services.reference-data.port" -> mockPort,
    "microservice.services.emcs-tfe.host" -> mockHost,
    "microservice.services.emcs-tfe.port" -> mockPort,
    "auditing.consumer.baseUri.host" -> mockHost,
    "auditing.consumer.baseUri.port" -> mockPort,
    "feature-switch.enable-reference-data-stub-source" -> "true"
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

  def buildRequest(path: String): WSRequest = client.url(s"http://localhost:$port/emcs-tfe$path").withFollowRedirects(false)

  def document(response: WSResponse): JsValue = Json.parse(response.body)
}
