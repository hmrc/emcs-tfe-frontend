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
import config.ErrorHandler
import controllers.predicates.FakeAuthAction
import fixtures.MovementListFixtures
import fixtures.messages.EN
import forms.ViewAllMovementsFormProvider
import mocks.connectors.MockEmcsTfeConnector
import mocks.viewmodels.MockMovementPaginationHelper
import models.MovementSearchSelectOption.ARC
import models.MovementSortingSelectOption.{ArcAscending, Newest}
import models.response.UnexpectedDownstreamResponseError
import models.response.emcsTfe.{GetMovementListItem, GetMovementListResponse}
import models.{MovementListSearchOptions, MovementSearchSelectOption, MovementSortingSelectOption}
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, convertToStringShouldWrapper}
import play.api.http.Status
import play.api.i18n.MessagesApi
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels.helpers.SelectItemHelper
import views.html.viewAllMovements.ViewAllMovements

import scala.concurrent.{ExecutionContext, Future}

class ViewAllMovementsControllerSpec extends SpecBase with MovementListFixtures with FakeAuthAction with MockMovementPaginationHelper with MockEmcsTfeConnector {

  val movements: Seq[GetMovementListItem] = Seq.fill(10)(movement1)
  lazy val threePageMovementListResponse: GetMovementListResponse =
    GetMovementListResponse(movements, 30)

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit val messages = app.injector.instanceOf[MessagesApi].preferred(Seq(EN.lang))

  lazy val view = app.injector.instanceOf[ViewAllMovements]
  lazy val formProvider = app.injector.instanceOf[ViewAllMovementsFormProvider]

  lazy val controller: ViewAllMovementsController = new ViewAllMovementsController(
    mcc = app.injector.instanceOf[MessagesControllerComponents],
    connector = mockGetMovementListConnector,
    view = view,
    errorHandler = errorHandler,
    authAction = FakeSuccessAuthAction,
    paginationHelper = mockMovementPaginationHelper,
    formProvider = formProvider
  )

