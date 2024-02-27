package controllers.messages

import base.SpecBase
import config.SessionKeys
import controllers.messages.routes.ViewAllMessagesController
import controllers.predicates.{FakeAuthAction, FakeBetaAllowListAction, FakeDataRetrievalAction}
import fixtures.messages.EN
import fixtures.{GetSubmissionFailureMessageFixtures, MessagesFixtures}
import forms.DeleteMessageFormProvider
import mocks.services.{MockDeleteMessageService, MockGetMessagesService, MockGetMovementService}
import models.messages.{MessageCache, MessagesSearchOptions}
import models.requests.DataRequest
import models.response.emcsTfe.messages.DeleteMessageResponse
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, convertToStringShouldWrapper}
import pages.{ViewAllMessagesPage, ViewMessagePage}
import play.api.data.Form
import play.api.http.Status
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, RequestHeader, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, redirectLocation, status}
import play.twirl.api.Html
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels.helpers.messages.DeleteMessageHelper
import views.html.messages.DeleteMessage

import java.time.Instant
import scala.concurrent.Future

class DeleteMessageControllerSpec extends SpecBase
  with MessagesFixtures
  with FakeAuthAction
  with MockGetMessagesService
  with MockGetMovementService
  with MockDeleteMessageService
  with GetSubmissionFailureMessageFixtures {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(EN.lang))

  lazy val formProvider = new DeleteMessageFormProvider

  lazy val view = app.injector.instanceOf[DeleteMessage]

  lazy val controller: DeleteMessageController = new DeleteMessageController(
    mcc = app.injector.instanceOf[MessagesControllerComponents],
    auth = FakeSuccessAuthAction,
    getData = new FakeDataRetrievalAction(testMinTraderKnownFacts, testMessageStatistics),
    betaAllowList = new FakeBetaAllowListAction,
    getMessagesService = mockGetMessagesService,
    deleteMessageService = mockDeleteMessagesService,
    formProvider = formProvider,
    view = view,
    deleteMessageHelper = new DeleteMessageHelper(),
    errorHandler = errorHandler,
  )

  val testMessageId = 1234

  val testMessageFromCache = MessageCache(
    ern = testErn,
    message = message1.copy(uniqueMessageIdentifier = testMessageId),
    errorMessage = Some(GetSubmissionFailureMessageResponseFixtures.getSubmissionFailureMessageResponseModel),
    lastUpdated = Instant.now
  )

  "GET" when {
    implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] =
      FakeRequest("GET", "/")
        .withSession(SessionKeys.FROM_PAGE -> ViewMessagePage.toString)

    implicit val dr: DataRequest[_] = dataRequest(fakeRequest)

    "MockGetMessagesService returns a message" should {
      "render the view" in {
        MockGetMessagesService
          .getMessage(testErn, testMessageId)
          .returns(Future.successful(Some(testMessageFromCache)))

        val result: Future[Result] = controller.onPageLoad(testErn, testMessageId)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsString(result) shouldBe
          view(
            testMessageFromCache.message,
            formProvider(),
            ViewAllMessagesController.onPageLoad(testErn, MessagesSearchOptions()).url,
            ViewMessagePage
          ).toString()
      }
    }

    "MockGetMessagesService does not return a message" should {
      "redirect back to the messages inbox" in {
        MockGetMessagesService
          .getMessage(testErn, testMessageId)
          .returns(Future.successful(None))

        val result: Future[Result] = controller.onPageLoad(testErn, testMessageId)(fakeRequest)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.ViewAllMessagesController.onPageLoad(testErn, MessagesSearchOptions()).url)
      }
    }


  }

  "POST" when {

    "the user does not select a form option" in {
      val formKeyValue = "value" -> ""

      val fakeRequest =
        FakeRequest("POST", "/")
          .withFormUrlEncodedBody(formKeyValue)
          .withSession(SessionKeys.FROM_PAGE -> ViewAllMessagesPage.toString)

      implicit val dr: DataRequest[_] = dataRequest(fakeRequest)

      MockGetMessagesService
        .getMessage(testErn, testMessageId)
        .returns(Future.successful(Some(testMessageFromCache)))

      val boundForm = formProvider().bind(Map(formKeyValue))

      val result: Future[Result] = controller.onSubmit(testErn, testMessageId)(fakeRequest)

      status(result) shouldBe Status.OK
      contentAsString(result) shouldBe
        view(
          testMessageFromCache.message,
          boundForm,
          ViewAllMessagesController.onPageLoad(testErn, MessagesSearchOptions()).url,
          ViewAllMessagesPage
        ).toString()
    }

    "the user has arrived from the ViewAllMessagesPage, and selects 'No, return to all messages'" in {
      val fakeRequest =
        FakeRequest("POST", "/")
          .withFormUrlEncodedBody(data = "value" -> "false")
          .withSession(SessionKeys.FROM_PAGE -> ViewAllMessagesPage.toString)

      val result: Future[Result] = controller.onSubmit(testErn, testMessageId)(fakeRequest)

      status(result) shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.ViewAllMessagesController.onPageLoad(testErn, MessagesSearchOptions()).url)
    }

    "the user has arrived from the ViewMessagesPage, and selects 'No, return to message'" in {
      val fakeRequest =
        FakeRequest("POST", "/")
          .withFormUrlEncodedBody(data = "value" -> "false")
          .withSession(SessionKeys.FROM_PAGE -> ViewMessagePage.toString)

      val result: Future[Result] = controller.onSubmit(testErn, testMessageId)(fakeRequest)

      status(result) shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.ViewMessageController.onPageLoad(testErn, testMessageId).url)
    }

    "the user selects 'Yes, delete this message', and the message is deleted" in {
      MockDeleteMessagesService
        .deleteMessage(testErn, testMessageId)
        .returns(Future.successful(DeleteMessageResponse(recordsAffected = 1)))

      val fakeRequest =
        FakeRequest("POST", "/")
          .withFormUrlEncodedBody(data = "value" -> "true")
          .withSession(SessionKeys.FROM_PAGE -> ViewMessagePage.toString)

      val result: Future[Result] = controller.onSubmit(testErn, testMessageId)(fakeRequest)

      status(result) shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.ViewAllMessagesController.onPageLoad(testErn, MessagesSearchOptions()).url)
      //      await(result).session.get(SessionKeys.DELETED_MESSAGE_TITLE) TODO test RequestHeader?
    }

    "the user selects 'Yes, delete this message', and the message is not deleted" in {
      MockDeleteMessagesService
        .deleteMessage(testErn, testMessageId)
        .returns(Future.successful(DeleteMessageResponse(recordsAffected = 0)))

      val fakeRequest =
        FakeRequest("POST", "/")
          .withFormUrlEncodedBody(data = "value" -> "true")
          .withSession(SessionKeys.FROM_PAGE -> ViewMessagePage.toString)

      val result: Future[Result] = controller.onSubmit(testErn, testMessageId)(fakeRequest)

      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      // Html(contentAsString(result)) shouldBe errorHandler.internalServerErrorTemplate(fakeRequest) TODO the title is different?
    }


  }

}
