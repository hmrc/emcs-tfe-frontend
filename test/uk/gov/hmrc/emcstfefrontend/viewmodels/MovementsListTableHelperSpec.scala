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

package uk.gov.hmrc.emcstfefrontend.viewmodels

import play.api.i18n.MessagesApi
import uk.gov.hmrc.emcstfefrontend.fixtures.MovementListFixtures
import uk.gov.hmrc.emcstfefrontend.fixtures.messages.ViewMovementListMessages
import uk.gov.hmrc.emcstfefrontend.support.UnitSpec
import uk.gov.hmrc.emcstfefrontend.utils.DateUtils
import uk.gov.hmrc.emcstfefrontend.views.html.components.link
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{HeadCell, Table, TableRow}

class MovementsListTableHelperSpec extends UnitSpec with MovementListFixtures with DateUtils {

  lazy val messagesApi = app.injector.instanceOf[MessagesApi]
  lazy val link = app.injector.instanceOf[link]
  lazy val helper: MovementsListTableHelper = new MovementsListTableHelper(link)
  lazy val movements = getMovementListResponse.movements

  "MovementsListTableHelper" when {

    Seq(ViewMovementListMessages.English, ViewMovementListMessages.Welsh) foreach { viewMessages =>

      implicit val messages = messagesApi.preferred(Seq(viewMessages.lang))

      s"rendering for '${viewMessages.lang.code}' language code" when {

        "calling .headerRow" must {

          "construct the correct heading row" in {

            helper.headerRow shouldBe Some(Seq(
              HeadCell(Text(viewMessages.tableArc)),
              HeadCell(Text(viewMessages.tableConsignor))
            ))
          }
        }

        "calling .dataRows(ern: String, movements: Seq[GetMovementListItem])" must {

          "construct the expected data rows" in {

            helper.dataRows(testErn, movements) shouldBe Seq(
              Seq(
                TableRow(
                  content = HtmlContent(link(
                    link = movement1.viewMovementUrl(testErn).url,
                    messageKey = movement1.arc
                  ))
                ),
                TableRow(
                  content = Text(movement1.otherTraderID)
                )
              ),
              Seq(
                TableRow(
                  content = HtmlContent(link(
                    link = movement2.viewMovementUrl(testErn).url,
                    messageKey = movement2.arc
                  ))
                ),
                TableRow(
                  content = Text(movement1.otherTraderID)
                )
              )
            )
          }
        }

        "calling .constructTable(ern: String, movements: Seq[GetMovementListItem])" must {

          "construct the expected data rows" in {

            helper.constructTable(testErn, movements) shouldBe Table(
              firstCellIsHeader = true,
              rows = Seq(
                Seq(
                  TableRow(
                    content = HtmlContent(link(
                      link = movement1.viewMovementUrl(testErn).url,
                      messageKey = movement1.arc
                    ))
                  ),
                  TableRow(
                    content = Text(movement1.otherTraderID)
                  )
                ),
                Seq(
                  TableRow(
                    content = HtmlContent(link(
                      link = movement2.viewMovementUrl(testErn).url,
                      messageKey = movement2.arc
                    ))
                  ),
                  TableRow(
                    content = Text(movement1.otherTraderID)
                  )
                )
              ),
              head = Some(Seq(
                HeadCell(Text(viewMessages.tableArc)),
                HeadCell(Text(viewMessages.tableConsignor))
              ))
            )
          }
        }
      }
    }
  }
}