  "GET /" when {

    implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")

    "connector call is successful" should {

      "redirect to the index 1 when current index is below the minimum" in {

        val searchOptions = MovementListSearchOptions(index = 0)

        MockEmcsTfeConnector
          .getMovementList(testErn, Some(searchOptions))
          .returns(Future.successful(Right(threePageMovementListResponse)))

        val result: Future[Result] = controller.onPageLoad(testErn, searchOptions)(fakeRequest)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.ViewAllMovementsController.onPageLoad(testErn, MovementListSearchOptions(index = 1)).url)
      }

      "show the correct view and pagination with an index of 1" in {

        val searchOptions = MovementListSearchOptions(index = 1)

        MockEmcsTfeConnector
          .getMovementList(testErn, Some(searchOptions))
          .returns(Future.successful(Right(threePageMovementListResponse)))

        MockMovementPaginationHelper.constructPagination(index = 1, pageCount = 3)(None)

        val result: Future[Result] = controller.onPageLoad(testErn, searchOptions)(fakeRequest)

        status(result) shouldBe Status.OK
        Html(contentAsString(result)) shouldBe view(
          form = formProvider(),
          action = routes.ViewAllMovementsController.onSubmit(testErn, searchOptions),
          ern = testErn,
          movements = movements,
          sortSelectItems = MovementSortingSelectOption.constructSelectItems(Some(ArcAscending.toString)),
          searchSelectItems = MovementSearchSelectOption.constructSelectItems(None),
          pagination = None
        )
      }

      "show the correct view and pagination with an index of 2" in {

        val searchOptions = MovementListSearchOptions(index = 2)

        MockEmcsTfeConnector
          .getMovementList(testErn, Some(searchOptions))
          .returns(Future.successful(Right(threePageMovementListResponse)))

        MockMovementPaginationHelper.constructPagination(index = 2, pageCount = 3)(None)

        val result: Future[Result] = controller.onPageLoad(testErn, searchOptions)(fakeRequest)

        status(result) shouldBe Status.OK
        Html(contentAsString(result)) shouldBe view(
          formProvider(),
          routes.ViewAllMovementsController.onSubmit(testErn, searchOptions),
          ern = testErn,
          movements = movements,
          sortSelectItems = MovementSortingSelectOption.constructSelectItems(Some(ArcAscending.toString)),
          searchSelectItems = MovementSearchSelectOption.constructSelectItems(None),
          pagination = None
        )
      }

      "show the correct view and pagination with an index of 3" in {

        val searchOptions = MovementListSearchOptions(index = 3)

        MockEmcsTfeConnector
          .getMovementList(testErn, Some(searchOptions))
          .returns(Future.successful(Right(threePageMovementListResponse)))

        MockMovementPaginationHelper.constructPagination(index = 3, pageCount = 3)(None)

        val result: Future[Result] = controller.onPageLoad(testErn, searchOptions)(fakeRequest)

        status(result) shouldBe Status.OK
        Html(contentAsString(result)) shouldBe view(
          formProvider(),
          routes.ViewAllMovementsController.onSubmit(testErn, searchOptions),
          ern = testErn,
          movements = movements,
          sortSelectItems = MovementSortingSelectOption.constructSelectItems(Some(ArcAscending.toString)),
          searchSelectItems = MovementSearchSelectOption.constructSelectItems(None),
          pagination = None
        )
      }

      "redirect to the index 1 when current index is above the maximum" in {

        val searchOptions = MovementListSearchOptions(index = 4)

        MockEmcsTfeConnector
          .getMovementList(testErn, Some(searchOptions))
          .returns(Future.successful(Right(threePageMovementListResponse)))

        val result: Future[Result] = controller.onPageLoad(testErn, searchOptions)(fakeRequest)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.ViewAllMovementsController.onPageLoad(testErn, MovementListSearchOptions(index = 1)).url)
      }

      "show the correct view and pagination when movement count is 1 above a multiple of the pageCount" in {

        val searchOptions = MovementListSearchOptions(index = 3)

        MockEmcsTfeConnector
          .getMovementList(testErn, Some(searchOptions))
          .returns(Future.successful(Right(GetMovementListResponse(movements, 31))))

        MockMovementPaginationHelper.constructPagination(index = 3, pageCount = 4)(None)

        val result: Future[Result] = controller.onPageLoad(testErn, searchOptions)(fakeRequest)

        status(result) shouldBe Status.OK
        Html(contentAsString(result)) shouldBe view(
          formProvider(),
          routes.ViewAllMovementsController.onSubmit(testErn, searchOptions),
          ern = testErn,
          movements = movements,
          sortSelectItems = MovementSortingSelectOption.constructSelectItems(Some(ArcAscending.toString)),
          searchSelectItems = MovementSearchSelectOption.constructSelectItems(None),
          pagination = None
        )
      }

