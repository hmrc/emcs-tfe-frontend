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

package services


import connectors.emcsTfe.DeleteMessageConnector
import models.response.DeleteMessageException
import models.response.emcsTfe.messages.DeleteMessageResponse
import repositories.MessageInboxRepository
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DeleteMessageService @Inject()(deleteMessageConnector: DeleteMessageConnector,
                                     messageInboxRepository: MessageInboxRepository)
                                    (implicit ec: ExecutionContext) {

  def deleteMessage(exciseRegistrationNumber: String, uniqueMessageIdentifier: Long)(implicit hc: HeaderCarrier): Future[DeleteMessageResponse] = {

    deleteMessageConnector.deleteMessage(exciseRegistrationNumber, uniqueMessageIdentifier).map {
      case Right(recordsAffected) => {

        messageInboxRepository.delete(exciseRegistrationNumber, uniqueMessageIdentifier).map(_ => {
          // TODO what to do here???
          ???
        })
      }
      case Left(errorResponse) =>
        throw DeleteMessageException(s"Error deleting message $uniqueMessageIdentifier for trader $exciseRegistrationNumber: ${errorResponse.message}")

    }

    ???
  }

}
