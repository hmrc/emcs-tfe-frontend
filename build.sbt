import play.sbt.routes.RoutesKeys
import sbt.*
import uk.gov.hmrc.DefaultBuildSettings.addTestReportOption
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

val appName = "emcs-tfe-frontend"

val silencerVersion = "1.7.12"

lazy val ItTest = config("it") extend Test

ThisBuild / scalaVersion := "2.13.12"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    majorVersion := 1,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    scalacOptions ++= Seq(
      "-feature",
      "-rootdir",
      baseDirectory.value.getCanonicalPath,
      "-Wconf:cat=deprecation:ws,cat=feature:ws,cat=optimizer:ws,src=target/.*:s"
    ),
    Assets / pipelineStages := Seq(gzip),
    RoutesKeys.routesImport ++= Seq(
      "models._",
      "models.draftMovements._",
      "uk.gov.hmrc.play.bootstrap.binders.RedirectUrl"
    )
  )
  .settings(
    scalacOptions += "-Wconf:cat=unused-imports&src=html/.*:s",
    scalacOptions += "-Wconf:cat=unused-imports&src=routes/.*:s"
  )
  .settings(inConfig(Test)(testSettings): _*)
  .configs(ItTest)
  .settings(inConfig(ItTest)(itSettings): _*)
  .settings(
    ItTest / fork := true,
    ItTest / unmanagedSourceDirectories := Seq((ItTest / baseDirectory).value / "it"),
    ItTest / unmanagedClasspath += baseDirectory.value / "resources",
    Runtime / unmanagedClasspath += baseDirectory.value / "resources",
    ItTest / javaOptions += "-Dlogger.resource=logback-test.xml",
    ItTest / parallelExecution := false,
    addTestReportOption(ItTest, "int-test-reports")
  )
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(
    // concatenate js
    Concat.groups := Seq(
      "javascripts/application.js" ->
        group(Seq(
          "javascripts/app.js"
        )),
      "javascripts/autocomplete.js" ->
        group(Seq(
          "javascripts/accessible-autocomplete-polyfill.js"
        ))
    ),
    // prevent removal of unused code which generates warning errors due to use of third-party libs
    uglifyCompressOptions := Seq("unused=false", "dead_code=false"),
    uglifyOps := UglifyOps.singleFile, // no source map
    pipelineStages := Seq(digest),
    // below line required to force asset pipeline to operate in dev rather than only prod
    Assets / pipelineStages := Seq(concat, uglify),
    // only compress files generated by concat
    uglify / includeFilter := GlobFilter("*.js")
  )
  .settings(PlayKeys.playDefaultPort := 8310)
  .settings(
    TwirlKeys.templateImports ++= Seq(
    "play.twirl.api.HtmlFormat",
    "play.twirl.api.HtmlFormat._",
    "uk.gov.hmrc.govukfrontend.views.html.components._",
    "uk.gov.hmrc.hmrcfrontend.views.html.components._",
    "uk.gov.hmrc.hmrcfrontend.views.html.helpers._",
    "uk.gov.hmrc.hmrcfrontend.views.config._",
    "views.ViewUtils._",
    "viewmodels.govuk._",
    "models.PageSection._",
    "controllers.routes._",
    "uk.gov.hmrc.hmrcfrontend.views.viewmodels.timeoutdialog.TimeoutDialog",
    "viewmodels.govuk.all._"
  ))
  .settings(CodeCoverageSettings.settings: _*)



lazy val testSettings: Seq[Def.Setting[_]] = Seq(
  fork := true,
  unmanagedSourceDirectories += baseDirectory.value / "test-utils",
  Test / javaOptions += "-Dlogger.resource=logback-test.xml"
)

lazy val itSettings = Defaults.itSettings ++ Seq(
  unmanagedSourceDirectories := Seq(
    baseDirectory.value / "it",
    baseDirectory.value / "test-utils"
  ),
  unmanagedResourceDirectories := Seq(
    baseDirectory.value / "it" / "resources"
  ),
  parallelExecution := false,
  fork := true
)
