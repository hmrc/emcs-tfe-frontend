package uk.gov.hmrc.emcstfefrontend.stubs

import uk.gov.hmrc.emcstfefrontend.fixtures.BaseFixtures
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status.{OK, UNAUTHORIZED}
import play.api.libs.json.Json

object GetTraderKnownFactsStub extends DownstreamStub with BaseFixtures {

  val traderKnownFactsUri = "/emcs-tfe-reference-data/oracle/trader-known-facts?exciseRegistrationId=ERN"

  val traderKnownFactsJson = Json.obj(
    "traderName" -> "SEED TRADER 1629",
    "addressLine1" -> "629 High Street",
    "addressLine2" -> "Any Suburb",
    "addressLine3" -> "Any Town",
    "addressLine4" -> "Any County",
    "addressLine5" -> "UK",
    "postcode" -> "SS1 99AA"

  )


  def test(): StubMapping =
    onSuccess(POST, traderKnownFactsUri, OK,
      Json.obj(
        "traderName" -> "SEED TRADER 1629",
        "addressLine1" -> "629 High Street",
        "addressLine2" -> "Any Suburb",
        "addressLine3" -> "Any Town",
        "addressLine4" -> "Any County",
        "addressLine5" -> "UK",
        "postcode" -> "SS1 99AA"

      ))

  def unauthorised(): StubMapping =
    onError(POST, traderKnownFactsUri, UNAUTHORIZED)
}