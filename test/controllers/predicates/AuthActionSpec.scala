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

package controllers.predicates

import base.SpecBase
import config.{AppConfig, EnrolmentKeys}
import featureswitch.core.config._
import fixtures.BaseFixtures
import mocks.services.{MockGetMessageStatisticsService, MockGetTraderKnownFactsService}
import models.common.RoleType
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Organisation}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.{Credentials, ~}

import scala.concurrent.{ExecutionContext, Future}

class AuthActionSpec extends SpecBase
  with BaseFixtures
  with FeatureSwitching
  with MockGetTraderKnownFactsService
  with MockGetMessageStatisticsService {

  lazy val bodyParsers: BodyParsers.Default = app.injector.instanceOf[BodyParsers.Default]
  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  lazy val dataRetrievalAction: DataRetrievalAction = new DataRetrievalActionImpl(mockGetTraderKnownFactsService, mockGetMessageStatisticsService)

  type AuthRetrieval = ~[~[~[Option[AffinityGroup], Enrolments], Option[String]], Option[Credentials]]

  implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  lazy val config: AppConfig = appConfig

  abstract class Harness(ern: String = testErn) {

    val authConnector: AuthConnector
    lazy val authAction = new AuthActionImpl(authConnector, appConfig, bodyParsers)
    def onPageLoad(): Action[AnyContent] = authAction(ern) { _ => Results.Ok }

    lazy val result: Future[Result] = onPageLoad()(fakeRequest)
  }

  def authResponse(affinityGroup: Option[AffinityGroup] = Some(Organisation),
                   enrolments: Enrolments = Enrolments(Set.empty),
                   internalId: Option[String] = Some(testInternalId),
                   credId: Option[Credentials] = Some(Credentials(testCredId, "gg"))): AuthRetrieval =
    new ~(new ~(new ~(affinityGroup, enrolments), internalId), credId)

  "AuthAction" when {

    "calling .invokeBlock" when {

      "User is not logged in" must {

        "redirect to the sign-in URL with the ContinueURL set" in new Harness {

          override val authConnector = new FakeFailingAuthConnector(new BearerTokenExpired)

          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some("http://localhost:9949/auth-login-stub/gg-sign-in?continue=http%3A%2F%2Flocalhost%3A8310%2Femcs%2Faccount")
        }
      }

      "An unexpected Authorisation exception is returned from the Auth library" must {

        "redirect to unauthorised" in new Harness {

          override val authConnector = new FakeFailingAuthConnector(new InsufficientConfidenceLevel)

          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(controllers.errors.routes.UnauthorisedController.unauthorised().url)
        }
      }

      "User is logged in" when {

        "Affinity Group of user does not exist" must {

          "redirect to unauthorised" in new Harness {

            override val authConnector = new FakeSuccessAuthConnector(authResponse(affinityGroup = None))

            status(result) mustBe SEE_OTHER
            redirectLocation(result) mustBe Some(controllers.errors.routes.UnauthorisedController.unauthorised().url)
          }
        }

        "Affinity Group of user is not Organisation" must {

          "redirect to unauthorised" in new Harness {

            override val authConnector = new FakeSuccessAuthConnector(authResponse(affinityGroup = Some(Agent)))

            status(result) mustBe SEE_OTHER
            redirectLocation(result) mustBe Some(controllers.errors.routes.UnauthorisedController.unauthorised().url)
          }
        }

        "Affinity Group of user is Organisation" when {

          "internalId is not retrieved from Auth" must {

            "redirect to unauthorised" in new Harness {

              override val authConnector = new FakeSuccessAuthConnector(authResponse(internalId = None))

              status(result) mustBe SEE_OTHER
              redirectLocation(result) mustBe Some(controllers.errors.routes.UnauthorisedController.unauthorised().url)
            }
          }

          "internalId is retrieved from Auth" when {

            "credential is not retrieved from Auth" must {

              "redirect to unauthorised" in new Harness {

                override val authConnector = new FakeSuccessAuthConnector(authResponse(credId = None))

                status(result) mustBe SEE_OTHER
                redirectLocation(result) mustBe Some(controllers.errors.routes.UnauthorisedController.unauthorised().url)
              }
            }

            "credential is retrieved from Auth" when {

              s"Enrolments is missing the ${EnrolmentKeys.EMCS_ENROLMENT}" must {

                "redirect to unauthorised" in new Harness {

                  override val authConnector = new FakeSuccessAuthConnector(authResponse())

                  status(result) mustBe SEE_OTHER
                  redirectLocation(result) mustBe Some(controllers.errors.routes.UnauthorisedController.unauthorised().url)
                }
              }

              s"Enrolments exists for ${EnrolmentKeys.EMCS_ENROLMENT} but is NOT activated" must {

                "redirect to unauthorised" in new Harness {

                  override val authConnector = new FakeSuccessAuthConnector(authResponse(enrolments = Enrolments(Set(
                    Enrolment(
                      key = EnrolmentKeys.EMCS_ENROLMENT,
                      identifiers = Seq(EnrolmentIdentifier(EnrolmentKeys.ERN, testErn)),
                      state = EnrolmentKeys.INACTIVE
                    )
                  ))))

                  status(result) mustBe SEE_OTHER
                  redirectLocation(result) mustBe Some(controllers.errors.routes.UnauthorisedController.unauthorised().url)
                }
              }

              s"Enrolments exists for ${EnrolmentKeys.EMCS_ENROLMENT} AND is activated" when {

                s"the ${EnrolmentKeys.ERN} identifier is missing (must be impossible)" must {

                  "redirect to unauthorised" in new Harness {

                    override val authConnector = new FakeSuccessAuthConnector(authResponse(enrolments = Enrolments(Set(
                      Enrolment(
                        key = EnrolmentKeys.EMCS_ENROLMENT,
                        identifiers = Seq(),
                        state = EnrolmentKeys.ACTIVATED
                      )
                    ))))

                    status(result) mustBe SEE_OTHER
                    redirectLocation(result) mustBe Some(controllers.errors.routes.UnauthorisedController.unauthorised().url)
                  }
                }

                s"the ${EnrolmentKeys.ERN} identifier is present" must {

                  s"the ${EnrolmentKeys.ERN} identifier matches the ERN from the URL" when {
                    "allow the User through, returning a 200 (OK)" in new Harness {
                      override val authConnector = new FakeSuccessAuthConnector(authResponse(enrolments = Enrolments(Set(
                        Enrolment(
                          key = EnrolmentKeys.EMCS_ENROLMENT,
                          identifiers = Seq(EnrolmentIdentifier(EnrolmentKeys.ERN, testErn)),
                          state = EnrolmentKeys.ACTIVATED
                        )
                      ))))

                      status(result) mustBe OK
                    }
                  }

                  s"the ${EnrolmentKeys.ERN} identifier DOES NOT match the ERN from the URL" must {

                    "Redirect to unauthorised" in new Harness {

                      override val authConnector = new FakeSuccessAuthConnector(authResponse(enrolments = Enrolments(Set(
                        Enrolment(
                          key = EnrolmentKeys.EMCS_ENROLMENT,
                          identifiers = Seq(EnrolmentIdentifier(EnrolmentKeys.ERN, "otherErn")),
                          state = EnrolmentKeys.ACTIVATED
                        )
                      ))))

                      status(result) mustBe SEE_OTHER
                      redirectLocation(result) mustBe Some(controllers.errors.routes.UnauthorisedController.unauthorised().url)
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  "AuthActionHelper.ifCanAccessDraftTemplates" must {
    "return the block" when {
      RoleType.values.filter(_.canCreateNewMovement).foreach {
        roleType =>
          val ernPrefix = roleType.descriptionKey.split('.').last
          val ern = s"${ernPrefix}123"
          s"passed in ERN starting with $ernPrefix" in new Harness {
            override val authConnector = new FakeSuccessAuthConnector(authResponse(enrolments = Enrolments(Set(
              Enrolment(
                key = EnrolmentKeys.EMCS_ENROLMENT,
                identifiers = Seq(EnrolmentIdentifier(EnrolmentKeys.ERN, ern)),
                state = EnrolmentKeys.ACTIVATED
              )
            ))))

            val helper: AuthActionHelper = new AuthActionHelper {
              override val auth: AuthAction = authAction
              override val getData: DataRetrievalAction = dataRetrievalAction
            }

            val block: Future[Result] = Future.successful(Results.Ok)

            val res: Future[Result] = helper.ifCanAccessDraftTemplates(ern)(block)

            status(res) mustBe OK
          }
      }
    }
    "redirect to account home" when {
      RoleType.values.filterNot(_.canCreateNewMovement).foreach {
        roleType =>
          val ernPrefix = roleType.descriptionKey.split('.').last
          val ern = s"${ernPrefix}123"
          s"passed in ERN starting with $ernPrefix" in new Harness {
            override val authConnector = new FakeSuccessAuthConnector(authResponse(enrolments = Enrolments(Set(
              Enrolment(
                key = EnrolmentKeys.EMCS_ENROLMENT,
                identifiers = Seq(EnrolmentIdentifier(EnrolmentKeys.ERN, ern)),
                state = EnrolmentKeys.ACTIVATED
              )
            ))))

            val helper: AuthActionHelper = new AuthActionHelper {
              override val auth: AuthAction = authAction
              override val getData: DataRetrievalAction = dataRetrievalAction
            }

            val block: Future[Result] = Future.successful(Results.Ok)

            val res: Future[Result] = helper.ifCanAccessDraftTemplates(ern)(block)

            status(res) mustBe SEE_OTHER
            redirectLocation(res) mustBe Some(controllers.routes.AccountHomeController.viewAccountHome(ern).url)
          }
      }
    }
  }
}
