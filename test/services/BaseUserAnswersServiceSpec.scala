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

import base.SpecBase
import models.UserAnswers
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.IntegrationPatience
import play.api.libs.json.Json
import repositories.BaseUserAnswersRepository
import uk.gov.hmrc.mongo.test.{CleanMongoCollectionSupport, PlayMongoRepositorySupport}
import utils.TimeMachine

import java.time.Instant
import java.time.temporal.ChronoUnit
import scala.concurrent.ExecutionContext

trait BaseUserAnswersServiceSpec extends SpecBase
  with PlayMongoRepositorySupport[UserAnswers]
  with CleanMongoCollectionSupport
  with IntegrationPatience
  with BeforeAndAfterAll {

  val service: BaseUserAnswersService
  val repository: BaseUserAnswersRepository

  implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  val instantNow = Instant.now.truncatedTo(ChronoUnit.MILLIS)
  val userAnswers = UserAnswers(testErn, Json.obj("foo" -> "bar"), Instant.ofEpochSecond(1))
  val timeMachine: TimeMachine = () => instantNow

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    await(repository.collection.drop().toFuture())
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    await(repository.collection.drop().toFuture())
  }

  ".set" must {

    "set the last updated time on the supplied user answers to `now`, and save them" in {
      val expectedResult = userAnswers copy (lastUpdated = instantNow)
      val setResult = service.set(userAnswers).futureValue

      setResult mustBe expectedResult
    }
  }

  ".get" when {

    "there is a record for this id" must {

      "update the lastUpdated time and get the record" in {
        insert(userAnswers).futureValue

        val result = service.get(userAnswers.ern).futureValue
        val expectedResult = userAnswers copy (lastUpdated = instantNow)

        result.value mustBe expectedResult
      }
    }

    "there is no record for this id" must {

      "return None" in {
        service.get("wrongErn").futureValue mustBe None
      }
    }
  }

  ".remove" must {

    "remove a record" in {
      insert(userAnswers).futureValue

      val result = service.remove(userAnswers).futureValue

      result mustBe true
      service.get(userAnswers.ern).futureValue mustBe None
    }

    "return true when there is no record to remove" in {
      val result = service.remove(userAnswers).futureValue

      result mustBe true
    }
  }
}
