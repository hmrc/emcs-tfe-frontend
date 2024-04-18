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

package controllers.drafts

import base.SpecBase
import controllers.predicates.{BetaAllowListActionImpl, FakeAuthAction, FakeDataRetrievalAction}
import fixtures.messages.EN
import fixtures.{DraftMovementsFixtures, ExciseProductCodeFixtures, MemberStatesFixtures}
import forms.{ViewAllDraftMovementsFormProvider, ViewAllMovementsFormProvider}
import mocks.config.MockAppConfig
import mocks.connectors.{MockBetaAllowListConnector, MockEmcsTfeConnector, MockGetExciseProductCodesConnector, MockGetMemberStatesConnector}
import mocks.viewmodels.MockDraftMovementsPaginationHelper
import models.MovementSortingSelectOption.Newest
import models._
import models.draftMovements.{DraftMovementSortingSelectOption, GetDraftMovementsSearchOptions}
import models.requests.DataRequest
import models.response.emcsTfe.draftMovement.{DraftMovement, GetDraftMovementsResponse}
import models.response.{NotFoundError, UnexpectedDownstreamResponseError}
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, convertToStringShouldWrapper}
import play.api.data.{Form, FormError}
import play.api.http.Status
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels.helpers.SelectItemHelper
import views.html.viewAllDrafts.ViewAllDraftMovementsView

import scala.concurrent.Future