      "show the correct view and pagination when movement count is 1 below a multiple of the pageCount" in {

        val searchOptions = MovementListSearchOptions(index = 3)

        MockEmcsTfeConnector
          .getMovementList(testErn, Some(searchOptions))
          .returns(Future.successful(Right(GetMovementListResponse(movements, 39))))

        MockMovementPaginationHelper.constructPagination(index = 3, pageCount = 4)(None)

        val result: Future[Result] = controller.onPageLoad(testErn, searchOptions)(fakeRequest)

        status(result) shouldBe Status.OK
        Html(contentAsString(result)) shouldBe view(
          formProvider(),
          routes.ViewAllMovementsController.onSubmit(testErn, searchOptions),
          ern = testErn,
          movements = movements,
          sortSelectItems = MovementSortingSelectOption.constructSelectItems(Some(ArcAscending.toString)),
          searchSelectItems = MovementSearchSelectOption.constructSelectItems(None),
          pagination = None
        )
      }
    }

    "connector call is unsuccessful" should {

      "return 500" in {

        MockEmcsTfeConnector
          .getMovementList(testErn, Some(MovementListSearchOptions(index = 1)))
          .returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result: Future[Result] = controller.onPageLoad(testErn, MovementListSearchOptions(index = 1))(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        Html(contentAsString(result)) shouldBe errorHandler.internalServerErrorTemplate
      }
    }
  }

  "POST /" when {

    implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("POST", "/")

    "invalid data submitted" when {

      "connector call is successful" should {

        "redirect to the index 1 when current index is below the minimum" in {

          val searchOptions = MovementListSearchOptions(index = 0)

          MockEmcsTfeConnector
            .getMovementList(testErn, Some(searchOptions))
            .returns(Future.successful(Right(threePageMovementListResponse)))

          val result: Future[Result] = controller.onSubmit(testErn, searchOptions)(
            fakeRequest.withFormUrlEncodedBody(("value", "invalid"))
          )

          status(result) shouldBe Status.SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.ViewAllMovementsController.onPageLoad(testErn, MovementListSearchOptions(index = 1)).url)
        }

        "show the correct view and pagination with an index of 1" in {

          val searchOptions = MovementListSearchOptions(index = 1)

          MockEmcsTfeConnector
            .getMovementList(testErn, Some(searchOptions))
            .returns(Future.successful(Right(threePageMovementListResponse)))

          MockMovementPaginationHelper.constructPagination(index = 1, pageCount = 3)(None)

          val result: Future[Result] = controller.onSubmit(testErn, searchOptions)(
            fakeRequest.withFormUrlEncodedBody(("value", "invalid"))
          )

          status(result) shouldBe Status.BAD_REQUEST
          Html(contentAsString(result)) shouldBe view(
            form = formProvider(),
            action = routes.ViewAllMovementsController.onSubmit(testErn, searchOptions),
            ern = testErn,
            movements = movements,
            sortSelectItems = MovementSortingSelectOption.constructSelectItems(Some(ArcAscending.toString)),
            searchSelectItems = MovementSearchSelectOption.constructSelectItems(None),
            pagination = None
          )
        }

        "show the correct view and pagination with an index of 2" in {

          val searchOptions = MovementListSearchOptions(index = 2)

          MockEmcsTfeConnector
            .getMovementList(testErn, Some(searchOptions))
            .returns(Future.successful(Right(threePageMovementListResponse)))

          MockMovementPaginationHelper.constructPagination(index = 2, pageCount = 3)(None)

          val result: Future[Result] = controller.onSubmit(testErn, searchOptions)(
            fakeRequest.withFormUrlEncodedBody(("value", "invalid"))
          )

          status(result) shouldBe Status.BAD_REQUEST
          Html(contentAsString(result)) shouldBe view(
            formProvider(),
            routes.ViewAllMovementsController.onSubmit(testErn, searchOptions),
            ern = testErn,
            movements = movements,
            sortSelectItems = MovementSortingSelectOption.constructSelectItems(Some(ArcAscending.toString)),
            searchSelectItems = MovementSearchSelectOption.constructSelectItems(None),
            pagination = None
          )
        }

        "show the correct view and pagination with an index of 3" in {

          val searchOptions = MovementListSearchOptions(index = 3)

          MockEmcsTfeConnector
            .getMovementList(testErn, Some(searchOptions))
            .returns(Future.successful(Right(threePageMovementListResponse)))

          MockMovementPaginationHelper.constructPagination(index = 3, pageCount = 3)(None)

          val result: Future[Result] = controller.onSubmit(testErn, searchOptions)(
            fakeRequest.withFormUrlEncodedBody(("value", "invalid"))
          )

          status(result) shouldBe Status.BAD_REQUEST
          Html(contentAsString(result)) shouldBe view(
            formProvider(),
            routes.ViewAllMovementsController.onSubmit(testErn, searchOptions),
            ern = testErn,
            movements = movements,
            sortSelectItems = MovementSortingSelectOption.constructSelectItems(Some(ArcAscending.toString)),
            searchSelectItems = MovementSearchSelectOption.constructSelectItems(None),
            pagination = None
          )
        }

        "redirect to the index 1 when current index is above the maximum" in {

          val searchOptions = MovementListSearchOptions(index = 4)

          MockEmcsTfeConnector
            .getMovementList(testErn, Some(searchOptions))
            .returns(Future.successful(Right(threePageMovementListResponse)))

          val result: Future[Result] = controller.onSubmit(testErn, searchOptions)(
            fakeRequest.withFormUrlEncodedBody(("value", "invalid"))
          )

          status(result) shouldBe Status.SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.ViewAllMovementsController.onPageLoad(testErn, MovementListSearchOptions(index = 1)).url)
        }

        "show the correct view and pagination when movement count is 1 above a multiple of the pageCount" in {

          val searchOptions = MovementListSearchOptions(index = 3)

          MockEmcsTfeConnector
            .getMovementList(testErn, Some(searchOptions))
            .returns(Future.successful(Right(GetMovementListResponse(movements, 31))))

          MockMovementPaginationHelper.constructPagination(index = 3, pageCount = 4)(None)

          val result: Future[Result] = controller.onSubmit(testErn, searchOptions)(
            fakeRequest.withFormUrlEncodedBody(("value", "invalid"))
          )

          status(result) shouldBe Status.BAD_REQUEST
          Html(contentAsString(result)) shouldBe view(
            formProvider(),
            routes.ViewAllMovementsController.onSubmit(testErn, searchOptions),
            ern = testErn,
            movements = movements,
            sortSelectItems = MovementSortingSelectOption.constructSelectItems(Some(ArcAscending.toString)),
            searchSelectItems = MovementSearchSelectOption.constructSelectItems(None),
            pagination = None
          )
        }

        "show the correct view and pagination when movement count is 1 below a multiple of the pageCount" in {

          val searchOptions = MovementListSearchOptions(index = 3)

          MockEmcsTfeConnector
            .getMovementList(testErn, Some(searchOptions))
            .returns(Future.successful(Right(GetMovementListResponse(movements, 39))))

          MockMovementPaginationHelper.constructPagination(index = 3, pageCount = 4)(None)

          val result: Future[Result] = controller.onSubmit(testErn, searchOptions)(
            fakeRequest.withFormUrlEncodedBody(("value", "invalid"))
          )

          status(result) shouldBe Status.BAD_REQUEST
          Html(contentAsString(result)) shouldBe view(
            formProvider(),
            routes.ViewAllMovementsController.onSubmit(testErn, searchOptions),
            ern = testErn,
            movements = movements,
            sortSelectItems = MovementSortingSelectOption.constructSelectItems(Some(ArcAscending.toString)),
            searchSelectItems = MovementSearchSelectOption.constructSelectItems(None),
            pagination = None
          )
        }
      }

      "connector call is unsuccessful" should {

        "return 500" in {

          MockEmcsTfeConnector
            .getMovementList(testErn, Some(MovementListSearchOptions(index = 1)))
            .returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

          val result: Future[Result] = controller.onSubmit(testErn, MovementListSearchOptions(index = 1))(fakeRequest)

          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          Html(contentAsString(result)) shouldBe errorHandler.internalServerErrorTemplate
        }
      }
    }

    "valid data is submitted" when {

      "redirect to ViewAllMovementsController.onPageLoad" in {

        val searchOptions = MovementListSearchOptions(index = 1)

        val result: Future[Result] = controller.onSubmit(testErn, searchOptions)(
          fakeRequest.withFormUrlEncodedBody(ViewAllMovementsFormProvider.sortByKey -> Newest.code)
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
