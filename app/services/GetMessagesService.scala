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

package services

import connectors.emcsTfe.{GetMessagesConnector, MarkMessageAsReadConnector}
import models.messages.{MessageCache, MessagesSearchOptions}
import models.response.MessagesException
import models.response.emcsTfe.messages.GetMessagesResponse
import repositories.MessageInboxRepository
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetMessagesService @Inject()(
                                    messagesConnector: GetMessagesConnector,
                                    markMessageAsReadConnector: MarkMessageAsReadConnector,
                                    messageInboxRepository: MessageInboxRepository
                                  )(implicit ec: ExecutionContext) {

  def getMessages(ern: String, search: Option[MessagesSearchOptions])(implicit hc: HeaderCarrier): Future[GetMessagesResponse] = {
    messagesConnector.getMessages(ern, search).map {
      case Right(messages) =>
        messages.messagesData.messages.foreach { message =>
          messageInboxRepository.set(MessageCache(ern, message))
        }
        messages
      case _ => throw MessagesException(s"Error occurred when fetching messages for trader $ern")
    }
  }

  def getMessage(ern: String, uniqueMessageIdentifier: Long)(implicit hc: HeaderCarrier): Future[Option[MessageCache]] = {

    messageInboxRepository.get(ern, uniqueMessageIdentifier).map {
      case Some(message) =>
        markMessageAsReadConnector.markMessageAsRead(ern, uniqueMessageIdentifier)
        Some(message)
      case _ =>
        None
    }

  }

}
