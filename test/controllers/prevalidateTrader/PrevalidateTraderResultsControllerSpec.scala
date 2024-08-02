/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.prevalidateTrader

import base.SpecBase
import config.AppConfig
import controllers.predicates.{BetaAllowListActionImpl, FakeAuthAction, FakeDataRetrievalAction, PrevalidateTraderDataRetrievalAction}
import fixtures.{ExciseProductCodeFixtures, ItemFixtures, PrevalidateTraderFixtures}
import mocks.config.MockAppConfig
import mocks.connectors.MockBetaAllowListConnector
import mocks.services.{MockGetExciseProductCodesService, MockPrevalidateTraderService, MockPrevalidateUserAnswersService}
import models.prevalidate.{EntityGroup, PrevalidateTraderModel}
import models.requests.UserAnswersRequest
import models.response.emcsTfe.prevalidateTrader.{ExciseTraderValidationResponse, PreValidateTraderApiResponse, ProductError, ValidateProductAuthorisationResponse}
import models.{ExciseProductCode, Index, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakePrevalidateNavigator
import pages.prevalidateTrader.{PrevalidateConsigneeTraderIdentificationPage, PrevalidateEPCPage}
import play.api.i18n.Messages
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.prevalidateTrader.PrevalidateTraderResultsView

import scala.concurrent.Future

class PrevalidateTraderResultsControllerSpec extends SpecBase
  with FakeAuthAction
  with MockPrevalidateUserAnswersService
  with ItemFixtures
  with ExciseProductCodeFixtures
  with MockPrevalidateTraderService
  with MockGetExciseProductCodesService
  with PrevalidateTraderFixtures
  with MockBetaAllowListConnector
  with MockAppConfig {

  lazy val view = app.injector.instanceOf[PrevalidateTraderResultsView]
  lazy val controllerRoute: String = routes.PrevalidateTraderResultsController.onPageLoad(testErn).url
  def exciseProductPageRoute(idx: Index = 0): Call = routes.PrevalidateExciseProductCodeController.onPageLoad(testErn, idx, NormalMode)
  lazy val addToListPageRoute: Call = routes.PrevalidateAddToListController.onPageLoad(testErn)
  implicit val config: AppConfig = appConfig

  lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, controllerRoute)

  val sampleEPCs: Seq[ExciseProductCode] = Seq(beerExciseProductCode, wineExciseProductCode, tobaccoExciseProductCode)

  val ernToCheck = "GBWK002281023"
  val entityGroupToCheck = EntityGroup.UKTrader

  class Setup(val userAnswers: UserAnswers = emptyUserAnswers, getEPCs: Boolean = true, preValidateEnabled: Boolean = true) {

    implicit val userAnswerReq: UserAnswersRequest[AnyContentAsEmpty.type] = userAnswersRequest(request, userAnswers)
    implicit val msgs: Messages = messages(request)

    MockUserAnswersService.get(userAnswers.ern).returns(Future.successful(Some(userAnswers))).anyNumberOfTimes()

    lazy val betaAllowListAction = new BetaAllowListActionImpl(
      betaAllowListConnector = mockBetaAllowListConnector,
      errorHandler = errorHandler,
      config = mockAppConfig
    )

    if(getEPCs) MockGetExciseProductCodesService.getExciseProductCodes().returns(Future.successful(sampleEPCs))

    lazy val controller = new PrevalidateTraderResultsController(
      messagesApi = messagesApi,
      userAnswersService = mockUserAnswersService,
      betaAllowList = betaAllowListAction,
      navigator = new FakePrevalidateNavigator(testOnwardRoute),
      auth = FakeSuccessAuthAction,
      getData = new FakeDataRetrievalAction(Some(testMinTraderKnownFacts), Some(testMessageStatistics)),
      prevalidateTraderService = mockPrevalidateTraderService,
      requireData = new PrevalidateTraderDataRetrievalAction(mockUserAnswersService),
      controllerComponents = messagesControllerComponents,
      getExciseProductCodesService = mockGetExciseProductCodesService,
      view = view
    )

    MockedAppConfig.betaAllowListCheckingEnabled.repeat(2).returns(true)
    MockBetaAllowListConnector.check(testErn, "tfeNavHub").returns(Future.successful(Right(true)))
    MockBetaAllowListConnector.check(testErn, "tfePreValidate").returns(Future.successful(Right(preValidateEnabled)))
  }

  "PrevalidateTraderResults Controller" when {

    "user is on the private beta list" should {

      "must return OK and the correct view when there the ERN is invalid" in new Setup(
        emptyUserAnswers
          .set(PrevalidateConsigneeTraderIdentificationPage, PrevalidateTraderModel(ern = ernToCheck, entityGroup = testEntityGroup))
          .set(PrevalidateEPCPage(testIndex1), beerExciseProductCode)
      ) {

        MockPrevalidateTraderService.prevalidate(testErn, ernToCheck, entityGroupToCheck, Seq(beerExciseProductCode.code)).returns(Future.successful(
          PreValidateTraderApiResponse(ExciseTraderValidationResponse(
            validationTimestamp = validationTimestamp,
            exciseTraderResponse = Seq(exciseTraderResponse.copy(validTrader = false))
          ))
        ))

        val result = controller.onPageLoad(testErn)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          ernOpt = None,
          addCodeCall = exciseProductPageRoute(),
          approved = Seq.empty,
          notApproved = Seq.empty
        ).toString()
      }

      "must return OK and the correct view when the correct addCodeCall when below the max codes allowed" in new Setup(
        emptyUserAnswers
          .set(PrevalidateConsigneeTraderIdentificationPage, PrevalidateTraderModel(ern = ernToCheck, entityGroup = testEntityGroup))
          .set(PrevalidateEPCPage(0), wineExciseProductCode)
          .set(PrevalidateEPCPage(1), wineExciseProductCode)
          .set(PrevalidateEPCPage(2), wineExciseProductCode)
          .set(PrevalidateEPCPage(3), wineExciseProductCode)
          .set(PrevalidateEPCPage(4), wineExciseProductCode)
          .set(PrevalidateEPCPage(5), wineExciseProductCode)
          .set(PrevalidateEPCPage(6), wineExciseProductCode)
          .set(PrevalidateEPCPage(7), wineExciseProductCode)
          .set(PrevalidateEPCPage(8), wineExciseProductCode)
      ) {

        MockPrevalidateTraderService.prevalidate(testErn, ernToCheck, entityGroupToCheck, (1 to 9).map(_ => wineExciseProductCode.code))
          .returns(Future.successful(preValidateApiResponseModel))

        val result = controller.onPageLoad(testErn)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          ernOpt = Some(ernToCheck),
          addCodeCall = exciseProductPageRoute(9),
          approved = (1 to 9).map(_ => wineExciseProductCode),
          notApproved = Seq.empty
        ).toString()
      }

      "must return OK and the correct view when the correct addCodeCall when on the max codes allowed" in new Setup(
        emptyUserAnswers
          .set(PrevalidateConsigneeTraderIdentificationPage, PrevalidateTraderModel(ern = ernToCheck, entityGroup = testEntityGroup))
          .set(PrevalidateEPCPage(0), wineExciseProductCode)
          .set(PrevalidateEPCPage(1), wineExciseProductCode)
          .set(PrevalidateEPCPage(2), wineExciseProductCode)
          .set(PrevalidateEPCPage(3), wineExciseProductCode)
          .set(PrevalidateEPCPage(4), wineExciseProductCode)
          .set(PrevalidateEPCPage(5), wineExciseProductCode)
          .set(PrevalidateEPCPage(6), wineExciseProductCode)
          .set(PrevalidateEPCPage(7), wineExciseProductCode)
          .set(PrevalidateEPCPage(8), wineExciseProductCode)
          .set(PrevalidateEPCPage(9), wineExciseProductCode)
      ) {

        MockPrevalidateTraderService.prevalidate(testErn, ernToCheck, entityGroupToCheck, (1 to 10).map(_ => wineExciseProductCode.code))
          .returns(Future.successful(preValidateApiResponseModel))

        val result = controller.onPageLoad(testErn)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          ernOpt = Some(ernToCheck),
          addCodeCall = addToListPageRoute,
          approved = (1 to 10).map(_ => wineExciseProductCode),
          notApproved = Seq.empty
        ).toString()
      }

      "must return OK and the correct view when the correct addCodeCall when above the max codes allowed" in new Setup(
        emptyUserAnswers
          .set(PrevalidateConsigneeTraderIdentificationPage, PrevalidateTraderModel(ern = ernToCheck, entityGroup = testEntityGroup))
          .set(PrevalidateEPCPage(0), wineExciseProductCode)
          .set(PrevalidateEPCPage(1), wineExciseProductCode)
          .set(PrevalidateEPCPage(2), wineExciseProductCode)
          .set(PrevalidateEPCPage(3), wineExciseProductCode)
          .set(PrevalidateEPCPage(4), wineExciseProductCode)
          .set(PrevalidateEPCPage(5), wineExciseProductCode)
          .set(PrevalidateEPCPage(6), wineExciseProductCode)
          .set(PrevalidateEPCPage(7), wineExciseProductCode)
          .set(PrevalidateEPCPage(8), wineExciseProductCode)
          .set(PrevalidateEPCPage(9), wineExciseProductCode)
          .set(PrevalidateEPCPage(10), wineExciseProductCode)
      ) {

        MockPrevalidateTraderService.prevalidate(testErn, ernToCheck, entityGroupToCheck, (1 to 11).map(_ => wineExciseProductCode.code))
          .returns(Future.successful(preValidateApiResponseModel))

        val result = controller.onPageLoad(testErn)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          ernOpt = Some(ernToCheck),
          addCodeCall = addToListPageRoute,
          approved = (1 to 11).map(_ => wineExciseProductCode),
          notApproved = Seq.empty
        ).toString()
      }

      "must return OK and the correct view when all EPCs are eligible" in new Setup(
        emptyUserAnswers
          .set(PrevalidateConsigneeTraderIdentificationPage, PrevalidateTraderModel(ern = ernToCheck, entityGroup = testEntityGroup))
          .set(PrevalidateEPCPage(0), wineExciseProductCode)
          .set(PrevalidateEPCPage(1), beerExciseProductCode)
          .set(PrevalidateEPCPage(2), tobaccoExciseProductCode)
      ) {

        MockPrevalidateTraderService.prevalidate(testErn, ernToCheck, entityGroupToCheck, Seq(wineExciseProductCode, beerExciseProductCode, tobaccoExciseProductCode).map(_.code))
          .returns(Future.successful(preValidateApiResponseModel))

        val result = controller.onPageLoad(testErn)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          ernOpt = Some(ernToCheck),
          addCodeCall = exciseProductPageRoute(3),
          approved = Seq(wineExciseProductCode, beerExciseProductCode, tobaccoExciseProductCode),
          notApproved = Seq.empty
        ).toString()
      }

      "must return OK and the correct view when all EPCs are ineligible" in new Setup(
        emptyUserAnswers
          .set(PrevalidateConsigneeTraderIdentificationPage, PrevalidateTraderModel(ern = ernToCheck, entityGroup = testEntityGroup))
          .set(PrevalidateEPCPage(0), wineExciseProductCode)
          .set(PrevalidateEPCPage(1), beerExciseProductCode)
          .set(PrevalidateEPCPage(2), tobaccoExciseProductCode)
      ) {

        MockPrevalidateTraderService.prevalidate(testErn, ernToCheck, entityGroupToCheck, Seq(wineExciseProductCode, beerExciseProductCode, tobaccoExciseProductCode).map(_.code))
          .returns(Future.successful(preValidateApiResponseModel.copy(
            exciseTraderValidationResponse = ExciseTraderValidationResponse(
              validationTimestamp = validationTimestamp,
              exciseTraderResponse = Seq(
                exciseTraderResponse.copy(validateProductAuthorisationResponse = Some(ValidateProductAuthorisationResponse(
                  valid = false,
                  productError = Some(Seq(
                    ProductError(wineExciseProductCode.code, "error code", "error text"),
                    ProductError(beerExciseProductCode.code, "error code", "error text"),
                    ProductError(tobaccoExciseProductCode.code, "error code", "error text")
                  ))
                )))
              )
            )
          )))

        val result = controller.onPageLoad(testErn)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          ernOpt = Some(ernToCheck),
          addCodeCall = exciseProductPageRoute(3),
          approved = Seq.empty,
          notApproved = Seq(wineExciseProductCode, beerExciseProductCode, tobaccoExciseProductCode)
        ).toString()
      }

      "must return OK and the correct view when some EPCs are eligible and some are not" in new Setup(
        emptyUserAnswers
          .set(PrevalidateConsigneeTraderIdentificationPage, PrevalidateTraderModel(ern = ernToCheck, entityGroup = testEntityGroup))
          .set(PrevalidateEPCPage(0), wineExciseProductCode)
          .set(PrevalidateEPCPage(1), beerExciseProductCode)
          .set(PrevalidateEPCPage(2), tobaccoExciseProductCode)
      ) {

        MockPrevalidateTraderService.prevalidate(testErn, ernToCheck, entityGroupToCheck, Seq(wineExciseProductCode, beerExciseProductCode, tobaccoExciseProductCode).map(_.code))
          .returns(Future.successful(preValidateApiResponseModel.copy(
            exciseTraderValidationResponse = ExciseTraderValidationResponse(
              validationTimestamp = validationTimestamp,
              exciseTraderResponse = Seq(
                exciseTraderResponse.copy(validateProductAuthorisationResponse = Some(ValidateProductAuthorisationResponse(
                  valid = false,
                  productError = Some(Seq(
                    ProductError(wineExciseProductCode.code, "error code", "error text"),
                    ProductError(beerExciseProductCode.code, "error code", "error text")
                  ))
                )))
              )
            )
          )))

        val result = controller.onPageLoad(testErn)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          ernOpt = Some(ernToCheck),
          addCodeCall = exciseProductPageRoute(3),
          approved = Seq(tobaccoExciseProductCode),
          notApproved = Seq(wineExciseProductCode, beerExciseProductCode)
        ).toString()
      }

      "must redirect to the first EPC entry page when no EPCs have been entered" in new Setup(emptyUserAnswers
        .set(PrevalidateConsigneeTraderIdentificationPage, PrevalidateTraderModel(ern = ernToCheck, entityGroup = testEntityGroup)), getEPCs = false) {

        val result = controller.onPageLoad(testErn)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.PrevalidateExciseProductCodeController.onPageLoad(testErn, Index(0), NormalMode).url
      }

      "must redirect to the ERN entry page when no ERN has been entered" in new Setup(emptyUserAnswers
        .set(PrevalidateEPCPage(0), wineExciseProductCode), getEPCs = false) {

        val result = controller.onPageLoad(testErn)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.PrevalidateConsigneeTraderIdentificationController.onPageLoad(testErn).url
      }

      "must redirect to the ERN entry page when no data has been entered" in new Setup(emptyUserAnswers, getEPCs = false) {

        val result = controller.onPageLoad(testErn)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.PrevalidateConsigneeTraderIdentificationController.onPageLoad(testErn).url
      }
    }

    "user is NOT on the private beta list" should {

      "redirect to legacy" in new Setup(preValidateEnabled = false, getEPCs = false) {

        val result = controller.onPageLoad(testErn)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual "http://localhost:8080/emcs/trader/GBWKTestErn/prevalidate"
      }
    }
  }
}
