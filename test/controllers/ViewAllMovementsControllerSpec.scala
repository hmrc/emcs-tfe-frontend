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

package controllers

import base.SpecBase
import controllers.predicates.{FakeAuthAction, FakeDataRetrievalAction}
import fixtures.messages.EN
import fixtures.{ExciseProductCodeFixtures, MemberStatesFixtures, MovementListFixtures}
import forms.ViewAllMovementsFormProvider
import mocks.config.MockAppConfig
import mocks.connectors.{MockEmcsTfeConnector, MockGetExciseProductCodesConnector, MockGetMemberStatesConnector}
import mocks.viewmodels.MockMovementPaginationHelper
import models.MovementFilterDirectionOption._
import models.MovementSortingSelectOption.Newest
import models._
import models.requests.DataRequest
import models.response.emcsTfe.{GetMovementListItem, GetMovementListResponse}
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
import views.html.viewAllMovements.ViewAllMovementsView

import scala.concurrent.Future

class ViewAllMovementsControllerSpec extends SpecBase
  with MovementListFixtures
  with FakeAuthAction
  with MockMovementPaginationHelper
  with MockGetExciseProductCodesConnector
  with ExciseProductCodeFixtures
  with MockGetMemberStatesConnector
  with MemberStatesFixtures
  with MockEmcsTfeConnector
  with MockAppConfig {

  val movements: Seq[GetMovementListItem] = Seq.fill(10)(movement1)
  lazy val threePageMovementListResponse: GetMovementListResponse =
    GetMovementListResponse(movements, 30)

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(EN.lang))

  lazy val view = app.injector.instanceOf[ViewAllMovementsView]
  lazy val formProvider = app.injector.instanceOf[ViewAllMovementsFormProvider]

  val selectItemList = SelectItemHelper.constructSelectItems(MovementSortingSelectOption.values, None, None)

  val epcsListConnectorResult: Seq[ExciseProductCode] = Seq(beerExciseProductCode, wineExciseProductCode)
  val epcsListForView: Seq[SelectOptionModel] = MovementListSearchOptions.CHOOSE_PRODUCT_CODE +: epcsListConnectorResult

  val countryListConnectorResult: Seq[MemberState] = Seq(memberStateAT, memberStateBE)
  val countryListForView: Seq[SelectOptionModel] = MovementListSearchOptions.CHOOSE_COUNTRY +: countryListConnectorResult

  trait Test {

    lazy val controller: ViewAllMovementsController = new ViewAllMovementsController(
      mcc = app.injector.instanceOf[MessagesControllerComponents],
      getMovementListConnector = mockGetMovementListConnector,
      getExciseProductCodesConnector = mockGetExciseProductCodesConnector,
      getMemberStatesConnector = mockGetMemberStatesConnector,
      view = view,
      errorHandler = errorHandler,
      auth = FakeSuccessAuthAction,
      getData = new FakeDataRetrievalAction(Some(testMinTraderKnownFacts), Some(testMessageStatistics)),
      paginationHelper = mockMovementPaginationHelper,
      formProvider = formProvider
    )(ec)
  }

  private def buildView(searchOptions: MovementListSearchOptions,
                        movementListResponse: GetMovementListResponse,
                        form: Form[MovementListSearchOptions],
                       )(implicit request: DataRequest[_]): Html =
    view(
      form = form,
      action = routes.ViewAllMovementsController.onSubmit(testErn, searchOptions),
      ern = testErn,
      movementListResponse = movementListResponse,
      sortSelectItems = MovementSortingSelectOption.constructSelectItems(Some(Newest.toString)),
      searchSelectItems = MovementSearchSelectOption.constructSelectItems(form),
      movementStatusItems = MovementFilterStatusOption.selectItems(searchOptions.movementStatus),
      exciseProductCodeSelectItems = SelectItemHelper.constructSelectItems(epcsListForView, None, None),
      countrySelectItems = SelectItemHelper.constructSelectItems(countryListForView, None, None),
      pagination = None,
      directionFilterOption = All,
      totalMovements = movementListResponse.count,
      currentFilters = searchOptions
    )

  private def successView(searchOptions: MovementListSearchOptions,
                          movementListResponse: GetMovementListResponse = threePageMovementListResponse
                         )(implicit request: DataRequest[_]): Html =
    buildView(searchOptions, movementListResponse, formProvider().fill(searchOptions))

  private def viewWithErrors(
                              searchOptions: MovementListSearchOptions,
                              movementListResponse: GetMovementListResponse = threePageMovementListResponse,
                              form: Form[MovementListSearchOptions] = formProvider().withError(FormError("sortBy", Seq("error.required")))
                            )(implicit request: DataRequest[_]): Html =
    buildView(searchOptions, movementListResponse, form)

  "GET /" when {

    implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")
    implicit val dr: DataRequest[_] = dataRequest(fakeRequest)

    "connector call is successful" should {

      "redirect to the index 1 when current index is below the minimum" in new Test {

        val searchOptions = MovementListSearchOptions(index = 0)

        MockEmcsTfeConnector
          .getMovementList(testErn, Some(searchOptions))
          .returns(Future.successful(Right(threePageMovementListResponse)))

        MockGetExciseProductCodesConnector
          .getExciseProductCodes()
          .returns(Future.successful(Right(epcsListConnectorResult)))

        MockGetMemberStatesConnector
          .getMemberStates()
          .returns(Future.successful(Right(countryListConnectorResult)))

        val result: Future[Result] = controller.onPageLoad(testErn, searchOptions)(fakeRequest)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.ViewAllMovementsController.onPageLoad(testErn, MovementListSearchOptions(index = 1)).url)
      }

      "show the correct view and pagination with an index of 1" in new Test {

        val searchOptions = MovementListSearchOptions(index = 1)

        MockEmcsTfeConnector
          .getMovementList(testErn, Some(searchOptions))
          .returns(Future.successful(Right(threePageMovementListResponse)))

        MockGetExciseProductCodesConnector
          .getExciseProductCodes()
          .returns(Future.successful(Right(epcsListConnectorResult)))

        MockGetMemberStatesConnector
          .getMemberStates()
          .returns(Future.successful(Right(countryListConnectorResult)))

        MockMovementPaginationHelper.constructPagination(index = 1, pageCount = 3)(None)

        val result: Future[Result] = controller.onPageLoad(testErn, searchOptions)(fakeRequest)

        status(result) shouldBe Status.OK
        Html(contentAsString(result)) shouldBe successView(searchOptions)
      }

      "show the correct view and pagination with an index of 2" in new Test {

        val searchOptions = MovementListSearchOptions(index = 2)

        MockEmcsTfeConnector
          .getMovementList(testErn, Some(searchOptions))
          .returns(Future.successful(Right(threePageMovementListResponse)))

        MockGetExciseProductCodesConnector
          .getExciseProductCodes()
          .returns(Future.successful(Right(epcsListConnectorResult)))

        MockGetMemberStatesConnector
          .getMemberStates()
          .returns(Future.successful(Right(countryListConnectorResult)))

        MockMovementPaginationHelper.constructPagination(index = 2, pageCount = 3)(None)

        val result: Future[Result] = controller.onPageLoad(testErn, searchOptions)(fakeRequest)

        status(result) shouldBe Status.OK
        Html(contentAsString(result)) shouldBe successView(searchOptions)
      }

      "show the correct view and pagination with an index of 3" in new Test {

        val searchOptions = MovementListSearchOptions(index = 3)

        MockEmcsTfeConnector
          .getMovementList(testErn, Some(searchOptions))
          .returns(Future.successful(Right(threePageMovementListResponse)))

        MockGetExciseProductCodesConnector
          .getExciseProductCodes()
          .returns(Future.successful(Right(epcsListConnectorResult)))

        MockGetMemberStatesConnector
          .getMemberStates()
          .returns(Future.successful(Right(countryListConnectorResult)))

        MockMovementPaginationHelper.constructPagination(index = 3, pageCount = 3)(None)

        val result: Future[Result] = controller.onPageLoad(testErn, searchOptions)(fakeRequest)

        status(result) shouldBe Status.OK
        Html(contentAsString(result)) shouldBe successView(searchOptions)
      }

      "redirect to the index 1 when current index is above the maximum" in new Test {

        val searchOptions = MovementListSearchOptions(index = 4)

        MockEmcsTfeConnector
          .getMovementList(testErn, Some(searchOptions))
          .returns(Future.successful(Right(threePageMovementListResponse)))

        MockGetExciseProductCodesConnector
          .getExciseProductCodes()
          .returns(Future.successful(Right(epcsListConnectorResult)))

        MockGetMemberStatesConnector
          .getMemberStates()
          .returns(Future.successful(Right(countryListConnectorResult)))

        val result: Future[Result] = controller.onPageLoad(testErn, searchOptions)(fakeRequest)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.ViewAllMovementsController.onPageLoad(testErn, MovementListSearchOptions(index = 1)).url)
      }

      "show the correct view and pagination when movement count is 1 above a multiple of the pageCount" in new Test {

        val searchOptions = MovementListSearchOptions(index = 3)
        val movementListResponse: GetMovementListResponse = GetMovementListResponse(movements, 31)

        MockEmcsTfeConnector
          .getMovementList(testErn, Some(searchOptions))
          .returns(Future.successful(Right(movementListResponse)))

        MockGetExciseProductCodesConnector
          .getExciseProductCodes()
          .returns(Future.successful(Right(epcsListConnectorResult)))

        MockGetMemberStatesConnector
          .getMemberStates()
          .returns(Future.successful(Right(countryListConnectorResult)))

        MockMovementPaginationHelper.constructPagination(index = 3, pageCount = 4)(None)

        val result: Future[Result] = controller.onPageLoad(testErn, searchOptions)(fakeRequest)

        status(result) shouldBe Status.OK
        Html(contentAsString(result)) mustBe successView(searchOptions, movementListResponse)
      }

      "show the correct view and pagination when movement count is 1 below a multiple of the pageCount" in new Test {

        val searchOptions = MovementListSearchOptions(index = 3)
        val movementListResponse: GetMovementListResponse = GetMovementListResponse(movements, 39)

        MockEmcsTfeConnector
          .getMovementList(testErn, Some(searchOptions))
          .returns(Future.successful(Right(movementListResponse)))

        MockGetExciseProductCodesConnector
          .getExciseProductCodes()
          .returns(Future.successful(Right(epcsListConnectorResult)))

        MockGetMemberStatesConnector
          .getMemberStates()
          .returns(Future.successful(Right(countryListConnectorResult)))

        MockMovementPaginationHelper.constructPagination(index = 3, pageCount = 4)(None)

        val result: Future[Result] = controller.onPageLoad(testErn, searchOptions)(fakeRequest)

        status(result) shouldBe Status.OK
        Html(contentAsString(result)) shouldBe successView(searchOptions, movementListResponse)
      }

      "show the correct view when filters have been applied" in new Test {

        val searchOptions = MovementListSearchOptions(index = 1, countryOfOrigin = Some("GB"))

        MockEmcsTfeConnector
          .getMovementList(testErn, Some(searchOptions))
          .returns(Future.successful(Right(threePageMovementListResponse)))

        MockGetExciseProductCodesConnector
          .getExciseProductCodes()
          .returns(Future.successful(Right(epcsListConnectorResult)))

        MockGetMemberStatesConnector
          .getMemberStates()
          .returns(Future.successful(Right(countryListConnectorResult)))

        MockMovementPaginationHelper.constructPaginationWithSearch(searchOptions = searchOptions, pageCount = 3)(None)

        val result: Future[Result] = controller.onPageLoad(testErn, searchOptions)(fakeRequest)

        status(result) shouldBe Status.OK
        Html(contentAsString(result)) shouldBe successView(searchOptions)
      }

      "show the correct view when a search has been applied" in new Test {

        val searchOptions = MovementListSearchOptions(index = 1, countryOfOrigin = Some("GB"), searchValue = Some("search term"))

        MockEmcsTfeConnector
          .getMovementList(testErn, Some(searchOptions))
          .returns(Future.successful(Right(threePageMovementListResponse)))

        MockGetExciseProductCodesConnector
          .getExciseProductCodes()
          .returns(Future.successful(Right(epcsListConnectorResult)))

        MockGetMemberStatesConnector
          .getMemberStates()
          .returns(Future.successful(Right(countryListConnectorResult)))

        MockMovementPaginationHelper.constructPaginationWithSearch(searchOptions = searchOptions, pageCount = 3)(None)

        val result: Future[Result] = controller.onPageLoad(testErn, searchOptions)(fakeRequest)

        status(result) shouldBe Status.OK
        Html(contentAsString(result)) shouldBe successView(searchOptions)
      }
    }

    "get movement connector call is unsuccessful" when {

      "not found" should {
        "return the view" in new Test {
          MockEmcsTfeConnector
            .getMovementList(testErn, Some(MovementListSearchOptions(index = 1)))
            .returns(Future.successful(Left(NotFoundError)))

          MockGetExciseProductCodesConnector
            .getExciseProductCodes()
            .returns(Future.successful(Right(epcsListConnectorResult)))

          MockGetMemberStatesConnector
            .getMemberStates()
            .returns(Future.successful(Right(countryListConnectorResult)))

          MockMovementPaginationHelper.constructPagination(index = 1, pageCount = 1)(None)

          val result: Future[Result] = controller.onPageLoad(testErn, MovementListSearchOptions(index = 1))(fakeRequest)

          status(result) shouldBe Status.OK
        }
      }

      "any other error message" should {
        "return 500" in new Test {

          MockEmcsTfeConnector
            .getMovementList(testErn, Some(MovementListSearchOptions(index = 1)))
            .returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

          val result: Future[Result] = controller.onPageLoad(testErn, MovementListSearchOptions(index = 1))(fakeRequest)

          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          Html(contentAsString(result)) shouldBe errorHandler.internalServerErrorTemplate(fakeRequest)
        }
      }
    }

    "get EPCs connector call is unsuccessful" should {

      "return 500" in new Test {

        MockEmcsTfeConnector
          .getMovementList(testErn, Some(MovementListSearchOptions(index = 1)))
          .returns(Future.successful(Right(GetMovementListResponse(movements, 39))))

        MockGetExciseProductCodesConnector
          .getExciseProductCodes()
          .returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result: Future[Result] = controller.onPageLoad(testErn, MovementListSearchOptions(index = 1))(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        Html(contentAsString(result)) shouldBe errorHandler.internalServerErrorTemplate(fakeRequest)
      }
    }

    "get countries connector call is unsuccessful" should {

      "return 500" in new Test {

        MockEmcsTfeConnector
          .getMovementList(testErn, Some(MovementListSearchOptions(index = 1)))
          .returns(Future.successful(Right(GetMovementListResponse(movements, 39))))

        MockGetExciseProductCodesConnector
          .getExciseProductCodes()
          .returns(Future.successful(Right(epcsListConnectorResult)))

        MockGetMemberStatesConnector
          .getMemberStates()
          .returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result: Future[Result] = controller.onPageLoad(testErn, MovementListSearchOptions(index = 1))(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        Html(contentAsString(result)) shouldBe errorHandler.internalServerErrorTemplate(fakeRequest)
      }
    }
  }

  "POST /" when {

    implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("POST", "/")
    implicit val dr: DataRequest[_] = dataRequest(fakeRequest)

    "invalid data submitted" when {

      "connector call is successful" should {

        "redirect to the index 1 when current index is below the minimum" in new Test {

          val searchOptions = MovementListSearchOptions(index = 0)

          MockEmcsTfeConnector
            .getMovementList(testErn, Some(searchOptions))
            .returns(Future.successful(Right(threePageMovementListResponse)))

          MockGetExciseProductCodesConnector
            .getExciseProductCodes()
            .returns(Future.successful(Right(epcsListConnectorResult)))

          MockGetMemberStatesConnector
            .getMemberStates()
            .returns(Future.successful(Right(countryListConnectorResult)))

          val result: Future[Result] = controller.onSubmit(testErn, searchOptions)(
            fakeRequest.withFormUrlEncodedBody(("value", "invalid"))
          )

          status(result) shouldBe Status.SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.ViewAllMovementsController.onPageLoad(testErn, MovementListSearchOptions(index = 1)).url)
        }

        "show the correct view and pagination with an index of 1" when {
          "form errors contain searchValue" in new Test {

            val searchOptions = MovementListSearchOptions(index = 1, searchValue = Some("beans"))

            MockEmcsTfeConnector
              .getMovementList(testErn, Some(searchOptions))
              .returns(Future.successful(Right(threePageMovementListResponse)))

            MockGetExciseProductCodesConnector
              .getExciseProductCodes()
              .returns(Future.successful(Right(epcsListConnectorResult)))

            MockGetMemberStatesConnector
              .getMemberStates()
              .returns(Future.successful(Right(countryListConnectorResult)))

            MockMovementPaginationHelper.constructPaginationWithSearch(searchOptions, pageCount = 3)(None)

            val result: Future[Result] = controller.onSubmit(testErn, searchOptions)(
              fakeRequest.withFormUrlEncodedBody(ViewAllMovementsFormProvider.sortBy -> Newest.code, ViewAllMovementsFormProvider.searchValue -> "beans")
            )

            status(result) shouldBe Status.BAD_REQUEST
            Html(contentAsString(result)) shouldBe viewWithErrors(
              searchOptions,
              form = formProvider().fill(searchOptions).withError(FormError(ViewAllMovementsFormProvider.searchKey, ViewAllMovementsFormProvider.searchKeyRequiredMessage))
            )
          }
          "form errors contain searchKey" in new Test {

            val searchOptions = MovementListSearchOptions(index = 1, searchValue = Some("aaaaaaaaaaaaaaaaaaaaaa"), searchKey = Some(MovementSearchSelectOption.ARC))

            MockEmcsTfeConnector
              .getMovementList(testErn, Some(searchOptions))
              .returns(Future.successful(Right(threePageMovementListResponse)))

            MockGetExciseProductCodesConnector
              .getExciseProductCodes()
              .returns(Future.successful(Right(epcsListConnectorResult)))

            MockGetMemberStatesConnector
              .getMemberStates()
              .returns(Future.successful(Right(countryListConnectorResult)))

            MockMovementPaginationHelper.constructPaginationWithSearch(searchOptions, pageCount = 3)(None)

            val result: Future[Result] = controller.onSubmit(testErn, searchOptions)(
              fakeRequest.withFormUrlEncodedBody(
                ViewAllMovementsFormProvider.sortBy -> Newest.code,
                ViewAllMovementsFormProvider.searchKey -> MovementSearchSelectOption.ARC.code,
                ViewAllMovementsFormProvider.searchValue -> ("a" * (ViewAllMovementsFormProvider.ARC_MAX_LENGTH + 1))
              )
            )

            status(result) shouldBe Status.BAD_REQUEST
            Html(contentAsString(result)) shouldBe viewWithErrors(
              searchOptions,
              form = formProvider().fill(searchOptions).withError(FormError(ViewAllMovementsFormProvider.searchValue, ViewAllMovementsFormProvider.arcMaxLengthMessage))
            )
          }
          "form errors contain anything else" in new Test {

            val searchOptions = MovementListSearchOptions(index = 1)

            MockEmcsTfeConnector
              .getMovementList(testErn, Some(searchOptions))
              .returns(Future.successful(Right(threePageMovementListResponse)))

            MockGetExciseProductCodesConnector
              .getExciseProductCodes()
              .returns(Future.successful(Right(epcsListConnectorResult)))

            MockGetMemberStatesConnector
              .getMemberStates()
              .returns(Future.successful(Right(countryListConnectorResult)))

            MockMovementPaginationHelper.constructPagination(index = 1, pageCount = 3)(None)

            val result: Future[Result] = controller.onSubmit(testErn, searchOptions)(
              fakeRequest.withFormUrlEncodedBody("value" -> "invalid")
            )

            status(result) shouldBe Status.BAD_REQUEST
            Html(contentAsString(result)) shouldBe viewWithErrors(searchOptions)
          }
        }

        "show the correct view and pagination with an index of 2" in new Test {

          val searchOptions = MovementListSearchOptions(index = 2)

          MockEmcsTfeConnector
            .getMovementList(testErn, Some(searchOptions))
            .returns(Future.successful(Right(threePageMovementListResponse)))

          MockGetExciseProductCodesConnector
            .getExciseProductCodes()
            .returns(Future.successful(Right(epcsListConnectorResult)))

          MockGetMemberStatesConnector
            .getMemberStates()
            .returns(Future.successful(Right(countryListConnectorResult)))

          MockMovementPaginationHelper.constructPagination(index = 2, pageCount = 3)(None)

          val result: Future[Result] = controller.onSubmit(testErn, searchOptions)(
            fakeRequest.withFormUrlEncodedBody(("value", "invalid"))
          )

          status(result) shouldBe Status.BAD_REQUEST
          Html(contentAsString(result)) shouldBe viewWithErrors(searchOptions)
        }

        "show the correct view and pagination with an index of 3" in new Test {

          val searchOptions = MovementListSearchOptions(index = 3)

          MockEmcsTfeConnector
            .getMovementList(testErn, Some(searchOptions))
            .returns(Future.successful(Right(threePageMovementListResponse)))

          MockGetExciseProductCodesConnector
            .getExciseProductCodes()
            .returns(Future.successful(Right(epcsListConnectorResult)))

          MockGetMemberStatesConnector
            .getMemberStates()
            .returns(Future.successful(Right(countryListConnectorResult)))

          MockMovementPaginationHelper.constructPagination(index = 3, pageCount = 3)(None)

          val result: Future[Result] = controller.onSubmit(testErn, searchOptions)(
            fakeRequest.withFormUrlEncodedBody(("value", "invalid"))
          )

          status(result) shouldBe Status.BAD_REQUEST
          Html(contentAsString(result)) shouldBe viewWithErrors(searchOptions)
        }

        "redirect to the index 1 when current index is above the maximum" in new Test {

          val searchOptions = MovementListSearchOptions(index = 4)

          MockEmcsTfeConnector
            .getMovementList(testErn, Some(searchOptions))
            .returns(Future.successful(Right(threePageMovementListResponse)))

          MockGetExciseProductCodesConnector
            .getExciseProductCodes()
            .returns(Future.successful(Right(epcsListConnectorResult)))

          MockGetMemberStatesConnector
            .getMemberStates()
            .returns(Future.successful(Right(countryListConnectorResult)))

          val result: Future[Result] = controller.onSubmit(testErn, searchOptions)(
            fakeRequest.withFormUrlEncodedBody(("value", "invalid"))
          )

          status(result) shouldBe Status.SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.ViewAllMovementsController.onPageLoad(testErn, MovementListSearchOptions(index = 1)).url)
        }

        "show the correct view and pagination when movement count is 1 above a multiple of the pageCount" in new Test {

          val searchOptions = MovementListSearchOptions(index = 3)
          val movementListResponse: GetMovementListResponse = GetMovementListResponse(movements, 31)

          MockEmcsTfeConnector
            .getMovementList(testErn, Some(searchOptions))
            .returns(Future.successful(Right(movementListResponse)))

          MockGetExciseProductCodesConnector
            .getExciseProductCodes()
            .returns(Future.successful(Right(epcsListConnectorResult)))

          MockGetMemberStatesConnector
            .getMemberStates()
            .returns(Future.successful(Right(countryListConnectorResult)))

          MockMovementPaginationHelper.constructPagination(index = 3, pageCount = 4)(None)

          val result: Future[Result] = controller.onSubmit(testErn, searchOptions)(
            fakeRequest.withFormUrlEncodedBody(("value", "invalid"))
          )

          status(result) shouldBe Status.BAD_REQUEST
          Html(contentAsString(result)) shouldBe viewWithErrors(searchOptions, movementListResponse)
        }

        "show the correct view and pagination when movement count is 1 below a multiple of the pageCount" in new Test {

          val searchOptions = MovementListSearchOptions(index = 3)
          val movementListResponse: GetMovementListResponse = GetMovementListResponse(movements, 39)

          MockEmcsTfeConnector
            .getMovementList(testErn, Some(searchOptions))
            .returns(Future.successful(Right(movementListResponse)))

          MockGetExciseProductCodesConnector
            .getExciseProductCodes()
            .returns(Future.successful(Right(epcsListConnectorResult)))

          MockGetMemberStatesConnector
            .getMemberStates()
            .returns(Future.successful(Right(countryListConnectorResult)))

          MockMovementPaginationHelper.constructPagination(index = 3, pageCount = 4)(None)

          val result: Future[Result] = controller.onSubmit(testErn, searchOptions)(
            fakeRequest.withFormUrlEncodedBody(("value", "invalid"))
          )

          status(result) shouldBe Status.BAD_REQUEST
          Html(contentAsString(result)) shouldBe viewWithErrors(searchOptions, movementListResponse)
        }
      }

      "get movement connector call is unsuccessful" when {

        "not found" should {
          "return the view" in new Test {
            val searchOptions = MovementListSearchOptions(index = 1)

            MockEmcsTfeConnector
              .getMovementList(testErn, Some(searchOptions))
              .returns(Future.successful(Left(NotFoundError)))

            MockGetExciseProductCodesConnector
              .getExciseProductCodes()
              .returns(Future.successful(Right(epcsListConnectorResult)))

            MockGetMemberStatesConnector
              .getMemberStates()
              .returns(Future.successful(Right(countryListConnectorResult)))

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
              .getMovementList(testErn, Some(MovementListSearchOptions(index = 1)))
              .returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

            val result: Future[Result] = controller.onSubmit(testErn, MovementListSearchOptions(index = 1))(fakeRequest)

            status(result) shouldBe Status.INTERNAL_SERVER_ERROR
            Html(contentAsString(result)) shouldBe errorHandler.internalServerErrorTemplate(fakeRequest)
          }
        }
      }

      "get EPCs connector call is unsuccessful" should {

        "return 500" in new Test {

          MockEmcsTfeConnector
            .getMovementList(testErn, Some(MovementListSearchOptions(index = 1)))
            .returns(Future.successful(Right(GetMovementListResponse(movements, 39))))

          MockGetExciseProductCodesConnector
            .getExciseProductCodes()
            .returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

          val result: Future[Result] = controller.onSubmit(testErn, MovementListSearchOptions(index = 1))(fakeRequest)

          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          Html(contentAsString(result)) shouldBe errorHandler.internalServerErrorTemplate(fakeRequest)
        }
      }

      "get countries connector call is unsuccessful" should {

        "return 500" in new Test {

          MockEmcsTfeConnector
            .getMovementList(testErn, Some(MovementListSearchOptions(index = 1)))
            .returns(Future.successful(Right(GetMovementListResponse(movements, 39))))

          MockGetExciseProductCodesConnector
            .getExciseProductCodes()
            .returns(Future.successful(Right(epcsListConnectorResult)))

          MockGetMemberStatesConnector
            .getMemberStates()
            .returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

          val result: Future[Result] = controller.onSubmit(testErn, MovementListSearchOptions(index = 1))(fakeRequest)

          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          Html(contentAsString(result)) shouldBe errorHandler.internalServerErrorTemplate(fakeRequest)
        }
      }
    }

    "valid data is submitted" when {

      "redirect to ViewAllMovementsController.onPageLoad" in new Test {

        val searchOptions = MovementListSearchOptions(index = 1)

        val result: Future[Result] = controller.onSubmit(testErn, searchOptions)(
          fakeRequest.withFormUrlEncodedBody(ViewAllMovementsFormProvider.sortBy -> Newest.code)
        )

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.ViewAllMovementsController.onPageLoad(testErn, MovementListSearchOptions(
          index = 1,
          sortBy = Newest
        )).url)
      }
    }
  }
}
