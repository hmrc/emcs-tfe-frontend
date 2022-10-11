/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.connectors

import play.api.http.{HeaderNames, MimeTypes, Status}
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.emcstfefrontend.mocks.config.MockAppConfig
import uk.gov.hmrc.emcstfefrontend.mocks.connectors.MockHttpClient
import uk.gov.hmrc.emcstfefrontend.models.response.EmcsTfeResponse
import uk.gov.hmrc.emcstfefrontend.support.UnitSpec
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

class EmcsTfeConnectorSpec extends UnitSpec with Status with MimeTypes with HeaderNames with MockAppConfig with MockHttpClient {

  trait Test {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

    val connector = new EmcsTfeConnector(mockHttpClient, mockAppConfig)

    val baseUrl: String = "http://test-BaseUrl"
    MockedAppConfig.emcsTfeBaseUrl.returns(baseUrl)
  }

  "getMessage" should {
    "return a Right" when {
      "downstream call is successful" in new Test {
        val response: HttpResponse = HttpResponse(status = Status.OK, json = Json.toJson(EmcsTfeResponse("test message")), headers = Map.empty)

        MockHttpClient.get(s"$baseUrl/hello-world").returns(Future.successful(response))

        await(connector.getMessage()) shouldBe Right(EmcsTfeResponse("test message"))
      }
    }
    "return a Left" when {
      "downstream call is successful but doesn't match expected JSON" in new Test {
        case class TestModel(field: String)
        object TestModel {
          implicit val format: OFormat[TestModel] = Json.format
        }
        val response: HttpResponse = HttpResponse(status = Status.OK, json = Json.toJson(TestModel("test message")), headers = Map.empty)

        MockHttpClient.get(s"$baseUrl/hello-world").returns(Future.successful(response))

        await(connector.getMessage()) shouldBe Left("JSON validation error")
      }
      "downstream call is unsuccessful" in new Test {
        val response = HttpResponse(status = Status.INTERNAL_SERVER_ERROR, json = Json.toJson(EmcsTfeResponse("test message")), headers = Map.empty)

        MockHttpClient.get(s"$baseUrl/hello-world").returns(Future.successful(response))

        await(connector.getMessage()) shouldBe Left("Unexpected downstream response status")
      }
    }
  }
}
