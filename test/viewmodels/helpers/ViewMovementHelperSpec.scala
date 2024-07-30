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

package viewmodels.helpers

import base.SpecBase
import config.AppConfig
import fixtures.GetMovementResponseFixtures
import mocks.services.MockGetDocumentTypesService
import models.DocumentType
import models.MovementEadStatus._
import models.common.DestinationType._
import models.common.{AddressModel, TraderModel}
import models.requests.DataRequest
import org.jsoup.Jsoup
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{Key, SummaryListRow, Text, Value}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.tools.LogCapturing
import utils.Logging
import viewmodels._
import viewmodels.helpers.events.MovementEventHelper
import views.BaseSelectors
import views.html.components.{h2, p, summaryCard}
import views.html.viewMovement.partials.overview_partial

import scala.concurrent.{ExecutionContext, Future}

class ViewMovementHelperSpec extends SpecBase with GetMovementResponseFixtures with LogCapturing with Logging with MockGetDocumentTypesService {

  implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"), ern = getMovementResponseModel.consignorTrader.traderExciseNumber.get)
  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  lazy val p: p = app.injector.instanceOf[p]
  lazy val h2: h2 = app.injector.instanceOf[h2]
  lazy val overview_partial: overview_partial = app.injector.instanceOf[overview_partial]

  val helper: ViewMovementHelper = new ViewMovementHelper(
    p,
    h2,
    overview_partial,
    app.injector.instanceOf[ViewMovementItemsHelper],
    app.injector.instanceOf[ViewMovementTransportHelper],
    app.injector.instanceOf[ViewMovementGuarantorHelper],
    app.injector.instanceOf[ViewMovementOverviewHelper],
    app.injector.instanceOf[ViewMovementDeliveryHelper],
    new ViewMovementDocumentHelper(
      h2,
      app.injector.instanceOf[summaryCard],
      overview_partial,
      p,
      mockGetDocumentTypesService
    ),
    app.injector.instanceOf[ItemDetailsCardHelper],
    app.injector.instanceOf[ItemPackagingCardHelper],
    app.injector.instanceOf[MovementEventHelper],
    appConfig: AppConfig
  )
  implicit lazy val messages: Messages = messages(request)

  object Selectors extends BaseSelectors {

    override def h2(i: Int) = s"h2:nth-of-type($i)"

    override def h3(i: Int) = s"h3:nth-of-type($i)"

    def h4(i: Int) = s"h4:nth-of-type($i)"

    def summaryListAtIndexRowKey(summaryListIndex: Int, rowIndex: Int) = s"dl:nth-of-type($summaryListIndex) div.govuk-summary-list__row:nth-of-type($rowIndex) > dt"

    def summaryListAtIndexRowValue(summaryListIndex: Int, rowIndex: Int) = s"dl:nth-of-type($summaryListIndex) div.govuk-summary-list__row:nth-of-type($rowIndex) > dd"
  }

  "movementCard" should {
    "output the correct section" when {
      Map(
        Overview -> "Overview",
        Movement -> "Movement details",
        Items -> "Item details",
        Delivery -> "Delivery details",
        Transport -> "Transport details",
        Guarantor -> "Guarantor details",
        Documents -> "Document details"
      ).foreach {
        case (subNavigationTab, expectedTitle) =>
          s"the subNavigationTab is $subNavigationTab" in {
            if (subNavigationTab == Documents) {
              MockGetDocumentTypesService.getDocumentTypes().returns(Future.successful(Seq(DocumentType("1", "Document type description"))))
            }
            val result = await(helper.movementCard(Some(subNavigationTab), getMovementResponseModel))
            val doc = Jsoup.parse(result.toString())
            doc.select(Selectors.h2(1)).text() mustBe expectedTitle
          }
      }

      "the subNavigationTab is None" in {
        MockGetDocumentTypesService.getDocumentTypes().returns(Future.successful(Seq(DocumentType("1", "Document type description"))))

        val result = await(helper.movementCard(
          None,
          getMovementResponseModel.copy(
            memberStateCode = Some("GB"),
            dispatchImportOfficeReferenceNumber = Some("imp123"),
            eadEsad = getMovementResponseModel.eadEsad.copy(importSadNumber = Some(Seq("sad123"))),
          )
        ))
        val doc = Jsoup.parse(result.toString())
        doc.select(Selectors.h2(1)).text() mustBe "Movement details"
        doc.select(Selectors.h2(2)).text() mustBe "Delivery details"
        doc.select(Selectors.h2(3)).text() mustBe "Transport details"
        doc.select(Selectors.h2(4)).text() mustBe "Guarantor details"
        doc.select(Selectors.h2(5)).text() mustBe "Exempted organisation"
        doc.select(Selectors.h2(6)).text() mustBe "Export"
        doc.select(Selectors.h2(7)).text() mustBe "Import"
        doc.select(Selectors.h2(8)).text() mustBe "Single Administrative Document(s) (SAD)"
        doc.select(Selectors.h2(9)).text() mustBe "Document details"
        doc.select(Selectors.h2(10)).text() mustBe "Items"
      }
    }
  }

