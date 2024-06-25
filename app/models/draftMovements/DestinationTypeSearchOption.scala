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

package models.draftMovements

import models.SelectOptionModel
import models.common.{DestinationType, Enumerable}
import play.api.i18n.Messages
import play.api.mvc.QueryStringBindable
import models.common.WithName
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.checkbox._

import scala.util.Try

sealed trait DestinationTypeSearchOption extends SelectOptionModel {
  override val code: String = this.toString
  val destinationType: DestinationType
}

object DestinationTypeSearchOption extends Enumerable.Implicits {

  case object TaxWarehouse extends WithName("taxWarehouse") with DestinationTypeSearchOption {
    override val displayName = "destinationType.1"
    override val destinationType: DestinationType = DestinationType.TaxWarehouse
  }

  case object RegisteredConsignee extends WithName("registeredConsignee") with DestinationTypeSearchOption {
    override val displayName = "destinationType.2"
    override val destinationType: DestinationType = DestinationType.RegisteredConsignee
  }

  case object TemporaryRegisteredConsignee extends WithName("temporaryRegisteredConsignee") with DestinationTypeSearchOption {
    override val displayName = "destinationType.3"
    override val destinationType: DestinationType = DestinationType.TemporaryRegisteredConsignee
  }

  case object DirectDelivery extends WithName("directDelivery") with DestinationTypeSearchOption {
    override val displayName = "destinationType.4"
    override val destinationType: DestinationType = DestinationType.DirectDelivery
  }

  case object ExemptedOrganisation extends WithName("exemptedOrganisation") with DestinationTypeSearchOption {
    override val displayName = "destinationType.5"
    override val destinationType: DestinationType = DestinationType.ExemptedOrganisation
  }

  case object Export extends WithName("export") with DestinationTypeSearchOption {
    override val displayName = "destinationType.6"
    override val destinationType: DestinationType = DestinationType.Export
  }

  case object UnknownDestination extends WithName("unknownDestination") with DestinationTypeSearchOption {
    override val displayName = "destinationType.8"
    override val destinationType: DestinationType = DestinationType.UnknownDestination
  }

  case object CertifiedConsignee extends WithName("certifiedConsignee") with DestinationTypeSearchOption {
    override val displayName = "destinationType.9"
    override val destinationType: DestinationType = DestinationType.CertifiedConsignee
  }

  case object TemporaryCertifiedConsignee extends WithName("temporaryCertifiedConsignee") with DestinationTypeSearchOption {
    override val displayName = "destinationType.10"
    override val destinationType: DestinationType = DestinationType.TemporaryCertifiedConsignee
  }

  val values: Seq[DestinationTypeSearchOption] = Seq(
    TaxWarehouse,
    RegisteredConsignee,
    TemporaryRegisteredConsignee,
    DirectDelivery,
    ExemptedOrganisation,
    Export,
    UnknownDestination,
    CertifiedConsignee,
    TemporaryCertifiedConsignee
  )

  def checkboxItems(implicit messages: Messages): Seq[CheckboxItem] =
    values.zipWithIndex.map {
      case (value, index) =>
        CheckboxItemViewModel(
          content = Text(messages(value.displayName)),
          fieldId = "destinationTypes",
          index   = index,
          value   = value.toString
        )
    }

  //noinspection ScalaStyle
  def apply(code: String): DestinationTypeSearchOption = code match {
    case TaxWarehouse.code => TaxWarehouse
    case RegisteredConsignee.code => RegisteredConsignee
    case TemporaryRegisteredConsignee.code => TemporaryRegisteredConsignee
    case DirectDelivery.code => DirectDelivery
    case ExemptedOrganisation.code => ExemptedOrganisation
    case Export.code => Export
    case UnknownDestination.code => UnknownDestination
    case CertifiedConsignee.code => CertifiedConsignee
    case TemporaryCertifiedConsignee.code => TemporaryCertifiedConsignee
    case invalid =>throw new IllegalArgumentException(s"Invalid argument of '$invalid' received which can not be mapped to a DestinationTypeSearchOption")
  }

  implicit val enumerable: Enumerable[DestinationTypeSearchOption] =
    Enumerable(values.map(v => v.toString -> v): _*)

  implicit def queryStringBinder(implicit stringBinder: QueryStringBindable[String]): QueryStringBindable[Seq[DestinationTypeSearchOption]] =
    new QueryStringBindable[Seq[DestinationTypeSearchOption]] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, Seq[DestinationTypeSearchOption]]] = {
        params.get(key).map { destinationTypeCodes =>
          Try(destinationTypeCodes.map(apply)).fold[Either[String, Seq[DestinationTypeSearchOption]]](
            e => Left(e.getMessage),
            Right(_)
          )
        }
      }

      override def unbind(key: String, destinations: Seq[DestinationTypeSearchOption]): String =
        destinations.map(destinationType =>
          stringBinder.unbind(key, destinationType.toString)
        ).mkString("&")
    }
}
