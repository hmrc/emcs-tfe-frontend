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
import models.auth.UserRequest
import models.messages.MessagesSearchOptions
import models.response.MessageStatisticsException
import models.response.emcsTfe.GetMessageStatisticsResponse
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetMessageStatisticsService @Inject()(connector: GetMessageStatisticsConnector,
                                            override val config: AppConfig
                                           )(implicit ec: ExecutionContext) extends FeatureSwitching {

  def getMessageStatistics(ern: String)(implicit hc: HeaderCarrier, request: UserRequest[_]): Future[Option[GetMessageStatisticsResponse]] = {

    val isOnMessagesPage = request.request.path.contains(
      routes.ViewAllMessagesController.onPageLoad(request.ern, MessagesSearchOptions()).path().split("\\?").head
    )

    if(config.messageStatisticsNotificationEnabled || isOnMessagesPage) {
      connector.getMessageStatistics(ern).map {
        case Right(messageStatistics) => Some(messageStatistics)
        case _ => throw MessageStatisticsException(s"No message statistics found for trader $ern")
      }
    } else {
      Future.successful(None)
    }
  }

}
