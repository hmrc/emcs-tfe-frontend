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

package forms.mappings

import play.api.data.validation.{Constraint, Invalid, Valid}

import java.time.LocalDate
import scala.util.Try

trait Constraints {

  protected def firstError[A](constraints: Constraint[A]*): Constraint[A] =
    Constraint {
      input =>
        constraints
          .map(_.apply(input))
          .find(_ != Valid)
          .getOrElse(Valid)
    }

  protected def minimumValue[A](minimum: A, errorKey: String)(implicit ev: Ordering[A]): Constraint[A] =
    Constraint {
      input =>

        import ev._

        if (input >= minimum) {
          Valid
        } else {
          Invalid(errorKey, minimum)
        }
    }

  protected def maximumValue[A](maximum: A, errorKey: String)(implicit ev: Ordering[A]): Constraint[A] =
    Constraint {
      input =>

        import ev._

        if (input <= maximum) {
          Valid
        } else {
          Invalid(errorKey, maximum)
        }
    }

  protected def inRange[A](minimum: A, maximum: A, errorKey: String)(implicit ev: Ordering[A]): Constraint[A] =
    Constraint {
      input =>

        import ev._

        if (input >= minimum && input <= maximum) {
          Valid
        } else {
          Invalid(errorKey, minimum, maximum)
        }
    }

  protected def regexp(regex: String, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.matches(regex) =>
        Valid
      case _ =>
        Invalid(errorKey, regex)
    }

  protected def regexpUnlessEmpty(regex: String, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.isEmpty =>
        Valid
      case str if str.matches(regex) =>
        Valid
      case _ =>
        Invalid(errorKey, regex)
    }

  protected def fixedLength(length: Int, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.length == length =>
        Valid
      case _ =>
        Invalid(errorKey, length)
    }

  protected def maxLength(maximum: Int, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.length <= maximum =>
        Valid
      case _ =>
        Invalid(errorKey, maximum)
    }

  protected def valueInList(list: Seq[String], errorKey: String, args: Any*): Constraint[String] =
    Constraint {
      case value if list.contains(value) =>
        Valid
      case _ =>
        Invalid(errorKey, args: _*)
    }

  protected def decimalMaxLength(maximum: Int, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.replace(".", "").length <= maximum =>
        Valid
      case _ =>
        Invalid(errorKey, maximum)
    }


  protected def isDecimal(errorKey: String): Constraint[String] =
    Constraint {
      case answer if Try(BigDecimal(answer)).isSuccess =>
        Valid
      case _ =>
        Invalid(errorKey)
    }

  protected def isInt(errorKey: String): Constraint[String] =
    Constraint {
      case answer if Try(BigInt(answer)).isSuccess =>
        Valid
      case _ =>
        Invalid(errorKey)
    }

  protected def decimalRange(min: BigDecimal, max: BigDecimal, errorKey: String): Constraint[BigDecimal] =
    Constraint {
      case answer if answer <= max && answer >= min =>
        Valid
      case _ =>
        Invalid(errorKey, min, max)
    }

  protected def maxDecimalPlaces(max: Int, errorKey: String): Constraint[BigDecimal] =
    Constraint {
      case answer if answer.scale <= max =>
        Valid
      case _ =>
        Invalid(errorKey, max)
    }

  protected def decimalMaxAmount(maximum: BigDecimal, errorKey: String): Constraint[BigDecimal] =
    Constraint {
      case answer if answer <= maximum =>
        Valid
      case _ =>
        Invalid(errorKey, maximum)
    }

  protected def maxDate(maximum: LocalDate, errorKey: String, args: Any*): Constraint[LocalDate] =
    Constraint {
      case date if date.isAfter(maximum) =>
        Invalid(errorKey, args: _*)
      case _ =>
        Valid
    }

  protected def minDate(minimum: LocalDate, errorKey: String, args: Any*): Constraint[LocalDate] =
    Constraint {
      case date if date.isBefore(minimum) =>
        Invalid(errorKey, args: _*)
      case _ =>
        Valid
    }

  protected def nonEmptySet(errorKey: String): Constraint[Set[_]] =
    Constraint {
      case set if set.nonEmpty =>
        Valid
      case _ =>
        Invalid(errorKey)
    }

  protected def exclusiveItemInSet(errorKey: String, itemName: String): Constraint[Set[_]] =
    Constraint {
      case set if set.map(_.toString).contains(itemName) & set.size == 1 =>
        Valid
      case set if !set.map(_.toString).contains(itemName) =>
        Valid
      case _ =>
        Invalid(errorKey)
    }
}
