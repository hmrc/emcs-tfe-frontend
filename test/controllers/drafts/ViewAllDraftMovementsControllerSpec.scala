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
import config.AppConfig
import controllers.predicates.{FakeAuthAction, FakeDataRetrievalAction}
import fixtures.messages.EN
import fixtures.{DraftMovementsFixtures, ExciseProductCodeFixtures, MemberStatesFixtures}
import forms.{ViewAllDraftMovementsFormProvider, ViewAllMovementsFormProvider}
import mocks.config.MockAppConfig
import mocks.connectors.{MockEmcsTfeConnector, MockGetExciseProductCodesConnector, MockGetMemberStatesConnector}
import mocks.viewmodels.MockDraftMovementsPaginationHelper
import models.MovementSortingSelectOption.Newest
import models._
import models.draftMovements.{DraftMovementSortingSelectOption, GetDraftMovementsSearchOptions}
import models.requests.DataRequest
import models.response.UnexpectedDownstreamResponseError
import models.response.emcsTfe.draftMovement.{DraftMovement, GetDraftMovementsResponse}
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
  with MockAppConfig {

  val movements: Seq[DraftMovement] = Seq.fill(10)(draftMovementModelMax)
  lazy val threePageMovementListResponse: GetDraftMovementsResponse =
    GetDraftMovementsResponse(30, movements)

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(EN.lang))

  implicit val config: AppConfig = appConfig

  lazy val view = app.injector.instanceOf[ViewAllDraftMovementsView]
  lazy val formProvider = app.injector.instanceOf[ViewAllDraftMovementsFormProvider]

  val selectItemList = SelectItemHelper.constructSelectItems(MovementSortingSelectOption.values, None, None)

  val epcsListConnectorResult: Seq[ExciseProductCode] = Seq(beerExciseProductCode, wineExciseProductCode)
  val epcsListForView: Seq[SelectOptionModel] = GetDraftMovementsSearchOptions.CHOOSE_PRODUCT_CODE +: epcsListConnectorResult

  trait Test {

    lazy val controller: ViewAllDraftMovementsController = new ViewAllDraftMovementsController(
      mcc = app.injector.instanceOf[MessagesControllerComponents],
      getExciseProductCodesConnector = mockGetExciseProductCodesConnector,
      getDraftMovementsConnector = mockGetDraftMovementsConnector,
      view = view,
      errorHandler = errorHandler,
      auth = FakeSuccessAuthAction,
      getData = new FakeDataRetrievalAction(Some(testMinTraderKnownFacts), Some(testMessageStatistics)),
      paginationHelper = mockDraftMovementsPaginationHelper,
      formProvider = formProvider
    )
  }

  private def buildView(searchOptions: GetDraftMovementsSearchOptions,
                        form: Form[GetDraftMovementsSearchOptions],
                        numberOfMovements: Int
                       )(implicit request: DataRequest[_]): Html =
    view(
      form = form,
      action = routes.ViewAllDraftMovementsController.onSubmit(testErn, searchOptions),
      ern = testErn,
      movements = movements,
      sortSelectItems = DraftMovementSortingSelectOption.constructSelectItems(Some(DraftMovementSortingSelectOption.Newest.toString)),
      exciseItems = SelectItemHelper.constructSelectItems(epcsListForView, None, None),
      pagination = None,
      totalMovements = numberOfMovements,
      currentFilters = searchOptions
    )

  private def successView(searchOptions: GetDraftMovementsSearchOptions,
                          numberOfMovements: Int,
                          form: Form[GetDraftMovementsSearchOptions] = formProvider(),
                         )(implicit request: DataRequest[_]): Html =
    buildView(
      searchOptions = searchOptions,
      form = form,
      numberOfMovements = numberOfMovements
    )

  private def viewWithErrors(searchOptions: GetDraftMovementsSearchOptions,
                             numberOfMovements: Int,
                             form: Form[GetDraftMovementsSearchOptions] = formProvider().withError(FormError("sortBy", Seq("error.required")))
                            )(implicit request: DataRequest[_]): Html =
    buildView(searchOptions, form, numberOfMovements)

  "GET /" when {

    implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")
    implicit val dr: DataRequest[_] = dataRequest(fakeRequest)

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
        Html(contentAsString(result)) shouldBe successView(searchOptions, threePageMovementListResponse.count)
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
        Html(contentAsString(result)) shouldBe successView(searchOptions, threePageMovementListResponse.count)
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
        Html(contentAsString(result)) shouldBe successView(searchOptions, threePageMovementListResponse.count)
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
        Html(contentAsString(result)) shouldBe successView(searchOptions, numberOfMovements = 31)
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
        Html(contentAsString(result)) shouldBe successView(searchOptions, numberOfMovements = 39)
      }

      "show the correct view with the correct form values present based on the search options" in new Test {

        val searchOptions = GetDraftMovementsSearchOptions(index = 3, draftHasErrors = Some(true))

        MockEmcsTfeConnector
          .getDraftMovements(testErn, Some(searchOptions))
          .returns(Future.successful(Right(GetDraftMovementsResponse(39, movements))))

        MockGetExciseProductCodesConnector
          .getExciseProductCodes()
          .returns(Future.successful(Right(epcsListConnectorResult)))

        MockMovementPaginationHelper.constructPagination(searchOptions, pageCount = 4)(None)

        val result: Future[Result] = controller.onPageLoad(testErn, searchOptions)(fakeRequest)

        status(result) shouldBe Status.OK
        Html(contentAsString(result)) shouldBe successView(searchOptions, numberOfMovements = 39, formProvider().fill(searchOptions))
      }
    }

    "show the correct view with the correct form values present when search value has been entered" in new Test {

      val searchOptions = GetDraftMovementsSearchOptions(index = 3, draftHasErrors = Some(true), searchValue = Some("search term"))

      MockEmcsTfeConnector
        .getDraftMovements(testErn, Some(searchOptions))
        .returns(Future.successful(Right(GetDraftMovementsResponse(39, movements))))

      MockGetExciseProductCodesConnector
        .getExciseProductCodes()
        .returns(Future.successful(Right(epcsListConnectorResult)))

      MockMovementPaginationHelper.constructPagination(searchOptions, pageCount = 4)(None)

      val result: Future[Result] = controller.onPageLoad(testErn, searchOptions)(fakeRequest)

      status(result) shouldBe Status.OK
      Html(contentAsString(result)) shouldBe successView(
        searchOptions = searchOptions,
        numberOfMovements = 39,
        form = formProvider().fill(searchOptions)
      )
    }

    "get movement connector call is unsuccessful" when {

      "return 500" in new Test {

        MockEmcsTfeConnector
          .getDraftMovements(testErn, Some(GetDraftMovementsSearchOptions(index = 1)))
          .returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result: Future[Result] = controller.onPageLoad(testErn, GetDraftMovementsSearchOptions(index = 1))(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsString(result) shouldBe await(errorHandler.internalServerErrorTemplate(fakeRequest).map(_.toString))
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
        contentAsString(result) shouldBe await(errorHandler.internalServerErrorTemplate(fakeRequest).map(_.toString))
      }
    }
  }

  "POST /" when {

    implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("POST", "/")
    implicit val dr: DataRequest[_] = dataRequest(fakeRequest)

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
          Html(contentAsString(result)) shouldBe viewWithErrors(searchOptions, threePageMovementListResponse.count)
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
          Html(contentAsString(result)) shouldBe viewWithErrors(searchOptions, threePageMovementListResponse.count)
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
          Html(contentAsString(result)) shouldBe viewWithErrors(searchOptions, threePageMovementListResponse.count)
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
          Html(contentAsString(result)) shouldBe viewWithErrors(searchOptions, numberOfMovements = 31)
        }

        "show the correct view when some form fields are invalid" in new Test {

          val searchOptions = GetDraftMovementsSearchOptions(index = 3)

          MockEmcsTfeConnector
            .getDraftMovements(testErn, Some(searchOptions))
            .returns(Future.successful(Right(GetDraftMovementsResponse(31, movements))))

          MockGetExciseProductCodesConnector
            .getExciseProductCodes()
            .returns(Future.successful(Right(epcsListConnectorResult)))

          MockMovementPaginationHelper.constructPagination(index = 3, pageCount = 4)(None)

          val result: Future[Result] = controller.onSubmit(testErn, searchOptions)(
            fakeRequest.withFormUrlEncodedBody(Seq(
              s"${ViewAllDraftMovementsFormProvider.dateOfDispatchFrom}.day" -> "invalid",
              s"${ViewAllDraftMovementsFormProvider.dateOfDispatchFrom}.month" -> "2"
            ): _*)
          )

          val boundForm = formProvider().bind(Map(
            s"${ViewAllDraftMovementsFormProvider.dateOfDispatchFrom}.day" -> "invalid",
            s"${ViewAllDraftMovementsFormProvider.dateOfDispatchFrom}.month" -> "2"
          ))

          status(result) shouldBe Status.BAD_REQUEST
          Html(contentAsString(result)) shouldBe viewWithErrors(searchOptions, numberOfMovements = 31, form = boundForm)
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
          Html(contentAsString(result)) shouldBe viewWithErrors(searchOptions, numberOfMovements = 39)
        }
      }

      "get movement connector call is unsuccessful" when {

        "return 500" in new Test {

          MockEmcsTfeConnector
            .getDraftMovements(testErn, Some(GetDraftMovementsSearchOptions(index = 1)))
            .returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

          val result: Future[Result] = controller.onSubmit(testErn, GetDraftMovementsSearchOptions(index = 1))(fakeRequest)

          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          contentAsString(result) shouldBe await(errorHandler.internalServerErrorTemplate(fakeRequest).map(_.toString))
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
          contentAsString(result) shouldBe await(errorHandler.internalServerErrorTemplate(fakeRequest).map(_.toString))
        }
      }
    }

    "valid data is submitted" when {

      "redirect to ViewAllDraftMovementsController.onPageLoad" in new Test {

        val searchOptions = GetDraftMovementsSearchOptions(index = 1)

        val result: Future[Result] = controller.onSubmit(testErn, searchOptions)(
          fakeRequest.withFormUrlEncodedBody(ViewAllMovementsFormProvider.sortBy -> Newest.code)
        )

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.ViewAllDraftMovementsController.onPageLoad(testErn, GetDraftMovementsSearchOptions(
          index = 1,
          sortBy = DraftMovementSortingSelectOption.Newest
        )).url)
      }
    }
  }
}
