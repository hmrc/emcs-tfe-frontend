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

import config.AppConfig
import connectors.emcsTfe.GetMessageStatisticsConnector
import controllers.messages.routes
import featureswitch.core.config.FeatureSwitching
import models.messages.{MessageStatisticsCache, MessagesSearchOptions}
import models.response.emcsTfe.GetMessageStatisticsResponse
import play.api.mvc.Request
import repositories.MessageStatisticsRepository
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetMessageStatisticsService @Inject()(connector: GetMessageStatisticsConnector,
                                            messageStatisticsRepository: MessageStatisticsRepository,
                                            override val config: AppConfig
                                           )(implicit ec: ExecutionContext) extends FeatureSwitching with Logging {

  def getMessageStatistics(ern: String)(implicit hc: HeaderCarrier, request: Request[_]): Future[Option[GetMessageStatisticsResponse]] = {

    val isOnMessagesPage = request.path.contains(
      routes.ViewAllMessagesController.onPageLoad(ern, MessagesSearchOptions()).path().split("\\?").head
    )

    if(config.messageStatisticsNotificationEnabled || isOnMessagesPage) {
      if(isOnMessagesPage) retrieveFromCore(ern) else retrieveFromCache(ern)
    } else {
      Future.successful(None)
    }
  }

  private def retrieveFromCache(ern: String)(implicit hc: HeaderCarrier): Future[Option[GetMessageStatisticsResponse]] =
    messageStatisticsRepository.get(ern).flatMap {
      case Some(cacheValue) => Future.successful(Some(cacheValue.statistics))
      case _ => retrieveFromCore(ern)
    }

  private def retrieveFromCore(ern: String)(implicit hc: HeaderCarrier): Future[Option[GetMessageStatisticsResponse]] =
    connector.getMessageStatistics(ern).map {
      case Right(messageStatistics) =>
        //Intentional non-blocking async storage - as if fails, processing can continue anyway
        messageStatisticsRepository.set(MessageStatisticsCache(ern, messageStatistics))
        Some(messageStatistics)
      case _ =>
        logger.warn(s"[getMessageStatistics] No message statistics found for trader $ern")
        None
    }
}
