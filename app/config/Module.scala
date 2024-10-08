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

package config

import com.google.inject.AbstractModule
import connectors.referenceData._
import controllers.predicates._
import repositories._
import utils.{TimeMachine, TimeMachineImpl}

class Module extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[DataRetrievalAction]).to(classOf[DataRetrievalActionImpl]).asEagerSingleton()
    bind(classOf[AuthAction]).to(classOf[AuthActionImpl])
    bind(classOf[SelectExciseNumberAuthAction]).to(classOf[SelectExciseNumberAuthActionImpl])
    bind(classOf[GetExciseProductCodesConnector]).to(classOf[GetExciseProductCodesConnectorImpl]).asEagerSingleton()
    bind(classOf[GetMemberStatesConnector]).to(classOf[GetMemberStatesConnectorImpl]).asEagerSingleton()
    bind(classOf[GetCnCodeInformationConnector]).to(classOf[GetCnCodeInformationConnectorImpl]).asEagerSingleton()
    bind(classOf[GetItemPackagingTypesConnector]).to(classOf[GetItemPackagingTypesConnectorImpl]).asEagerSingleton()
    bind(classOf[GetDocumentTypesConnector]).to(classOf[GetDocumentTypesConnectorImpl]).asEagerSingleton()
    bind(classOf[MessageInboxRepository]).to(classOf[MessageInboxRepositoryImpl]).asEagerSingleton()
    bind(classOf[MessageStatisticsRepository]).to(classOf[MessageStatisticsRepositoryImpl]).asEagerSingleton()
    bind(classOf[TimeMachine]).to(classOf[TimeMachineImpl]).asEagerSingleton()
    bind(classOf[PrevalidateTraderUserAnswersRepository]).asEagerSingleton()
  }
}