class ViewAllDraftMovementsControllerSpec extends SpecBase
  with DraftMovementsFixtures
  with FakeAuthAction
  with MockDraftMovementsPaginationHelper
  with MockGetExciseProductCodesConnector
  with ExciseProductCodeFixtures
  with MockGetMemberStatesConnector
  with MemberStatesFixtures
  with MockEmcsTfeConnector
  with MockBetaAllowListConnector
  with MockAppConfig {

  val movements: Seq[DraftMovement] = Seq.fill(10)(draftMovementModelMax)
  lazy val threePageMovementListResponse: GetDraftMovementsResponse =
    GetDraftMovementsResponse(30, movements)

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(EN.lang))

  lazy val view = app.injector.instanceOf[ViewAllDraftMovementsView]
  lazy val formProvider = app.injector.instanceOf[ViewAllDraftMovementsFormProvider]

  val selectItemList = SelectItemHelper.constructSelectItems(MovementSortingSelectOption.values, None, None)

  val epcsListConnectorResult: Seq[ExciseProductCode] = Seq(beerExciseProductCode, wineExciseProductCode)
  val epcsListForView: Seq[SelectOptionModel] = GetDraftMovementsSearchOptions.CHOOSE_PRODUCT_CODE +: epcsListConnectorResult

  class Test(navHubEnabled: Boolean = true, searchMovementsEnabled: Boolean = true) {

    lazy val betaAllowListAction = new BetaAllowListActionImpl(
      betaAllowListConnector = mockBetaAllowListConnector,
      errorHandler = errorHandler,
      config = mockAppConfig
    )

    lazy val controller: ViewAllDraftMovementsController = new ViewAllDraftMovementsController(
      mcc = app.injector.instanceOf[MessagesControllerComponents],
      getExciseProductCodesConnector = mockGetExciseProductCodesConnector,
      getDraftMovementsConnector = mockGetDraftMovementsConnector,
      view = view,
      errorHandler = errorHandler,
      auth = FakeSuccessAuthAction,
      getData = new FakeDataRetrievalAction(testMinTraderKnownFacts, testMessageStatistics),
      betaAllowList = betaAllowListAction,
      paginationHelper = mockDraftMovementsPaginationHelper,
      formProvider = formProvider
    )(ec, appConfig)

    MockedAppConfig.betaAllowListCheckingEnabled.repeat(2).returns(true)
    MockBetaAllowListConnector.check(testErn, "tfeNavHub").returns(Future.successful(Right(navHubEnabled)))
    MockBetaAllowListConnector.check(testErn, "tfeSearchMovements").returns(Future.successful(Right(searchMovementsEnabled)))
  }

  private def buildView(searchOptions: GetDraftMovementsSearchOptions,
                        form: Form[GetDraftMovementsSearchOptions])(implicit request: DataRequest[_]): Html =
    view(
      form = form,
      action = routes.ViewAllDraftMovementsController.onSubmit(testErn, searchOptions),
      ern = testErn,
      movements = movements,
      sortSelectItems = DraftMovementSortingSelectOption.constructSelectItems(Some(DraftMovementSortingSelectOption.Newest.toString)),
      exciseItems = SelectItemHelper.constructSelectItems(epcsListForView, None, None),
      pagination = None
    )

  private def successView(searchOptions: GetDraftMovementsSearchOptions)(implicit request: DataRequest[_]): Html =
    buildView(searchOptions, formProvider())

  private def viewWithErrors(searchOptions: GetDraftMovementsSearchOptions)(implicit request: DataRequest[_]): Html =
    buildView(searchOptions, formProvider().withError(FormError("sortBy", Seq("error.required"))))

  "GET /" when {

    implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")
    implicit val dr: DataRequest[_] = dataRequest(fakeRequest)

    "user is on the private beta list" should {

      "connector call is successful" should {

        "redirect to the index 1 when current index is below the minimum" in new Test {

          val searchOptions = GetDraftMovementsSearchOptions(index = 0)

          MockEmcsTfeConnector
            .getDraftMovements(testErn, Some(searchOptions))
            .returns(Future.successful(Right(threePageMovementListResponse)))

          MockGetExciseProductCodesConnector
            .getExciseProductCodes()
            .returns(Future.successful(Right(epcsListConnectorResult)))

          val result: Future[Result] = controller.onPageLoad(testErn, searchOptions)(fakeRequest)

          status(result) shouldBe Status.SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.ViewAllDraftMovementsController.onPageLoad(testErn, GetDraftMovementsSearchOptions(index = 1)).url)
        }

        "show the correct view and pagination with an index of 1" in new Test {

          val searchOptions = GetDraftMovementsSearchOptions(index = 1)

          MockEmcsTfeConnector
            .getDraftMovements(testErn, Some(searchOptions))
            .returns(Future.successful(Right(threePageMovementListResponse)))

          MockGetExciseProductCodesConnector
            .getExciseProductCodes()
            .returns(Future.successful(Right(epcsListConnectorResult)))

          MockMovementPaginationHelper.constructPagination(index = 1, pageCount = 3)(None)

          val result: Future[Result] = controller.onPageLoad(testErn, searchOptions)(fakeRequest)

          status(result) shouldBe Status.OK
          Html(contentAsString(result)) shouldBe successView(searchOptions)
        }

        "show the correct view and pagination with an index of 2" in new Test {

          val searchOptions = GetDraftMovementsSearchOptions(index = 2)

          MockEmcsTfeConnector
            .getDraftMovements(testErn, Some(searchOptions))
            .returns(Future.successful(Right(threePageMovementListResponse)))

          MockGetExciseProductCodesConnector
            .getExciseProductCodes()
            .returns(Future.successful(Right(epcsListConnectorResult)))

          MockMovementPaginationHelper.constructPagination(index = 2, pageCount = 3)(None)

          val result: Future[Result] = controller.onPageLoad(testErn, searchOptions)(fakeRequest)

          status(result) shouldBe Status.OK
          Html(contentAsString(result)) shouldBe successView(searchOptions)
        }

        "show the correct view and pagination with an index of 3" in new Test {

          val searchOptions = GetDraftMovementsSearchOptions(index = 3)

          MockEmcsTfeConnector
            .getDraftMovements(testErn, Some(searchOptions))
            .returns(Future.successful(Right(threePageMovementListResponse)))

          MockGetExciseProductCodesConnector
            .getExciseProductCodes()
            .returns(Future.successful(Right(epcsListConnectorResult)))

          MockMovementPaginationHelper.constructPagination(index = 3, pageCount = 3)(None)

          val result: Future[Result] = controller.onPageLoad(testErn, searchOptions)(fakeRequest)

          status(result) shouldBe Status.OK
          Html(contentAsString(result)) shouldBe successView(searchOptions)
        }

        "redirect to the index 1 when current index is above the maximum" in new Test {

          val searchOptions = GetDraftMovementsSearchOptions(index = 4)

          MockEmcsTfeConnector
            .getDraftMovements(testErn, Some(searchOptions))
            .returns(Future.successful(Right(threePageMovementListResponse)))

          MockGetExciseProductCodesConnector
            .getExciseProductCodes()
            .returns(Future.successful(Right(epcsListConnectorResult)))

          val result: Future[Result] = controller.onPageLoad(testErn, searchOptions)(fakeRequest)

          status(result) shouldBe Status.SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.ViewAllDraftMovementsController.onPageLoad(testErn, GetDraftMovementsSearchOptions(index = 1)).url)
        }

        "show the correct view and pagination when movement count is 1 above a multiple of the pageCount" in new Test {

          val searchOptions = GetDraftMovementsSearchOptions(index = 3)

          MockEmcsTfeConnector
            .getDraftMovements(testErn, Some(searchOptions))
            .returns(Future.successful(Right(GetDraftMovementsResponse(31, movements))))

          MockGetExciseProductCodesConnector
            .getExciseProductCodes()
            .returns(Future.successful(Right(epcsListConnectorResult)))

          MockMovementPaginationHelper.constructPagination(index = 3, pageCount = 4)(None)

          val result: Future[Result] = controller.onPageLoad(testErn, searchOptions)(fakeRequest)

          status(result) shouldBe Status.OK
          Html(contentAsString(result)) shouldBe successView(searchOptions)
        }

        "show the correct view and pagination when movement count is 1 below a multiple of the pageCount" in new Test {

          val searchOptions = GetDraftMovementsSearchOptions(index = 3)

          MockEmcsTfeConnector
            .getDraftMovements(testErn, Some(searchOptions))
            .returns(Future.successful(Right(GetDraftMovementsResponse(39, movements))))

          MockGetExciseProductCodesConnector
            .getExciseProductCodes()
            .returns(Future.successful(Right(epcsListConnectorResult)))

          MockMovementPaginationHelper.constructPagination(index = 3, pageCount = 4)(None)

          val result: Future[Result] = controller.onPageLoad(testErn, searchOptions)(fakeRequest)

          status(result) shouldBe Status.OK
          Html(contentAsString(result)) shouldBe successView(searchOptions)
        }
      }

      "get movement connector call is unsuccessful" when {

        "not found" should {

          "return the view" in new Test {

            MockEmcsTfeConnector
              .getDraftMovements(testErn, Some(GetDraftMovementsSearchOptions(index = 1)))
              .returns(Future.successful(Left(NotFoundError)))

            MockGetExciseProductCodesConnector
              .getExciseProductCodes()
              .returns(Future.successful(Right(epcsListConnectorResult)))

            MockMovementPaginationHelper.constructPagination(index = 1, pageCount = 1)(None)

            val result: Future[Result] = controller.onPageLoad(testErn, GetDraftMovementsSearchOptions(index = 1))(fakeRequest)

            status(result) shouldBe Status.OK
          }
        }

        "any other error message" should {

          "return 500" in new Test {

            MockEmcsTfeConnector
              .getDraftMovements(testErn, Some(GetDraftMovementsSearchOptions(index = 1)))
              .returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

            val result: Future[Result] = controller.onPageLoad(testErn, GetDraftMovementsSearchOptions(index = 1))(fakeRequest)

            status(result) shouldBe Status.INTERNAL_SERVER_ERROR
            Html(contentAsString(result)) shouldBe errorHandler.internalServerErrorTemplate(fakeRequest)
          }
        }
      }

      "get EPCs connector call is unsuccessful" should {

        "return 500" in new Test {

          MockEmcsTfeConnector
            .getDraftMovements(testErn, Some(GetDraftMovementsSearchOptions(index = 1)))
            .returns(Future.successful(Right(GetDraftMovementsResponse(39, movements))))

          MockGetExciseProductCodesConnector
            .getExciseProductCodes()
            .returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

          val result: Future[Result] = controller.onPageLoad(testErn, GetDraftMovementsSearchOptions(index = 1))(fakeRequest)

          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          Html(contentAsString(result)) shouldBe errorHandler.internalServerErrorTemplate(fakeRequest)
        }
      }
    }

    "user is NOT on the private beta list" in new Test(searchMovementsEnabled = false) {

      val result: Future[Result] = controller.onPageLoad(testErn, GetDraftMovementsSearchOptions())(fakeRequest)

      status(result) shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some("http://localhost:8080/emcs/trader/GBWKTestErn/movements")
    }
  }

  "POST /" when {

    implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("POST", "/")
    implicit val dr: DataRequest[_] = dataRequest(fakeRequest)

    "user is on the private beta list" should {

      "invalid data submitted" when {

        "connector call is successful" should {

          "redirect to the index 1 when current index is below the minimum" in new Test {

            val searchOptions = GetDraftMovementsSearchOptions(index = 0)

            MockEmcsTfeConnector
              .getDraftMovements(testErn, Some(searchOptions))
              .returns(Future.successful(Right(threePageMovementListResponse)))

            MockGetExciseProductCodesConnector
              .getExciseProductCodes()
              .returns(Future.successful(Right(epcsListConnectorResult)))

            val result: Future[Result] = controller.onSubmit(testErn, searchOptions)(
              fakeRequest.withFormUrlEncodedBody(("value", "invalid"))
            )

            status(result) shouldBe Status.SEE_OTHER
            redirectLocation(result) shouldBe Some(routes.ViewAllDraftMovementsController.onPageLoad(testErn, GetDraftMovementsSearchOptions(index = 1)).url)
          }

          "show the correct view and pagination with an index of 1" in new Test {

            val searchOptions = GetDraftMovementsSearchOptions(index = 1)

            MockEmcsTfeConnector
              .getDraftMovements(testErn, Some(searchOptions))
              .returns(Future.successful(Right(threePageMovementListResponse)))

            MockGetExciseProductCodesConnector
              .getExciseProductCodes()
              .returns(Future.successful(Right(epcsListConnectorResult)))

            MockMovementPaginationHelper.constructPagination(index = 1, pageCount = 3)(None)

            val result: Future[Result] = controller.onSubmit(testErn, searchOptions)(
              fakeRequest.withFormUrlEncodedBody(("value", "invalid"))
            )

            status(result) shouldBe Status.BAD_REQUEST
            Html(contentAsString(result)) shouldBe viewWithErrors(searchOptions)
          }

          "show the correct view and pagination with an index of 2" in new Test {

            val searchOptions = GetDraftMovementsSearchOptions(index = 2)

            MockEmcsTfeConnector
              .getDraftMovements(testErn, Some(searchOptions))
              .returns(Future.successful(Right(threePageMovementListResponse)))

            MockGetExciseProductCodesConnector
              .getExciseProductCodes()
              .returns(Future.successful(Right(epcsListConnectorResult)))

            MockMovementPaginationHelper.constructPagination(index = 2, pageCount = 3)(None)

            val result: Future[Result] = controller.onSubmit(testErn, searchOptions)(
              fakeRequest.withFormUrlEncodedBody(("value", "invalid"))
            )

            status(result) shouldBe Status.BAD_REQUEST
            Html(contentAsString(result)) shouldBe viewWithErrors(searchOptions)
          }

          "show the correct view and pagination with an index of 3" in new Test {

            val searchOptions = GetDraftMovementsSearchOptions(index = 3)

            MockEmcsTfeConnector
              .getDraftMovements(testErn, Some(searchOptions))
              .returns(Future.successful(Right(threePageMovementListResponse)))

            MockGetExciseProductCodesConnector
              .getExciseProductCodes()
              .returns(Future.successful(Right(epcsListConnectorResult)))

            MockMovementPaginationHelper.constructPagination(index = 3, pageCount = 3)(None)

            val result: Future[Result] = controller.onSubmit(testErn, searchOptions)(
              fakeRequest.withFormUrlEncodedBody(("value", "invalid"))
            )

            status(result) shouldBe Status.BAD_REQUEST
            Html(contentAsString(result)) shouldBe viewWithErrors(searchOptions)
          }

          "redirect to the index 1 when current index is above the maximum" in new Test {

            val searchOptions = GetDraftMovementsSearchOptions(index = 4)

            MockEmcsTfeConnector
              .getDraftMovements(testErn, Some(searchOptions))
              .returns(Future.successful(Right(threePageMovementListResponse)))

            MockGetExciseProductCodesConnector
              .getExciseProductCodes()
              .returns(Future.successful(Right(epcsListConnectorResult)))

            val result: Future[Result] = controller.onSubmit(testErn, searchOptions)(
              fakeRequest.withFormUrlEncodedBody(("value", "invalid"))
            )

            status(result) shouldBe Status.SEE_OTHER
            redirectLocation(result) shouldBe Some(routes.ViewAllDraftMovementsController.onPageLoad(testErn, GetDraftMovementsSearchOptions(index = 1)).url)
          }

          "show the correct view and pagination when movement count is 1 above a multiple of the pageCount" in new Test {

            val searchOptions = GetDraftMovementsSearchOptions(index = 3)

            MockEmcsTfeConnector
              .getDraftMovements(testErn, Some(searchOptions))
              .returns(Future.successful(Right(GetDraftMovementsResponse(31, movements))))

            MockGetExciseProductCodesConnector
              .getExciseProductCodes()
              .returns(Future.successful(Right(epcsListConnectorResult)))

            MockMovementPaginationHelper.constructPagination(index = 3, pageCount = 4)(None)

            val result: Future[Result] = controller.onSubmit(testErn, searchOptions)(
              fakeRequest.withFormUrlEncodedBody(("value", "invalid"))
            )

            status(result) shouldBe Status.BAD_REQUEST
            Html(contentAsString(result)) shouldBe viewWithErrors(searchOptions)
          }

          "show the correct view and pagination when movement count is 1 below a multiple of the pageCount" in new Test {

            val searchOptions = GetDraftMovementsSearchOptions(index = 3)

            MockEmcsTfeConnector
              .getDraftMovements(testErn, Some(searchOptions))
              .returns(Future.successful(Right(GetDraftMovementsResponse(39, movements))))

            MockGetExciseProductCodesConnector
              .getExciseProductCodes()
              .returns(Future.successful(Right(epcsListConnectorResult)))

            MockMovementPaginationHelper.constructPagination(index = 3, pageCount = 4)(None)

            val result: Future[Result] = controller.onSubmit(testErn, searchOptions)(
              fakeRequest.withFormUrlEncodedBody(("value", "invalid"))
            )

            status(result) shouldBe Status.BAD_REQUEST
            Html(contentAsString(result)) shouldBe viewWithErrors(searchOptions)
          }
        }

        "get movement connector call is unsuccessful" when {

          "not found" should {

            "return the view" in new Test {

              val searchOptions = GetDraftMovementsSearchOptions(index = 1)

              MockEmcsTfeConnector
                .getDraftMovements(testErn, Some(searchOptions))
                .returns(Future.successful(Left(NotFoundError)))

              MockGetExciseProductCodesConnector
                .getExciseProductCodes()
                .returns(Future.successful(Right(epcsListConnectorResult)))

              MockMovementPaginationHelper.constructPagination(index = 1, pageCount = 1)(None)

              val result: Future[Result] = controller.onSubmit(testErn, searchOptions)(
                fakeRequest.withFormUrlEncodedBody(("value", "invalid"))
              )

              status(result) shouldBe Status.BAD_REQUEST
            }
          }

          "any other error message" should {

            "return 500" in new Test {

              MockEmcsTfeConnector
                .getDraftMovements(testErn, Some(GetDraftMovementsSearchOptions(index = 1)))
                .returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

              val result: Future[Result] = controller.onSubmit(testErn, GetDraftMovementsSearchOptions(index = 1))(fakeRequest)

              status(result) shouldBe Status.INTERNAL_SERVER_ERROR
              Html(contentAsString(result)) shouldBe errorHandler.internalServerErrorTemplate(fakeRequest)
            }
          }
        }

        "get EPCs connector call is unsuccessful" should {

          "return 500" in new Test {

            MockEmcsTfeConnector
              .getDraftMovements(testErn, Some(GetDraftMovementsSearchOptions(index = 1)))
              .returns(Future.successful(Right(GetDraftMovementsResponse(39, movements))))

            MockGetExciseProductCodesConnector
              .getExciseProductCodes()
              .returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

            val result: Future[Result] = controller.onSubmit(testErn, GetDraftMovementsSearchOptions(index = 1))(fakeRequest)

            status(result) shouldBe Status.INTERNAL_SERVER_ERROR
            Html(contentAsString(result)) shouldBe errorHandler.internalServerErrorTemplate(fakeRequest)
          }
        }
      }

      "valid data is submitted" when {

        "redirect to ViewAllDraftMovementsController.onPageLoad" in new Test {

          val searchOptions = GetDraftMovementsSearchOptions(index = 1)

          val result: Future[Result] = controller.onSubmit(testErn, searchOptions)(
            fakeRequest.withFormUrlEncodedBody(ViewAllMovementsFormProvider.sortByKey -> Newest.code)
          )

          status(result) shouldBe Status.SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.ViewAllDraftMovementsController.onPageLoad(testErn, GetDraftMovementsSearchOptions(
            index = 1,
            sortBy = DraftMovementSortingSelectOption.Newest
          )).url)
        }
      }
    }

    "user is NOT on the private beta list" in new Test(searchMovementsEnabled = false) {

      val result: Future[Result] = controller.onSubmit(testErn, GetDraftMovementsSearchOptions())(fakeRequest)

      status(result) shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some("http://localhost:8080/emcs/trader/GBWKTestErn/movements")
    }
  }

}
