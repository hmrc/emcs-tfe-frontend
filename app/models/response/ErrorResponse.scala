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

package models.response

import scala.util.control.NoStackTrace

sealed trait ErrorResponse {
  val message: String
}

case object UnexpectedDownstreamResponseError extends ErrorResponse {
  val message = "Unexpected downstream response status"
}

case object NoContentError extends ErrorResponse {
  val message: String = "No content found"
}

case object NotFoundError extends ErrorResponse {
  val message = "No data found"
}

case object JsonValidationError extends ErrorResponse {
  val message = "JSON validation error"
}

object ErrorConstants {
  val NOT_FOUND_MESSAGE = "No data found for requested search data"
}

case class ExciseProductCodesException(message: String) extends Exception(message) with NoStackTrace with ErrorResponse

case class MemberStatesException(message: String) extends Exception(message) with NoStackTrace with ErrorResponse

case class TraderKnownFactsException(message: String) extends Exception(message) with NoStackTrace with ErrorResponse

case class InvalidUserTypeException(message: String) extends Exception(message) with NoStackTrace with ErrorResponse

case class CnCodeInformationException(message: String) extends Exception(message) with NoStackTrace with ErrorResponse

case class PackagingTypesException(message: String) extends Exception(message) with NoStackTrace with ErrorResponse

case class MovementException(message: String) extends Exception(message) with NoStackTrace with ErrorResponse

case class MovementHistoryEventsException(message: String) extends Exception(message) with NoStackTrace with ErrorResponse

case class WineOperationsException(message: String) extends Exception(message) with NoStackTrace with ErrorResponse

case class MessageStatisticsException(message: String) extends Exception(message) with NoStackTrace with ErrorResponse

case class MessagesException(message: String) extends Exception(message) with NoStackTrace with ErrorResponse

case class DocumentTypesException(message: String) extends Exception(message) with NoStackTrace with ErrorResponse

case class MessageRetrievalException(message: String) extends Exception(message) with NoStackTrace with ErrorResponse

case class DeleteMessageException(message: String) extends Exception(message) with NoStackTrace with ErrorResponse

case class PrevalidateTraderException(message: String) extends Exception(message) with NoStackTrace with ErrorResponse

case class DraftTemplatesListException(message: String) extends Exception(message) with NoStackTrace with ErrorResponse

case class DraftTemplateGetException(message: String) extends Exception(message) with NoStackTrace with ErrorResponse

case class DraftTemplateCheckNameException(message: String) extends Exception(message) with NoStackTrace with ErrorResponse

case class DraftTemplateSetException(message: String) extends Exception(message) with NoStackTrace with ErrorResponse

case class DeleteTemplateException(message: String) extends Exception(message) with NoStackTrace with ErrorResponse

case class CreateDraftMovementException(message: String) extends Exception(message) with NoStackTrace with ErrorResponse