  "constructMovementView" should {
    "output the correct HTML" when {
      "the date of arrival is set" in {
        val result = helper.constructMovementView(getMovementResponseModel)
        val doc = Jsoup.parse(result.toString())
        doc.select(Selectors.h2(1)).text() mustBe "Movement details"
        doc.select(Selectors.summaryListAtIndexRowKey(1, 1)).text() mustBe "LRN"
        doc.select(Selectors.summaryListAtIndexRowValue(1, 1)).text() mustBe testLrn
        doc.select(Selectors.summaryListAtIndexRowKey(1, 2)).text() mustBe "eAD status"
        doc.select(Selectors.summaryListAtIndexRowValue(1, 2)).text() mustBe "Accepted An eAD has been created and the movement may be in transit."
        doc.select(Selectors.summaryListAtIndexRowKey(1, 3)).text() mustBe "Receipt status"
        doc.select(Selectors.summaryListAtIndexRowValue(1, 3)).text() mustBe "Accepted and unsatisfactory"
        doc.select(Selectors.summaryListAtIndexRowKey(1, 4)).text() mustBe "Movement type"
        doc.select(Selectors.summaryListAtIndexRowValue(1, 4)).text() mustBe "Import for Tax warehouse in Great Britain"
        doc.select(Selectors.summaryListAtIndexRowKey(1, 5)).text() mustBe "Movement direction"
        doc.select(Selectors.summaryListAtIndexRowValue(1, 5)).text() mustBe "Outbound"

        doc.select(Selectors.h3(1)).text() mustBe "Time and date"
        doc.select(Selectors.summaryListAtIndexRowKey(2, 1)).text() mustBe "Date of dispatch"
        doc.select(Selectors.summaryListAtIndexRowValue(2, 1)).text() mustBe "20 November 2008"
        doc.select(Selectors.summaryListAtIndexRowKey(2, 2)).text() mustBe "Time of dispatch"
        doc.select(Selectors.summaryListAtIndexRowValue(2, 2)).text().toLowerCase mustBe "12:00 am"
        doc.select(Selectors.summaryListAtIndexRowKey(2, 3)).text() mustBe "Date of arrival"
        doc.select(Selectors.summaryListAtIndexRowValue(2, 3)).text() mustBe "8 December 2008"

        doc.select(Selectors.h3(2)).text() mustBe "Invoice"
        doc.select(Selectors.summaryListAtIndexRowKey(3, 1)).text() mustBe "Invoice reference"
        doc.select(Selectors.summaryListAtIndexRowValue(3, 1)).text() mustBe "INV123"
        doc.select(Selectors.summaryListAtIndexRowKey(3, 2)).text() mustBe "Invoice date of issue"
        doc.select(Selectors.summaryListAtIndexRowValue(3, 2)).text() mustBe "1 December 2023"
      }

      "the date of arrival is NOT set (calculating the predicted date of arrival) - not showing the receipt status" in {
        val result = helper.constructMovementView(getMovementResponseModel.copy(reportOfReceipt = None))
        val doc = Jsoup.parse(result.toString())
        doc.select(Selectors.h2(1)).text() mustBe "Movement details"
        doc.select(Selectors.summaryListAtIndexRowKey(1, 1)).text() mustBe "LRN"
        doc.select(Selectors.summaryListAtIndexRowValue(1, 1)).text() mustBe testLrn
        doc.select(Selectors.summaryListAtIndexRowKey(1, 2)).text() mustBe "eAD status"
        doc.select(Selectors.summaryListAtIndexRowValue(1, 2)).text() mustBe "Accepted An eAD has been created and the movement may be in transit."
        doc.select(Selectors.summaryListAtIndexRowKey(1, 3)).text() mustBe "Movement type"
        doc.select(Selectors.summaryListAtIndexRowValue(1, 3)).text() mustBe "Import for Tax warehouse in Great Britain"
        doc.select(Selectors.summaryListAtIndexRowKey(1, 4)).text() mustBe "Movement direction"
        doc.select(Selectors.summaryListAtIndexRowValue(1, 4)).text() mustBe "Outbound"

        doc.select(Selectors.h3(1)).text() mustBe "Time and date"
        doc.select(Selectors.summaryListAtIndexRowKey(2, 1)).text() mustBe "Date of dispatch"
        doc.select(Selectors.summaryListAtIndexRowValue(2, 1)).text() mustBe "20 November 2008"
        doc.select(Selectors.summaryListAtIndexRowKey(2, 2)).text() mustBe "Time of dispatch"
        doc.select(Selectors.summaryListAtIndexRowValue(2, 2)).text().toLowerCase mustBe "12:00 am"
        doc.select(Selectors.summaryListAtIndexRowKey(2, 3)).text() mustBe "Predicted arrival"
        doc.select(Selectors.summaryListAtIndexRowValue(2, 3)).text() mustBe "10 December 2008"

        doc.select(Selectors.h3(2)).text() mustBe "Invoice"
        doc.select(Selectors.summaryListAtIndexRowKey(3, 1)).text() mustBe "Invoice reference"
        doc.select(Selectors.summaryListAtIndexRowValue(3, 1)).text() mustBe "INV123"
        doc.select(Selectors.summaryListAtIndexRowKey(3, 2)).text() mustBe "Invoice date of issue"
        doc.select(Selectors.summaryListAtIndexRowValue(3, 2)).text() mustBe "1 December 2023"
      }

      Seq(Accepted -> "An eAD has been created and the movement may be in transit.",
        Cancelled -> "The consignor has cancelled the movement before the date of dispatch on the eAD.",
        Delivered -> "The consignee has accepted the goods and the movement has been closed.",
        Diverted -> "The consignor has successfully submitted a change of destination before the goods have been received or in response to the consignee’s complete refusal of the goods.",
        ManuallyClosed -> "The member state of the consignor has manually closed the movement due to a technical problem preventing a report of receipt or because the consignee has gone out of business.",
        Refused -> "The consignee has refused all goods in the movement and the consignor must create a change of destination.",
        NoneStatus -> "",
        PartiallyRefused -> "The consignee has refused some goods in the movement and the consignor must create a change of destination.",
        Exporting -> "The movement has been accepted for export by customs.",
        DeemedExported -> "The movement has been approved for export by customs, but this status does not confirm that the goods have been exported (the report of receipt will provide confirmation).",
        Replaced -> "The movement of energy products has been split into two or more parts (up to a maximum of 9 parts).",
        Stopped -> "Officials have seized the movement because of an incident or irregularity.",
        Rejected -> "The consignee has rejected the movement and the consignor must now submit a change of destination if the goods are in transit or cancel the movement if the goods have not departed."
      ).foreach { statusToExplanation =>

        s"when the eAD status is ${statusToExplanation._1} - show the correct status and explanation" in {
          val result = helper.constructMovementView(getMovementResponseModel.copy(eadStatus = statusToExplanation._1))
          val doc = Jsoup.parse(result.toString())
          if (statusToExplanation._1 == NoneStatus) {
            doc.select(Selectors.summaryListAtIndexRowKey(1, 2)).text() mustBe "Receipt status"
          } else {
            doc.select(Selectors.summaryListAtIndexRowKey(1, 2)).text() mustBe "eAD status"
            doc.select(Selectors.summaryListAtIndexRowValue(1, 2)).text() mustBe s"${statusToExplanation._1} ${statusToExplanation._2}"
          }
        }

      }
    }
  }

