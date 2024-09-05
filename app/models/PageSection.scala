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

package models

import models.common.{Enumerable, WithName}

sealed trait PageSection

object PageSection extends Enumerable.Implicits {

  case object Home extends WithName("Home") with PageSection

  case object Messages extends WithName("Messages") with PageSection

  case object Drafts extends WithName("Drafts") with PageSection

  case object Movements extends WithName("Movements") with PageSection

  case object Templates extends WithName("Templates") with PageSection

  val values: Seq[PageSection] = Seq(
    Home, Messages, Drafts, Movements, Templates
  )
}
