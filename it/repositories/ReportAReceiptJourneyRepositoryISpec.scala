package repositories

import config.AppConfig
import models.UserAnswers
import org.mongodb.scala.model.Filters
import org.scalamock.scalatest.MockFactory
import play.api.libs.json.Json
import support.IntegrationBaseSpec
import uk.gov.hmrc.mongo.test.PlayMongoRepositorySupport

import java.time.temporal.ChronoUnit
import java.time.{Clock, Instant, ZoneId}
import scala.concurrent.ExecutionContext

class ReportAReceiptJourneyRepositoryISpec extends PlayMongoRepositorySupport[UserAnswers] with IntegrationBaseSpec with MockFactory {

  private implicit val ec = app.injector.instanceOf[ExecutionContext]
  private val appConfig = app.injector.instanceOf[AppConfig]

  private val instant = Instant.now.truncatedTo(ChronoUnit.MILLIS)
  private val stubClock: Clock = Clock.fixed(instant, ZoneId.systemDefault)

  private val userAnswers = UserAnswers("id", Json.obj("foo" -> "bar"), Instant.ofEpochSecond(1))

  protected override val repository = new ReportAReceiptJourneyRepository(
    mongoComponent = mongoComponent,
    appConfig      = appConfig,
    clock          = stubClock
  )

  override def beforeEach(): Unit = {
    super.beforeEach()
    deleteAll().futureValue
  }

  ".set" must {

    "set the last updated time on the supplied user answers to `now`, and save them" in {

      val expectedResult = userAnswers copy (lastUpdated = instant)

      val setResult     = repository.set(userAnswers).futureValue
      val updatedRecord = find(Filters.equal("_id", userAnswers.id)).futureValue

      setResult shouldEqual true
      updatedRecord shouldEqual Seq(expectedResult)
    }
  }

  ".get" when {

    "there is a record for this id" must {

      "update the lastUpdated time and get the record" in {

        insert(userAnswers).futureValue

        val result         = repository.get(userAnswers.id).futureValue
        val expectedResult = userAnswers copy (lastUpdated = instant)

        result shouldEqual Some(expectedResult)
      }
    }

    "there is no record for this id" must {

      "return None" in {

        repository.get("id that does not exist").futureValue shouldBe None
      }
    }
  }

  ".clear" must {

    "remove a record" in {

      insert(userAnswers).futureValue

      val result = repository.clear(userAnswers.id).futureValue

      result shouldEqual true
      repository.get(userAnswers.id).futureValue shouldBe None
    }

    "must return true when there is no record to remove" in {
      val result = repository.clear("id that does not exist").futureValue

      result shouldEqual true
    }
  }

  ".keepAlive" should {

    "when there is a record for this id" should {

      "must update its lastUpdated to `now` and return true" in {

        insert(userAnswers).futureValue

        val result = repository.keepAlive(userAnswers.id).futureValue

        val expectedUpdatedAnswers = userAnswers copy (lastUpdated = instant)

        result shouldEqual true
        val updatedAnswers = find(Filters.equal("_id", userAnswers.id)).futureValue
        updatedAnswers shouldEqual Seq(expectedUpdatedAnswers)
      }
    }

    "when there is no record for this id" should {

      "must return true" in {

        repository.keepAlive("id that does not exist").futureValue shouldEqual true
      }
    }
  }
}
