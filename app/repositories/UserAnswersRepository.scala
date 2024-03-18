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

package repositories

import config.AppConfig
import models.UserAnswers
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model._
import play.api.libs.json.Format
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats
import uk.gov.hmrc.play.http.logging.Mdc
import utils.TimeMachine

import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserAnswersRepositoryImpl @Inject()(implicit mongoComponent: MongoComponent,
                                         appConfig: AppConfig,
                                         time: TimeMachine,
                                         ec: ExecutionContext)
  extends PlayMongoRepository[UserAnswers](
    collectionName = "user-answers",
    mongoComponent = mongoComponent,
    domainFormat = UserAnswers.format,
    indexes = Seq(
      IndexModel(
        Indexes.ascending(UserAnswers.lastUpdatedKey),
        IndexOptions()
          .name("lastUpdatedIdx")
          .expireAfter(appConfig.userAnswersCacheTtl.toSeconds, TimeUnit.SECONDS)
      ),
      IndexModel(
        Indexes.ascending(UserAnswers.ernKey),
        IndexOptions().name("uniqueIdx")
      )
    ),
    replaceIndexes = appConfig.userAnswersReplaceIndexes
  ) with UserAnswersRepository {

  implicit val instantFormat: Format[Instant] = MongoJavatimeFormats.instantFormat

  private def by(ern: String): Bson =
    Filters.equal(UserAnswers.ernKey, ern)

  def keepAlive(ern: String): Future[Boolean] =
    collection
      .updateOne(
        filter = by(ern),
        update = Updates.set("lastUpdated", time.instant()),
      )
      .toFuture()
      .map(_ => true)

  def get(ern: String): Future[Option[UserAnswers]] =
    Mdc.preservingMdc(
      collection.findOneAndUpdate(
        filter = by(ern),
        update = Updates.set("lastUpdated", time.instant()),
        options = FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
      ).headOption()
    )

  def set(answers: UserAnswers): Future[Boolean] = {

    val updatedAnswers = answers copy (lastUpdated = time.instant())

    Mdc.preservingMdc(
      collection
        .replaceOne(
          filter = by(updatedAnswers.ern),
          replacement = updatedAnswers,
          options = ReplaceOptions().upsert(true)
        )
        .toFuture()
        .map(_ => true)
    )
  }

  def remove(ern: String): Future[Boolean] =
    collection
      .deleteOne(by(ern))
      .toFuture()
      .map(_ => true)
}

trait UserAnswersRepository {
  def keepAlive(ern: String): Future[Boolean]

  def get(ern: String): Future[Option[UserAnswers]]

  def set(answers: UserAnswers): Future[Boolean]

  def remove(ern: String): Future[Boolean]
}
