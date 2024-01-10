import sbt._

object AppDependencies {

  val bootstrapVersion = "8.4.0"
  val playSuffix = s"-play-30"

  val compile = Seq(
    "uk.gov.hmrc"             %% s"bootstrap-frontend$playSuffix"  %  bootstrapVersion,
    "uk.gov.hmrc"             %% s"play-frontend-hmrc$playSuffix"  % s"8.3.0",
    "org.typelevel"           %%  "cats-core"                     %  "2.10.0"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% s"bootstrap-test$playSuffix"      % bootstrapVersion  % "test, it",
    "org.scalamock"           %%  "scalamock"                     % "5.2.0"           % "test, it",
    "org.jsoup"               %   "jsoup"                         % "1.17.2"          %  Test,
    "com.vladsch.flexmark"     %   "flexmark-all"                    % "0.64.8"         % "test, it"
  )
}
