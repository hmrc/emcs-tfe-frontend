import sbt.*

object AppDependencies {

  val bootstrapVersion = "9.11.0"
  val hmrcMongoVersion = "2.5.0"
  val playSuffix = s"-play-30"

  val compile = Seq(
    "uk.gov.hmrc"             %% s"bootstrap-frontend$playSuffix" %  bootstrapVersion,
    "uk.gov.hmrc"             %% s"play-frontend-hmrc$playSuffix" % s"11.2.0",
    "org.typelevel"           %%  "cats-core"                     %  "2.12.0",
    "uk.gov.hmrc.mongo"       %% s"hmrc-mongo$playSuffix"         %  hmrcMongoVersion
  )

  val test = Seq(
    "uk.gov.hmrc"             %% s"bootstrap-test$playSuffix"     % bootstrapVersion  % "test, it",
    "uk.gov.hmrc.mongo"       %% s"hmrc-mongo-test$playSuffix"    % hmrcMongoVersion  % "test, it",
    "org.scalamock"           %%  "scalamock"                     % "5.2.0"           % "test, it",
    "org.jsoup"               %   "jsoup"                         % "1.18.1"          %  Test,
    "com.vladsch.flexmark"    %   "flexmark-all"                  % "0.64.8"          % "test, it"
  )
}
