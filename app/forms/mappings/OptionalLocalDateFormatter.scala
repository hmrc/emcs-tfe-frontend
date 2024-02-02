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

package forms.mappings

import play.api.data.FormError
import play.api.data.format.Formatter

import java.time.LocalDate
import scala.util.{Failure, Success, Try}

private[mappings] class OptionalLocalDateFormatter(
                                                    invalidKey: String,
                                                    twoRequiredKey: String,
                                                    requiredKey: String,
                                                    args: Seq[String] = Seq.empty
                                                  ) extends Formatter[Option[LocalDate]] with Formatters {

  private val fieldKeys: List[String] = List("day", "month", "year")

  private def toDate(key: String, day: Int, month: Int, year: Int): Either[Seq[FormError], Option[LocalDate]] =
    Try(LocalDate.of(year, month, day)) match {
      case Success(date) =>
        Right(Some(date))
      case Failure(_) =>
        Left(Seq(FormError(key, invalidKey, args)))
    }

  private def formatDate(key: String, data: Map[String, String]): Either[Seq[FormError], Option[LocalDate]] = {

    val int = intFormatter(
      requiredKey = invalidKey,
      wholeNumberKey = invalidKey,
      nonNumericKey = invalidKey,
      args
    )

    val trimmedData = data.map { case (k, v) => k -> v.trim }

    for {
      day   <- int.bind(s"$key.day", trimmedData)
      month <- int.bind(s"$key.month", trimmedData)
      year  <- int.bind(s"$key.year", trimmedData)
      date  <- toDate(key, day, month, year)
    } yield date
  }

  override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Option[LocalDate]] = {

    val fields = fieldKeys.map {
      field =>
        field -> data.get(s"$key.$field").filter(_.nonEmpty)
    }.toMap

    lazy val missingFields = fields
      .withFilter(_._2.isEmpty)
      .map(_._1)
      .toList

    fields.count(_._2.isDefined) match {
      case 3 =>
        formatDate(key, data).left.map {
          _.map(_.copy(key = key, args = args))
        }
      case 2 =>
        Left(List(FormError(key, requiredKey, missingFields ++ args)))
      case 1 =>
        Left(List(FormError(key, twoRequiredKey, missingFields ++ args)))
      case _ =>
        Right(None)
    }
  }

  override def unbind(key: String, value: Option[LocalDate]): Map[String, String] =
    Map(
      s"$key.day"   -> value.map(_.getDayOfMonth.toString).getOrElse(""),
      s"$key.month" -> value.map(_.getMonthValue.toString).getOrElse(""),
      s"$key.year"  -> value.map(_.getYear.toString).getOrElse("")
    )
}