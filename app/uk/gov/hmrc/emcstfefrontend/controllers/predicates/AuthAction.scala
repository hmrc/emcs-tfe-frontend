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

package uk.gov.hmrc.emcstfefrontend.controllers.predicates

import com.google.inject.Inject
import play.api.i18n.MessagesApi
import play.api.mvc.Results._
import play.api.mvc._
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.emcstfefrontend.config.{AppConfig, EnrolmentKeys}
import uk.gov.hmrc.emcstfefrontend.controllers
import uk.gov.hmrc.emcstfefrontend.models.auth.UserRequest
import uk.gov.hmrc.emcstfefrontend.utils.Logging
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}

trait AuthAction {
  def apply(ern: String): ActionBuilder[UserRequest, AnyContent] with ActionFunction[Request, UserRequest]
}

@Singleton
class AuthActionImpl @Inject()(override val authConnector: AuthConnector,
                               config: AppConfig,
                               val bodyParser: BodyParsers.Default
                              )(implicit val ec: ExecutionContext, val messagesApi: MessagesApi) extends AuthAction with AuthorisedFunctions with Logging {

  def apply(ern: String): ActionBuilder[UserRequest, AnyContent] with ActionFunction[Request, UserRequest] =
    new ActionBuilder[UserRequest, AnyContent] with ActionFunction[Request, UserRequest] {

      override val parser = bodyParser
      override val executionContext = ec

      override def invokeBlock[A](request: Request[A], block: UserRequest[A] => Future[Result]): Future[Result] = {

        implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(
          session = request.session,
          request = request
        )

        implicit val req = request

        authorised().retrieve(Retrievals.affinityGroup and Retrievals.allEnrolments and Retrievals.internalId and Retrievals.credentials) {

          case Some(Organisation) ~ enrolments ~ Some(internalId) ~ Some(credentials) =>
            checkOrganisationEMCSEnrolment(ern, enrolments, internalId, credentials.providerId)(block)

          case Some(Organisation) ~ _ ~ None ~ _ =>
            logger.warn("[invokeBlock] InternalId could not be retrieved from Auth")
            Future.successful(Redirect(controllers.errors.routes.UnauthorisedController.unauthorised()))

          case Some(Organisation) ~ _ ~ _ ~ None =>
            logger.warn("[invokeBlock] Credentials could not be retrieved from Auth")
            Future.successful(Redirect(controllers.errors.routes.UnauthorisedController.unauthorised()))

          case Some(affinityGroup) ~ _ ~ _ ~ _ =>
            logger.warn(s"[invokeBlock] User has incompatible AffinityGroup of '$affinityGroup'")
            Future.successful(Redirect(controllers.errors.routes.UnauthorisedController.unauthorised()))

          case _ =>
            logger.warn(s"[invokeBlock] User has no AffinityGroup")
            Future.successful(Redirect(controllers.errors.routes.UnauthorisedController.unauthorised()))

        } recover {
          case _: NoActiveSession =>
            Redirect(config.loginUrl, Map("continue" -> Seq(config.loginContinueUrl)))
          case x: AuthorisationException =>
            logger.debug(s"[invokeBlock] Authorisation Exception ${x.reason}")
            Redirect(controllers.errors.routes.UnauthorisedController.unauthorised())
        }
      }
    }

  private def checkOrganisationEMCSEnrolment[A](ernFromUrl: String,
                                                enrolments: Enrolments,
                                                internalId: String,
                                                credId: String
                                               )(block: UserRequest[A] => Future[Result])
                                               (implicit request: Request[A]): Future[Result] =
    enrolments.enrolments.find(_.key == EnrolmentKeys.EMCS_ENROLMENT) match {
      case Some(enrolment) if enrolment.isActivated =>
        enrolment.identifiers.find(_.key == EnrolmentKeys.ERN).map(_.value) match {
          case Some(ern) if ern == ernFromUrl =>
            block(UserRequest(request, ern, internalId, credId))
          case Some(ern) =>
            logger.warn(s"User with ern: '$ern' attempted to access ern: '$ernFromUrl' which they are not authorised to view")
            Future.successful(Redirect(controllers.errors.routes.UnauthorisedController.unauthorised()))
          case None =>
            logger.error(s"[checkOrganisationEMCSEnrolment] Could not find ${EnrolmentKeys.ERN} from the ${EnrolmentKeys.EMCS_ENROLMENT} enrolment")
            Future.successful(Redirect(controllers.errors.routes.UnauthorisedController.unauthorised()))
        }
      case Some(enrolment) if !enrolment.isActivated =>
        logger.debug(s"[checkOrganisationEMCSEnrolment] ${EnrolmentKeys.EMCS_ENROLMENT} enrolment found but not activated")
        Future.successful(Redirect(controllers.errors.routes.UnauthorisedController.unauthorised()))
      case _ =>
        logger.debug(s"[checkOrganisationEMCSEnrolment] No ${EnrolmentKeys.EMCS_ENROLMENT} enrolment found")
        Future.successful(Redirect(controllers.errors.routes.UnauthorisedController.unauthorised()))
    }
}
