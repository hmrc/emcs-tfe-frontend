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

package repositories

import base.SpecBase
import models.messages.MessageCache
import models.response.emcsTfe.messages.Message
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.IntegrationPatience
import uk.gov.hmrc.mongo.test.{CleanMongoCollectionSupport, PlayMongoRepositorySupport}

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext

class MessageInboxRepositorySpec extends SpecBase
  with PlayMongoRepositorySupport[MessageCache]
  with CleanMongoCollectionSupport
  with IntegrationPatience
  with BeforeAndAfterAll {

  implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  lazy val repository: MessageInboxRepositoryImpl = new MessageInboxRepositoryImpl(
    mongoComponent = mongoComponent,
    appConfig = appConfig
  )

  val message = MessageCache(
    ern = testErn,
    message = Message(
      uniqueMessageIdentifier = 2,
      dateCreatedOnCore = LocalDateTime.now(),
      arc = Some("ARC123"),
      messageType = "IE818",
      relatedMessageType = None,
      sequenceNumber = Some(1),
      readIndicator = false,
      lrn = Some("LRN123"),
      messageRole = 0,
      submittedByRequestingTrader = true
    )
  )

  override protected def afterAll(): Unit = {
    super.afterAll()
    await(repository.collection.drop().toFuture())
  }

  ".get" should {
    "return None when the repository is empty" in {
      repository.get("ern", 1).futureValue mustBe None
    }

    "return the correct record from the repository" in {
      repository.set(message).futureValue mustBe true

      val response = repository.get(testErn, 2).futureValue
      response.isDefined mustBe true
      response.get.message mustBe message.message
      response.get.lastUpdated mustNot be(message.lastUpdated)
    }
  }

  ".set" should {
    "populate the repository correctly" in {
      repository.set(message).futureValue mustBe true

      val response = repository.get(testErn, 2).futureValue
      response.isDefined mustBe true
      response.get.message mustBe message.message
      response.get.lastUpdated mustNot be(message.lastUpdated)
    }
  }


}
