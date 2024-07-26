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

import config.AppConfig
import models.messages.MessageStatisticsCache
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model._
import play.api.libs.json.Format
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats
import utils.Logging

import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class MessageStatisticsRepositoryImpl @Inject()(
                                            mongoComponent: MongoComponent,
                                            appConfig: AppConfig
                                          )(implicit ec: ExecutionContext)
  extends PlayMongoRepository[MessageStatisticsCache](
    collectionName = "message-statistics",
    mongoComponent = mongoComponent,
    domainFormat = MessageStatisticsCache.format,
    indexes = Seq(
      IndexModel(
        Indexes.ascending("lastUpdated"),
        IndexOptions()
          .name("lastUpdatedIdx")
          .expireAfter(appConfig.messageStatisticsCacheTtl.toSeconds, TimeUnit.SECONDS)
      ),
      IndexModel(
        Indexes.ascending("ern"),
        IndexOptions().name("uniqueIdx")
      )
    ),
    replaceIndexes = appConfig.messageStatisticsReplaceIndexes()
  ) with MessageStatisticsRepository with Logging {

  implicit val instantFormat: Format[Instant] = MongoJavatimeFormats.instantFormat

  private def by(ern: String): Bson = Filters.equal("ern", ern)

  def set(stats: MessageStatisticsCache): Future[Boolean] =
    collection
      .replaceOne(
        filter = by(stats.ern),
        replacement = stats copy (lastUpdated = Instant.now),
        options = ReplaceOptions().upsert(true)
      )
      .toFuture()
      .map(_ => true)
      .recover { _ =>
        logger.warn("[set] Failed to store message statistics, returning true to allow processing to continue regardless")
        true
      }

  def get(ern: String): Future[Option[MessageStatisticsCache]] =
    collection.find(by(ern)).headOption().recover { _ =>
      logger.warn("[get] Failed to get message statistics, returning None to allow processing to continue regardless")
      None
    }
}

trait MessageStatisticsRepository {
  def set(stats: MessageStatisticsCache): Future[Boolean]
  def get(ern: String): Future[Option[MessageStatisticsCache]]
}
