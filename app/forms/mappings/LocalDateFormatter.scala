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

import play.api.data.FormError
import play.api.data.format.Formatter

import java.time.{LocalDate, Month}
import scala.util.{Failure, Success, Try}

private[mappings] class LocalDateFormatter(
                                            allRequiredKey: String,
                                            oneRequiredKey: String,
                                            twoRequiredKey: String,
                                            oneInvalidKey: String,
                                            notARealDateKey: String,
                                            args: Seq[String] = Seq.empty
                                          ) extends Formatter[LocalDate] with Formatters {

  private val dayText = "day"
  private val monthText = "month"
  private val yearText = "year"

  private val fieldKeys: List[String] = List(dayText, monthText, yearText)

  private def toDate(key: String, day: Int, month: Int, year: Int): Either[Seq[FormError], LocalDate] =
    Try(LocalDate.of(year, month, day)) match {
      case Success(date) =>
        Right(date)
      case Failure(_) =>
        Left(Seq(FormError(key, notARealDateKey, args)))
    }

  private def handlePartialInputs(key: String, data: Map[String, String]): Seq[FormError] = {
    val fields = fieldKeys.map {
      field =>
        field -> data.get(s"$key.$field").filter(_.nonEmpty)
    }.toMap

    lazy val missingFields = fields
      .withFilter(_._2.isEmpty)
      .map(_._1)
      .toList

    fields.count(_._2.isDefined) match {
      case 3 => Nil
      case 2 => Seq(FormError(key, oneRequiredKey, missingFields ++ args))
      case 1 => Seq(FormError(key, twoRequiredKey, missingFields ++ args))
      case _ => Seq(FormError(key, allRequiredKey, args))
    }
  }

  private def handleRangeInputs(key: String, data: Map[String, String]): Seq[FormError] = {
    def isValidInt(value: Option[String], range: Range): Boolean =
      value.flatMap(v => Try(v.trim.toInt).toOption).exists(range.contains)

    def isValidMonthName(value: Option[String]): Boolean =
      value.exists(v => Month.values().exists(m => isMatchingMonth(m, v)))

    val dayValid = isValidInt(data.get(s"$key.day"), 1 to 31)
    val monthValid = isValidInt(data.get(s"$key.month"), 1 to 12) || isValidMonthName(data.get(s"$key.month"))
    val yearValid = isValidInt(data.get(s"$key.year"), 1 to Int.MaxValue)

    (dayValid, monthValid, yearValid) match {
      case (true, true, true) => Nil
      case (false, true, true) => Seq(FormError(key, oneInvalidKey, Seq(dayText) ++ args))
      case (true, false, true) => Seq(FormError(key, oneInvalidKey, Seq(monthText) ++ args))
      case (true, true, false) => Seq(FormError(key, oneInvalidKey, Seq(yearText) ++ args))
      case (true, false, false) => Seq(FormError(key, notARealDateKey, args))
      case (false, true, false) => Seq(FormError(key, notARealDateKey, args))
      case (false, false, true) => Seq(FormError(key, notARealDateKey, args))
      case (false, false, false) => Seq(FormError(key, notARealDateKey, args))
    }
  }

  private def monthNumberFromMonthName(monthName: String): Int =
    Month.values().find(isMatchingMonth(_, monthName)).map(_.getValue).getOrElse(0)

  private def isMatchingMonth(month: Month, monthName: String): Boolean =
    monthName.equalsIgnoreCase(month.toString) || monthName.equalsIgnoreCase(month.toString.take(3))

  override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], LocalDate] = {
    handlePartialInputs(key, data) match {
      case Nil =>
        handleRangeInputs(key, data) match {
          case Nil => {
            val day = data(s"$key.day").trim.toInt
            val month = Try(data(s"$key.month").trim.toInt).getOrElse(monthNumberFromMonthName(data(s"$key.month").trim))
            val year = data(s"$key.year").trim.toInt

            toDate(key, day, month, year)
          }
          case rangeErrors => Left(rangeErrors)
        }
      case partialInputErrors => Left(partialInputErrors)
    }
  }

  override def unbind(key: String, value: LocalDate): Map[String, String] =
    Map(
      s"$key.day" -> value.getDayOfMonth.toString,
      s"$key.month" -> value.getMonthValue.toString,
      s"$key.year" -> value.getYear.toString
    )
}
