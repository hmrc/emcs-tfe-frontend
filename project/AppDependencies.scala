import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val bootstrapVersion = "7.12.0"

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-frontend-play-28" % bootstrapVersion,
    "uk.gov.hmrc" %% "play-frontend-hmrc" % "6.2.0-play-28",
    "org.typelevel" %% "cats-core" % "2.9.0"
  )

  val test = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-28" % bootstrapVersion % "test, it",
    "org.scalamock" %% "scalamock" % "5.2.0" % "test, it",
    "org.jsoup" % "jsoup" % "1.15.3" % Test,
    "com.vladsch.flexmark" % "flexmark-all" % "0.36.8" % "test, it",
    "com.github.tomakehurst" % "wiremock-jre8" % "2.33.2" % "it"
  )
}
