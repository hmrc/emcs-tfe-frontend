package mocks.services

import models.response.emcsTfe.messages.DeleteMessageResponse
import org.scalamock.handlers.CallHandler3
import org.scalamock.scalatest.MockFactory
import services.DeleteMessageService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

trait MockDeleteMessageService extends MockFactory {

  lazy val mockDeleteMessagesService: DeleteMessageService = mock[DeleteMessageService]

  object MockDeleteMessagesService {
    def deleteMessage(exciseRegistrationNumber: String,
                      uniqueMessageIdentifier: Long): CallHandler3[String, Long, HeaderCarrier, Future[DeleteMessageResponse]] =
      (mockDeleteMessagesService.deleteMessage(_: String, _: Long)(_: HeaderCarrier))
        .expects(exciseRegistrationNumber, uniqueMessageIdentifier, *)
  }

}
