import sbt.Setting
import scoverage.ScoverageKeys

object CodeCoverageSettings {

  private val excludedPackages: Seq[String] = Seq(
    "<empty>",
    "Reverse.*",
    "uk.gov.hmrc.BuildInfo",
    ".*handlers.*",
    ".*components.*",
    "app.*",
    "prod.*",
    ".*Routes.*",
    ".*viewmodels.govuk.*",
    "testOnly.*",
    "testOnlyDoNotUseInAppConf.*",
    ".*featureswitch.*",
    ".*views.html.components.*",
    ".*views.html.templates.*",
    "^views$"
  )

  val settings: Seq[Setting[_]] = Seq(
    ScoverageKeys.coverageExcludedPackages := excludedPackages.mkString(";"),
    ScoverageKeys.coverageMinimumStmtTotal := 96,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )
}