  "getMovementTypeForMovementView" should {
    "show GB tax warehouse to Y" when {
      "the users ERN starts with GBWK and the logged in user is the consignor" in {
        implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"), ern = "GBWK123456789")
        val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
          destinationType = TaxWarehouse,
          consignorTrader = getMovementResponseModel.consignorTrader.copy(traderExciseNumber = Some("GBWK123456789"))
        ))

        result mustBe "Great Britain tax warehouse to Tax warehouse in Great Britain"
      }
    }

    "show GB tax warehouse to Y for XIWK ERN (dispatch place is GB)" when {
      Seq(
        TaxWarehouse -> "Great Britain tax warehouse to Tax warehouse in Great Britain",
        DirectDelivery -> "Great Britain tax warehouse to Direct delivery",
        RegisteredConsignee -> "Great Britain tax warehouse to Registered consignee",
        TemporaryRegisteredConsignee -> "Great Britain tax warehouse to Temporary registered consignee",
        ExemptedOrganisation -> "Great Britain tax warehouse to Exempted organisation",
        UnknownDestination -> "Great Britain tax warehouse to Unknown destination"
      ).foreach { destinationTypeToMessage =>

        s"when the destination type is ${destinationTypeToMessage._1}" in {
          val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
            destinationType = destinationTypeToMessage._1,
            placeOfDispatchTrader = Some(TraderModel(
              traderExciseNumber = Some("GBRC345GTR145"),
              traderName = Some("Current 801 Consignee"),
              address = Some(AddressModel(
                streetNumber = None,
                street = Some("Main101"),
                postcode = Some("ZZ78"),
                city = Some("Zeebrugge")
              )),
              vatNumber = Some("GB123456789"),
              eoriNumber = None
            ))
          ))(dataRequest(FakeRequest("GET", "/"), ern = "XIWK123456789"), implicitly)

          result mustBe destinationTypeToMessage._2
        }

      }
    }

    "show XI tax warehouse to Y for XIWK ERN (dispatch place is XI)" when {
      Seq(
        TaxWarehouse -> "Northern Ireland tax warehouse to Tax warehouse in Great Britain",
        DirectDelivery -> "Northern Ireland tax warehouse to Direct delivery",
        RegisteredConsignee -> "Northern Ireland tax warehouse to Registered consignee",
        TemporaryRegisteredConsignee -> "Northern Ireland tax warehouse to Temporary registered consignee",
        ExemptedOrganisation -> "Northern Ireland tax warehouse to Exempted organisation",
        UnknownDestination -> "Northern Ireland tax warehouse to Unknown destination"
      ).foreach { destinationTypeToMessage =>

        s"the destination type is ${destinationTypeToMessage._1}" in {
          val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
            destinationType = destinationTypeToMessage._1,
            placeOfDispatchTrader = Some(TraderModel(
              traderExciseNumber = Some("XI00345GTR145"),
              traderName = Some("Current 801 Consignee"),
              address = Some(AddressModel(
                streetNumber = None,
                street = Some("Main101"),
                postcode = Some("ZZ78"),
                city = Some("Zeebrugge")
              )),
              vatNumber = Some("GB123456789"),
              eoriNumber = None
            ))
          ))(dataRequest(FakeRequest("GET", "/"), ern = "XIWK123456789"), implicitly)

          result mustBe destinationTypeToMessage._2
        }

      }
    }

    "show non-UK movement to Y for XIWK ERN (dispatch place is neither GB not XI)" when {
      Seq(
        TaxWarehouse -> "Movement from outside the United Kingdom to Tax warehouse in Great Britain",
        DirectDelivery -> "Movement from outside the United Kingdom to Direct delivery",
        RegisteredConsignee -> "Movement from outside the United Kingdom to Registered consignee",
        TemporaryRegisteredConsignee -> "Movement from outside the United Kingdom to Temporary registered consignee",
        ExemptedOrganisation -> "Movement from outside the United Kingdom to Exempted organisation",
        UnknownDestination -> "Movement from outside the United Kingdom to Unknown destination"
      ).foreach {
        case (destinationType, message) =>

          s"the destination type is $destinationType" in {
            val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
              destinationType = destinationType,
              placeOfDispatchTrader = Some(TraderModel(
                traderExciseNumber = Some("FR00345GTR145"),
                traderName = Some("Current 801 Consignee"),
                address = Some(AddressModel(
                  streetNumber = None,
                  street = Some("Main101"),
                  postcode = Some("ZZ78"),
                  city = Some("Zeebrugge")
                )),
                vatNumber = Some("GB123456789"),
                eoriNumber = None
              ))
            ))(dataRequest(FakeRequest("GET", "/"), ern = "XIWK123456789"), implicitly)

            result mustBe message
          }

      }
    }

    "show movement to Y for XIWK ERN" when {
      Seq(
        TaxWarehouse -> "Movement to Tax warehouse in Great Britain",
        DirectDelivery -> "Movement to Direct delivery",
        RegisteredConsignee -> "Movement to Registered consignee",
        TemporaryRegisteredConsignee -> "Movement to Temporary registered consignee",
        ExemptedOrganisation -> "Movement to Exempted organisation",
        UnknownDestination -> "Movement to Unknown destination"
      ).foreach {
        case (destinationType, message) =>

          s"when the destination type is $destinationType" when {
            "placeOfDispatchTrader.traderExciseNumber is missing" in {
              val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
                destinationType = destinationType,
                placeOfDispatchTrader = Some(TraderModel(
                  traderExciseNumber = None,
                  traderName = Some("Current 801 Consignee"),
                  address = Some(AddressModel(
                    streetNumber = None,
                    street = Some("Main101"),
                    postcode = Some("ZZ78"),
                    city = Some("Zeebrugge")
                  )),
                  vatNumber = Some("GB123456789"),
                  eoriNumber = None
                ))
              ))(dataRequest(FakeRequest("GET", "/"), ern = "XIWK123456789"), implicitly)

              result mustBe message
            }

            "placeOfDispatchTrader is missing" in {
              val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
                destinationType = destinationType,
                placeOfDispatchTrader = None
              ))(dataRequest(FakeRequest("GET", "/"), ern = "XIWK123456789"), implicitly)

              result mustBe message
            }
          }

      }
    }

    "show movement to Y for GBWK ERN" when {
      "logged in user is consignee" in {
        val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
          consignorTrader = getMovementResponseModel.consignorTrader.copy(traderExciseNumber = Some("GBRC123456789")),
          consigneeTrader = getMovementResponseModel.consigneeTrader.map(_.copy(traderExciseNumber = Some("GBWK123456789")))
        ))(dataRequest(FakeRequest("GET", "/"), ern = s"GBWK123456789"), implicitly)
        result mustBe "Movement to tax warehouse in Great Britain"
      }
    }

    Seq("GB00", "XI00").foreach {
      ernPrefix =>
        s"show movement to Y for $ernPrefix ERN" in {
          val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
            destinationType = RegisteredConsignee
          ))(dataRequest(FakeRequest("GET", "/"), ern = s"${ernPrefix}123456789"), implicitly)
          result mustBe "Movement to Registered consignee"
        }
    }

    "show Import for GB/XI" when {

      "the users ERN starts with GBRC" in {
        val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
          destinationType = RegisteredConsignee
        ))(dataRequest(FakeRequest("GET", "/"), ern = "GBRC123456789"), implicitly)
        result mustBe "Import for Registered consignee"
      }

      "the users ERN starts with XIRC" in {
        val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
          destinationType = RegisteredConsignee
        ))(dataRequest(FakeRequest("GET", "/"), ern = "XIRC123456789"), implicitly)
        result mustBe "Import for Registered consignee"
      }

    }

    "show the destination type only" when {

      "the users ERN starts with GBWK and the movement type is an Export (Customs declaration in UK)" in {
        val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
          deliveryPlaceCustomsOfficeReferenceNumber = Some("GBWK123456789"),
          destinationType = Export
        ))(dataRequest(FakeRequest("GET", "/"), ern = "GBWK123456789"), implicitly)
        result mustBe "Export with customs declaration lodged in the United Kingdom"
      }

      "the users ERN starts with GBWK and the movement type is an Export (Customs declaration in EU)" in {
        val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
          deliveryPlaceCustomsOfficeReferenceNumber = Some("FR123456789"),
          destinationType = Export
        ))(dataRequest(FakeRequest("GET", "/"), ern = "GBWK123456789"), implicitly)
        result mustBe "Export with customs declaration lodged in the European Union"
      }

      "the users ERN starts with XIWK and the movement type is an Export (Customs declaration in UK)" in {
        val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
          deliveryPlaceCustomsOfficeReferenceNumber = Some("GBWK123456789"),
          destinationType = Export
        ))(dataRequest(FakeRequest("GET", "/"), ern = "XIWK123456789"), implicitly)
        result mustBe "Export with customs declaration lodged in the United Kingdom"
      }

      "the users ERN starts with XIWK and the movement type is an Export (Customs declaration in EU)" in {
        val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
          deliveryPlaceCustomsOfficeReferenceNumber = Some("FR123456789"),
          destinationType = Export
        ))(dataRequest(FakeRequest("GET", "/"), ern = "XIWK123456789"), implicitly)
        result mustBe "Export with customs declaration lodged in the European Union"
      }
    }

    "cater for Duty Paid scenarios" when {

      "user is XIPA" when {

        "DestinationType is CertifiedConsignee" in {
          val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
            destinationType = CertifiedConsignee
          ))(dataRequest(FakeRequest("GET", "/"), ern = "XIPA00123456"), implicitly)
          result mustBe "Northern Ireland tax warehouse to certified consignee"
        }

        "DestinationType is TemporaryRegisteredConsignee" in {
          val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
            destinationType = TemporaryCertifiedConsignee
          ))(dataRequest(FakeRequest("GET", "/"), ern = "XIPA00123456"), implicitly)
          result mustBe "Northern Ireland tax warehouse to temporary certified consignee"
        }

        "DestinationType is ReturnToThePlaceOfDispatchOfTheConsignor" in {
          val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
            destinationType = ReturnToThePlaceOfDispatchOfTheConsignor
          ))(dataRequest(FakeRequest("GET", "/"), ern = "XIPA00123456"), implicitly)
          result mustBe "Return to place of dispatch"
        }
      }

      "user is XIPC" when {

        "DestinationType is CertifiedConsignee" in {
          val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
            destinationType = CertifiedConsignee
          ))(dataRequest(FakeRequest("GET", "/"), ern = "XIPC00123456"), implicitly)
          result mustBe "Northern Ireland tax warehouse to certified consignee"
        }

        "DestinationType is TemporaryRegisteredConsignee" in {
          val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
            destinationType = TemporaryCertifiedConsignee
          ))(dataRequest(FakeRequest("GET", "/"), ern = "XIPC00123456"), implicitly)
          result mustBe "Northern Ireland tax warehouse to temporary certified consignee"
        }

        "DestinationType is ReturnToThePlaceOfDispatchOfTheConsignor" in {
          val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
            destinationType = ReturnToThePlaceOfDispatchOfTheConsignor
          ))(dataRequest(FakeRequest("GET", "/"), ern = "XIPC00123456"), implicitly)
          result mustBe "Return to place of dispatch"
        }
      }

      "user is XIPB" in {
        val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
          destinationType = CertifiedConsignee
        ))(dataRequest(FakeRequest("GET", "/"), ern = "XIPB00123456"), implicitly)
        result mustBe "European Union duty paid movement to Northern Ireland certified consignee"
      }

      "user is XIPD" in {
        val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
          destinationType = CertifiedConsignee
        ))(dataRequest(FakeRequest("GET", "/"), ern = "XIPD00123456"), implicitly)
        result mustBe "European Union duty paid movement to Northern Ireland temporary certified consignee"
      }
    }

    "cater for XITC" in {
      val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
        destinationType = CertifiedConsignee
      ))(dataRequest(FakeRequest("GET", "/"), ern = "XITC00123456"), implicitly)
      result mustBe "European Union duty-suspended movement to Northern Ireland temporary registered consignee"
    }

    "return Movement to Y when the user type / destination type can't be matched" in {
      lazy val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
        deliveryPlaceTrader = None,
        destinationType = TaxWarehouse
      ))(dataRequest(FakeRequest("GET", "/"), ern = "GBWK123456789"), implicitly)

      result mustBe "Movement to Tax warehouse in the European Union"
    }
  }

  "getDateOfArrivalRow" should {

    "return the 'Predicted arrival'" when {

      "the date of arrival is not present in the GetMovementResponse" in {
        val result = helper.getDateOfArrivalRow(getMovementResponseModel.copy(reportOfReceipt = None))
        result mustBe SummaryListRow(
          key = Key(Text(value = "Predicted arrival")),
          value = Value(Text(value = "10 December 2008")),
          classes = "govuk-summary-list__row"
        )
      }

    }

    "return the 'Date of arrival'" when {

      "the date of arrival is present in the GetMovementResponse" in {
        val result = helper.getDateOfArrivalRow(getMovementResponseModel)
        result mustBe SummaryListRow(
          key = Key(Text(value = "Date of arrival")),
          value = Value(Text(value = "8 December 2008")),
          classes = "govuk-summary-list__row"
        )
      }

    }
  }

  "constructDetailedItems" should {
    "output the correct HTML" when {
      "the movement has items" in {
        val result = helper.constructDetailedItems(getMovementResponseModel)
        val doc = Jsoup.parse(result.toString())
        doc.select(Selectors.h2(1)).text() mustBe "Items"
        doc.select(Selectors.h3(1)).text() mustBe "Item 1"
        doc.select(Selectors.h4(1)).text() mustBe "Packaging"
        doc.select(Selectors.h3(2)).text() mustBe "Item 2"
        doc.select(Selectors.h4(2)).text() mustBe "Packaging"
        doc.select(Selectors.h4(3)).text() mustBe "Packaging 2"

      }
    }
  }
}
