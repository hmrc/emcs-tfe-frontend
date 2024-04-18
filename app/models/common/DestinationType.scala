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

package models.common

import models.movementScenario.MovementScenario
import play.api.i18n.Messages
import play.api.mvc.QueryStringBindable
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.checkbox._

import scala.util.Try


sealed trait DestinationType

object DestinationType extends Enumerable.Implicits {
//registered consignor = import
  case object TaxWarehouse extends WithName("1") with DestinationType
  case object RegisteredConsignee extends WithName("2") with DestinationType
  case object TemporaryRegisteredConsignee extends WithName("3") with DestinationType
  case object DirectDelivery extends WithName("4") with DestinationType
  case object ExemptedOrganisation extends WithName("5") with DestinationType
  case object Export extends WithName("6") with DestinationType
  case object UnknownDestination extends WithName("8") with DestinationType
  case object CertifiedConsignee extends WithName("9") with DestinationType
  case object TemporaryCertifiedConsignee extends WithName("10") with DestinationType
  case object ReturnToThePlaceOfDispatchOfTheConsignor extends WithName("11") with DestinationType

  val values: Seq[DestinationType] = Seq(
    TaxWarehouse,
    RegisteredConsignee,
    TemporaryRegisteredConsignee,
    DirectDelivery,
    ExemptedOrganisation,
    Export,
    UnknownDestination,
    CertifiedConsignee,
    TemporaryCertifiedConsignee,
    ReturnToThePlaceOfDispatchOfTheConsignor
  )

  implicit val enumerable: Enumerable[DestinationType] =
    Enumerable(values.map(v => v.toString -> v): _*)

  def destinationType(code: String): DestinationType = values.find(_.toString == code) match {
    case Some(value) => value
    case None => throw new IllegalArgumentException(s"Destination code of '$code' could not be mapped to a valid Destination Type")
  }

  implicit def queryStringBinder(implicit stringBinder: QueryStringBindable[String]): QueryStringBindable[Seq[DestinationType]] =
    new QueryStringBindable[Seq[DestinationType]] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, Seq[DestinationType]]] = {
        params.get(key).map { destinationTypeCodes =>
          Try(destinationTypeCodes.map(destinationType)).fold[Either[String, Seq[DestinationType]]](
            e => Left(e.getMessage),
            Right(_)
          )
        }
      }

      override def unbind(key: String, destinations: Seq[DestinationType]): String =
        destinations.map(destinationType =>
          stringBinder.unbind(key, destinationType.toString)
        ).mkString("&")
    }

  def draftMovementsCheckboxItems(messageKeyPrefix: String)(implicit messages: Messages): Seq[CheckboxItem] = Seq(
    TaxWarehouse,
    RegisteredConsignee,
    TemporaryRegisteredConsignee,
    ExemptedOrganisation,
    DirectDelivery,
    UnknownDestination,
    Export,
    CertifiedConsignee,
    TemporaryCertifiedConsignee
  ).zipWithIndex.map { case (value, index) =>
    CheckboxItemViewModel(
      content = Text(messages(s"$messageKeyPrefix.${value.toString}")),
      fieldId = "destinationTypes",
      index = index,
      value = value.toString
    )
  }
}
