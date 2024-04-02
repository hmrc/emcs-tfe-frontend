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

package controllers.helpers

import config.AppConfig
import play.api.mvc.Result
import play.api.mvc.Results.Redirect

trait BetaChecks {

  def navigationHubBetaGuard(): (String, Result) =
    "tfeNavHub" -> Redirect(controllers.errors.routes.NotOnBetaListController.unauthorised())

  def messageInboxBetaGuard(ern: String)(implicit appConfig: AppConfig): Option[(String, Result)] =
    Some("tfeMessageInbox" -> Redirect(appConfig.emcsLegacyMessageInboxUrl(ern)))

  def homeBetaGuard(ern: String)(implicit appConfig: AppConfig): Option[(String, Result)] =
    Some("tfeHome" -> Redirect(appConfig.emcsLegacyHomeUrl(ern)))

  def draftsBetaGuard(ern: String)(implicit appConfig: AppConfig): Option[(String, Result)] =
    Some("tfeDrafts" -> Redirect(appConfig.emcsLegacyDraftsUrl(ern)))

  def searchMovementsBetaGuard(ern: String)(implicit appConfig: AppConfig): Option[(String, Result)] =
    Some("tfeSearchMovements" -> Redirect(appConfig.emcsLegacySearchMovementsUrl(ern)))

  def viewMovementBetaGuard(ern: String, arc: String)(implicit appConfig: AppConfig): Option[(String, Result)] =
    Some("tfeViewMovement" -> Redirect(appConfig.emcsLegacyViewMovementUrl(ern, arc)))

  def changeDestinationBetaGuard(ern: String, arc: String, ver: Int)(implicit appConfig: AppConfig): Option[(String, Result)] =
    Some("tfeChangeDestination" -> Redirect(appConfig.emcsLegacyChangeDestinationUrl(ern, arc, ver)))
}
