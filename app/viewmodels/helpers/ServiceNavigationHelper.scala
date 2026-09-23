/*
 * Copyright 2026 HM Revenue & Customs
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

package viewmodels.helpers

import config.AppConfig
import models.common.RoleType
import models.{NavigationBannerInfo, PageSection}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.servicenavigation.ServiceNavigationItem

import javax.inject.{Inject, Singleton}

@Singleton
class ServiceNavigationHelper @Inject()(appConfig: AppConfig) {

  def navigationItems(navInfo: NavigationBannerInfo)(implicit messages: Messages): Seq[ServiceNavigationItem] = {

    val roleType = RoleType.fromExciseRegistrationNumber(navInfo.ern)

    val home = Some(ServiceNavigationItem(
      content = Text(messages("navigation.home")),
      href = appConfig.host + appConfig.emcsTfeHomeUrl,
      current = navInfo.currentSection.contains(PageSection.Home),
      attributes = Map("id" -> "navigation-home-link")
    ))

    val newMessages = Some(ServiceNavigationItem(
      content = HtmlContent(messages("navigation.messages") + notificationBadge(navInfo.countOfNewMessages)),
      href = appConfig.host + appConfig.emcsTfeMessagesUrl(navInfo.ern),
      current = navInfo.currentSection.contains(PageSection.Messages),
      attributes = Map("id" -> "navigation-messages-link")
    ))

    val drafts = Option.when(roleType.canCreateNewMovement)(ServiceNavigationItem(
      content = Text(messages("navigation.drafts")),
      href = appConfig.host + appConfig.emcsTfeDraftMovementsUrl(navInfo.ern),
      current = navInfo.currentSection.contains(PageSection.Drafts),
      attributes = Map("id" -> "navigation-drafts-link")
    ))

    val movements = Some(ServiceNavigationItem(
      content = Text(messages("navigation.movements")),
      href = appConfig.host + appConfig.emcsTfeListMovementsUrl(navInfo.ern),
      current = navInfo.currentSection.contains(PageSection.Movements),
      attributes = Map("id" -> "navigation-movements-link")
    ))

    val templates = Option.when(appConfig.templatesLinkVisible && roleType.canCreateNewMovement)(ServiceNavigationItem(
      content = Text(messages("navigation.templates")),
      href = appConfig.host + appConfig.emcsTfeViewAllTemplatesUrl(navInfo.ern),
      current = navInfo.currentSection.contains(PageSection.Templates),
      attributes = Map("id" -> "navigation-templates-link")
    ))

    val bta = Some(ServiceNavigationItem(
      content = Text(messages("navigation.bta")),
      href = appConfig.businessTaxAccountUrl,
      attributes = Map("id" -> "navigation-bta-link")
    ))

    Seq(home, newMessages, drafts, movements, templates, bta).flatten
  }

  private def notificationBadge(countOfNewMessages: Option[Int]): String =
    countOfNewMessages match {
      case Some(count) if count > 99 => """ <span class="hmrc-notification-badge govuk-!-margin-left-1">99+</span>"""
      case Some(count) if count > 0  => s""" <span class="hmrc-notification-badge govuk-!-margin-left-1">$count</span>"""
      case _                         => ""
    }
}
