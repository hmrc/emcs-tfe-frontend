/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.config

import com.google.inject.AbstractModule

class Module extends AbstractModule {

  override def configure(): Unit = {

    bind(classOf[AppConfig]).asEagerSingleton()
  }
}
