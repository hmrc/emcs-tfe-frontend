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
import models.messages.MessageCache
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model._
import play.api.libs.json.Format
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class MessageInboxRepositoryImpl @Inject()(
                                        mongoComponent: MongoComponent,
                                        appConfig: AppConfig
                                      )(implicit ec: ExecutionContext)
  extends PlayMongoRepository[MessageCache](
    collectionName = "messages",
    mongoComponent = mongoComponent,
    domainFormat = MessageCache.format,
    indexes = Seq(
      IndexModel(
        Indexes.ascending("lastUpdated"),
        IndexOptions()
          .name("lastUpdatedIdx")
          .expireAfter(appConfig.messagesCacheTtl.toSeconds, TimeUnit.SECONDS)
      ),
      IndexModel(
        Indexes.compoundIndex(
          Indexes.ascending("ern"),
          Indexes.ascending("message.uniqueMessageIdentifier")
        ),
        IndexOptions()
          .name("uniqueIdx")
      )
    ),
    replaceIndexes = appConfig.messagesReplaceIndexes()
  ) with MessageInboxRepository {

  implicit val instantFormat: Format[Instant] = MongoJavatimeFormats.instantFormat

  private def by(ern: String, uniqueMessageIdentifier: Long): Bson =
    Filters.and(
      Filters.equal("ern", ern),
      Filters.equal("message.uniqueMessageIdentifier", uniqueMessageIdentifier)
    )

  def set(message: MessageCache): Future[Boolean] = {

    val updatedMessage = message copy (lastUpdated = Instant.now)

    collection
      .replaceOne(
        filter = by(message.ern, message.message.uniqueMessageIdentifier),
        replacement = updatedMessage,
        options = ReplaceOptions().upsert(true)
      )
      .toFuture()
      .map(_ => true)
  }

  private def keepAlive(ern: String, uniqueMessageIdentifier: Long): Future[Boolean] =
    collection
      .updateOne(
        filter = by(ern, uniqueMessageIdentifier),
        update = Updates.set("lastUpdated", Instant.now)
      )
      .toFuture()
      .map(_ => true)

  def get(ern: String, uniqueMessageIdentifier: Long): Future[Option[MessageCache]] =
    keepAlive(ern, uniqueMessageIdentifier).flatMap {
      _ =>
        collection
          .find(by(ern, uniqueMessageIdentifier))
          .headOption()
    }

  // TODO confirm delete method and method return type
  def delete(ern: String, uniqueMessageIdentifier: Long): Future[Option[MessageCache]] =
    keepAlive(ern, uniqueMessageIdentifier).flatMap(_ => {
      collection
        .findOneAndDelete(by(ern, uniqueMessageIdentifier))
        .headOption()
    })
}

trait MessageInboxRepository {
  def set(aMessage: MessageCache): Future[Boolean]

  def get(ern: String, uniqueMessageIdentifier: Long): Future[Option[MessageCache]]

  // TODO confirm delete method and method return type
  def delete(ern: String, uniqueMessageIdentifier: Long): Future[Option[MessageCache]]
}
