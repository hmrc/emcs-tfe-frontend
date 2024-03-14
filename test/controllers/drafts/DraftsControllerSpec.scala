package controllers.drafts

import base.SpecBase
import config.AppConfig
import controllers.predicates.{BetaAllowListActionImpl, FakeAuthAction, FakeDataRetrievalAction}
import mocks.config.MockAppConfig
import mocks.connectors.MockBetaAllowListConnector
import models.requests.DataRequest
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, convertToStringShouldWrapper}
import play.api.http.Status
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, redirectLocation, status}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class DraftsControllerSpec extends SpecBase with FakeAuthAction with MockFactory with MockBetaAllowListConnector with MockAppConfig {

  class Test(navHubEnabled: Boolean = true, draftsEnabled: Boolean = true) {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
    implicit val fakeRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest("GET", "/"))
    implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(fakeRequest)

    lazy val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

    lazy val betaAllowListAction = new BetaAllowListActionImpl(
      betaAllowListConnector = mockBetaAllowListConnector,
      errorHandler = errorHandler,
      config = mockAppConfig
    )

    val controller: DraftsController = new DraftsController(
      app.injector.instanceOf[MessagesControllerComponents],
      FakeSuccessAuthAction,
      new FakeDataRetrievalAction(testMinTraderKnownFacts, testMessageStatistics),
      betaAllowListAction
    )(ec, appConfig)

    MockedAppConfig.betaAllowListCheckingEnabled.repeat(2).returns(true)
    MockBetaAllowListConnector.check(testErn, "navHub").returns(Future.successful(Right(navHubEnabled)))
    MockBetaAllowListConnector.check(testErn, "tfeDrafts").returns(Future.successful(Right(draftsEnabled)))
  }

  "GET /trader/:exciseRegistrationNumber/drafts" when {

    "user is on the private beta list" should {

      "return under construction" in new Test {
        val result: Future[Result] = controller.onPageLoad(testErn)(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some("/emcs/account/test-only/construction")
      }
    }

    "user is NOT on the private beta list" should {

      "redirect to legacy at a glance page" in new Test(draftsEnabled = false) {
        val result: Future[Result] = controller.onPageLoad(testErn)(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some("http://localhost:8080/emcs/trader/GBWKTestErn/movement/drafts")
      }
    }

  }

}
