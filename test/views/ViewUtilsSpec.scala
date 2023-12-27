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

package views

import base.SpecBase
import fixtures.BaseFixtures
import models.auth.UserRequest
import models.requests.DataRequest
import play.api.Application
import play.api.data.Forms._
import play.api.data.{Form, FormError}
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.FakeRequest
import viewmodels.TraderInfo

class ViewUtilsSpec extends SpecBase with BaseFixtures {

  def messagesApi(app: Application): MessagesApi = app.injector.instanceOf[MessagesApi]
  def messages(app: Application): Messages = messagesApi(app).preferred(FakeRequest())

  def createDataRequest(hasMultipleErns: Boolean): DataRequest[_] =
    DataRequest(
      UserRequest(FakeRequest("GET", s"/consignment/$testErn/$testArc"), testErn, testInternalId, testCredId, hasMultipleErns)(messagesApi),
      testMinTraderKnownFacts,
      testMessageStatistics
    )

  ".title" in {
    case class UserData(name: String, age: Int)

    val userForm = Form(
      mapping(
        "name" -> text,
        "age" -> number
      )(UserData.apply)(UserData.unapply)
    )

    implicit val msgs: Messages = messages(app)

    ViewUtils.title(userForm, "TITLE") mustBe " TITLE - Excise Movement and Control System - GOV.UK"
  }

  ".title" when {
    case class UserData(name: String, age: Int)
    implicit val msgs: Messages = messages(app)

    val userForm = Form(
      mapping(
        "name" -> text,
        "age" -> number
      )(UserData.apply)(UserData.unapply)
    )

    "without form errors" in {
      ViewUtils.title(userForm, "TITLE") mustBe " TITLE - Excise Movement and Control System - GOV.UK"
    }

    "with form errors" in {
      val formError = FormError("testKey", "testMessage")
      ViewUtils.title(userForm.withError(formError), "TITLE") mustBe "Error: TITLE - Excise Movement and Control System - GOV.UK"
    }
  }

  ".titleNoForm" in {
    implicit val msgs: Messages = messages(app)
    ViewUtils.titleNoForm("TITLE") mustBe "TITLE - Excise Movement and Control System - GOV.UK"
  }

  ".maybeShowActiveTrader" when {

    "given a data request, when the user has multiple ERNs" in {
      ViewUtils.maybeShowActiveTrader(createDataRequest(true)) mustBe Some(TraderInfo(testMinTraderKnownFacts.traderName, testErn))
    }

    "given a data request, when the user has a single ERN" in {
      ViewUtils.maybeShowActiveTrader(createDataRequest(false)) mustBe None
    }
  }

}
