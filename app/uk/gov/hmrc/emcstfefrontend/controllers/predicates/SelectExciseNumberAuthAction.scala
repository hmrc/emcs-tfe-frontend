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
import uk.gov.hmrc.emcstfefrontend.models.auth.ExciseEnrolmentsRequest
import uk.gov.hmrc.emcstfefrontend.utils.Logging
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}

trait SelectExciseNumberAuthAction extends ActionBuilder[ExciseEnrolmentsRequest, AnyContent] with ActionFunction[Request, ExciseEnrolmentsRequest] with Logging

@Singleton
class SelectExciseNumberAuthActionImpl @Inject()(override val authConnector: AuthConnector,
                                                 override val parser: BodyParsers.Default,
                                                 config: AppConfig
                                                )(implicit override val executionContext: ExecutionContext, val messagesApi: MessagesApi) extends SelectExciseNumberAuthAction with AuthorisedFunctions {

  override def invokeBlock[A](request: Request[A], block: ExciseEnrolmentsRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(
      session = request.session,
      request = request
    )

    authorised().retrieve(Retrievals.affinityGroup and Retrievals.allEnrolments and Retrievals.internalId and Retrievals.credentials) {

      case Some(Organisation) ~ enrolments ~ Some(internalId) ~ Some(credentials) =>
        val exciseEnrolments = enrolments.enrolments.filter(enrolment =>
          enrolment.key == EnrolmentKeys.EMCS_ENROLMENT && enrolment.isActivated
        )

        if(exciseEnrolments.isEmpty) {
          logger.warn(s"[invokeBlock] User has no Active EMCS Enrolments")
          Future.successful(Redirect(controllers.errors.routes.UnauthorisedController.unauthorised()))
        } else {
          val exciseEnrolmentsRequest = ExciseEnrolmentsRequest(request, exciseEnrolments, internalId, credentials.providerId)
          block(exciseEnrolmentsRequest)
        }
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
