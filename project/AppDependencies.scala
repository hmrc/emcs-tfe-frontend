import sbt._

object AppDependencies {

  val bootstrapVersion = "8.1.0"
  val playSuffix = s"-play-28"

  val compile = Seq(
    "uk.gov.hmrc"             %% s"bootstrap-frontend$playSuffix" %  bootstrapVersion,
    "uk.gov.hmrc"             %% s"play-frontend-hmrc$playSuffix" % s"8.1.0",
    "org.typelevel"           %%  "cats-core"                     %  "2.9.0"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% s"bootstrap-test$playSuffix"     % bootstrapVersion  % "test, it",
    "org.scalamock"           %%  "scalamock"                     % "5.2.0"           % "test, it",
    "org.jsoup"               %   "jsoup"                         % "1.15.3"          %  Test,
    "com.vladsch.flexmark"    %   "flexmark-all"                  % "0.36.8"          % "test, it",
    "com.github.tomakehurst"  %   "wiremock-jre8"                 % "2.33.2"          % "it"
  )
}
