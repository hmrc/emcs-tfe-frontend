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

import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.mvc.{Action, AnyContent, BodyParsers, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Organisation}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.{Credentials, ~}
import base.SpecBase
import config.EnrolmentKeys
import controllers
import fixtures.BaseFixtures

import scala.concurrent.ExecutionContext

class SelectExciseNumberAuthActionSpec extends SpecBase with BaseFixtures {

  lazy val bodyParsers = app.injector.instanceOf[BodyParsers.Default]
  implicit lazy val ec = app.injector.instanceOf[ExecutionContext]

  type AuthRetrieval = ~[~[~[Option[AffinityGroup], Enrolments], Option[String]], Option[Credentials]]

  implicit val fakeRequest = FakeRequest()

  trait Harness {

    val authConnector: AuthConnector
    lazy val authAction = new SelectExciseNumberAuthActionImpl(authConnector, bodyParsers, appConfig)
    def onPageLoad(): Action[AnyContent] = authAction { _ => Results.Ok }

    lazy val result = onPageLoad()(fakeRequest)
  }

  def authResponse(affinityGroup: Option[AffinityGroup] = Some(Organisation),
                   enrolments: Enrolments = Enrolments(Set.empty),
                   internalId: Option[String] = Some(testInternalId),
                   credId: Option[Credentials] = Some(Credentials(testCredId, "gg"))): AuthRetrieval =
    new ~(new ~(new ~(affinityGroup, enrolments), internalId), credId)

  "SelectExciseNumberAuthAction" when {

    "calling .invokeBlock" when {

      "User is not logged in" must {

        "redirect to the sign-in URL with the ContinueURL set" in new Harness {

          override val authConnector = new FakeFailingAuthConnector(new BearerTokenExpired)

          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some("http://localhost:9949/auth-login-stub/gg-sign-in?continue=http%3A%2F%2Flocalhost%3A8310%2Femcs%2Faccount")
        }
      }

      "An unexpected Authorisation exception is returned from the Auth library" must {

        "redirect to unauthorised" in new Harness {

          override val authConnector = new FakeFailingAuthConnector(new InsufficientConfidenceLevel)

          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(controllers.errors.routes.UnauthorisedController.unauthorised().url)
        }
      }

      "User is logged in" when {

        "Affinity Group of user does not exist" must {

          "redirect to unauthorised" in new Harness {

            override val authConnector = new FakeSuccessAuthConnector(authResponse(affinityGroup = None))

            status(result) shouldBe SEE_OTHER
            redirectLocation(result) shouldBe Some(controllers.errors.routes.UnauthorisedController.unauthorised().url)
          }
        }

        "Affinity Group of user is not Organisation" must {

          "redirect to unauthorised" in new Harness {

            override val authConnector = new FakeSuccessAuthConnector(authResponse(affinityGroup = Some(Agent)))

            status(result) shouldBe SEE_OTHER
            redirectLocation(result) shouldBe Some(controllers.errors.routes.UnauthorisedController.unauthorised().url)
          }
        }

        "Affinity Group of user is Organisation" when {

          "internalId is not retrieved from Auth" must {

            "redirect to unauthorised" in new Harness {

              override val authConnector = new FakeSuccessAuthConnector(authResponse(internalId = None))

              status(result) shouldBe SEE_OTHER
              redirectLocation(result) shouldBe Some(controllers.errors.routes.UnauthorisedController.unauthorised().url)
            }
          }

          "internalId is retrieved from Auth" when {

            "credential is not retrieved from Auth" must {

              "redirect to unauthorised" in new Harness {

                override val authConnector = new FakeSuccessAuthConnector(authResponse(credId = None))

                status(result) shouldBe SEE_OTHER
                redirectLocation(result) shouldBe Some(controllers.errors.routes.UnauthorisedController.unauthorised().url)
              }
            }

            "credential is retrieved from Auth" when {

              s"Enrolments is missing the ${EnrolmentKeys.EMCS_ENROLMENT}" must {

                "redirect to unauthorised" in new Harness {

                  override val authConnector = new FakeSuccessAuthConnector(authResponse())

                  status(result) shouldBe SEE_OTHER
                  redirectLocation(result) shouldBe Some(controllers.errors.routes.UnauthorisedController.unauthorised().url)
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

                  status(result) shouldBe SEE_OTHER
                  redirectLocation(result) shouldBe Some(controllers.errors.routes.UnauthorisedController.unauthorised().url)
                }
              }

              s"Enrolments exists for ${EnrolmentKeys.EMCS_ENROLMENT} AND is activated" must {

                "allow the User through, returning a 200 (OK)" in new Harness {

                  override val authConnector = new FakeSuccessAuthConnector(authResponse(enrolments = Enrolments(Set(
                    Enrolment(
                      key = EnrolmentKeys.EMCS_ENROLMENT,
                      identifiers = Seq(EnrolmentIdentifier(EnrolmentKeys.ERN, testErn)),
                      state = EnrolmentKeys.ACTIVATED
                    )
                  ))))

                  status(result) shouldBe OK
                }
              }
            }
          }
        }
      }
    }
  }
}
