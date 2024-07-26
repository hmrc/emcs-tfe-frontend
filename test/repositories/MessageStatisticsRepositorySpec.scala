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
import fixtures.GetSubmissionFailureMessageFixtures
import models.messages.MessageStatisticsCache
import models.response.emcsTfe.GetMessageStatisticsResponse
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.IntegrationPatience
import uk.gov.hmrc.mongo.test.{CleanMongoCollectionSupport, PlayMongoRepositorySupport}

import scala.concurrent.ExecutionContext

class MessageStatisticsRepositorySpec extends SpecBase
  with PlayMongoRepositorySupport[MessageStatisticsCache]
  with CleanMongoCollectionSupport
  with IntegrationPatience
  with BeforeAndAfterAll
  with GetSubmissionFailureMessageFixtures {

  implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  lazy val repository: MessageStatisticsRepositoryImpl = new MessageStatisticsRepositoryImpl(
    mongoComponent = mongoComponent,
    appConfig = appConfig
  )

  val stats = MessageStatisticsCache(testErn, GetMessageStatisticsResponse(1,1))

  override protected def afterAll(): Unit = {
    super.afterAll()
    await(repository.collection.drop().toFuture())
  }

  ".get" should {
    "return None when the repository is empty" in {
      repository.get("ern").futureValue mustBe None
    }

    "return the correct record from the repository" in {
      repository.set(stats).futureValue mustBe true

      val response = repository.get(testErn).futureValue
      response.isDefined mustBe true
      response.get.statistics mustBe stats.statistics
    }
  }

  ".set" should {
    "populate the repository correctly AND update the lastUpdated timestamp" in {
      repository.set(stats).futureValue mustBe true

      val response = repository.get(testErn).futureValue
      response.isDefined mustBe true
      response.get.statistics mustBe stats.statistics
      response.get.lastUpdated mustNot be(stats.lastUpdated)
    }
  }
}
