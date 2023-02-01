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

import com.google.inject.Inject
import config.{AppConfig, EnrolmentKeys}
import models.auth.UserRequest
import play.api.i18n.MessagesApi
import play.api.mvc.Results._
import play.api.mvc._
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import utils.Logging

import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}

trait AuthAction extends ActionBuilder[UserRequest, AnyContent] with ActionFunction[Request, UserRequest] with Logging {
  def checkErnMatchesRequest[A](ern: String)(block: => Future[Result])(implicit request: UserRequest[A]): Future[Result] =
    if (ern == request.ern) block else {
      logger.warn(s"User with ern: '${request.ern}' attempted to access ern: '$ern' which they are not authorised to view")
      Future.successful(Redirect(controllers.errors.routes.UnauthorisedController.unauthorised()))
    }
}

@Singleton
class AuthActionImpl @Inject()(override val authConnector: AuthConnector,
                               config: AppConfig,
                               val parser: BodyParsers.Default
                              )(implicit val executionContext: ExecutionContext, val messagesApi: MessagesApi) extends AuthAction with AuthorisedFunctions {

  override def invokeBlock[A](request: Request[A], block: UserRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(
      session = request.session,
      request = request
    )

    implicit val req = request

    authorised().retrieve(Retrievals.affinityGroup and Retrievals.allEnrolments and Retrievals.internalId and Retrievals.credentials) {

      case Some(Organisation) ~ enrolments ~ Some(internalId) ~ Some(credentials) =>
        checkOrganisationEMCSEnrolment(enrolments, internalId, credentials.providerId)(block)

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

  private def checkOrganisationEMCSEnrolment[A](enrolments: Enrolments,
                                                internalId: String,
                                                credId: String
                                               )(block: UserRequest[A] => Future[Result])
                                               (implicit request: Request[A]): Future[Result] =
    enrolments.enrolments.find(_.key == EnrolmentKeys.EMCS_ENROLMENT) match {
      case Some(enrolment) if enrolment.isActivated =>
        enrolment.identifiers.find(_.key == EnrolmentKeys.ERN).map(_.value) match {
          case Some(ern) =>
            block(UserRequest(request, ern, internalId, credId))
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
