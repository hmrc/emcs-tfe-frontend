import sbt.*

object AppDependencies {

  val bootstrapVersion = "9.13.0"
  val hmrcMongoVersion = "2.6.0"
  val playSuffix = "-play-30"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"             %% s"bootstrap-frontend$playSuffix" %  bootstrapVersion,
    "uk.gov.hmrc"             %% s"play-frontend-hmrc$playSuffix" %  "12.6.0",
    "org.typelevel"           %%  "cats-core"                     %  "2.12.0",
    "uk.gov.hmrc.mongo"       %% s"hmrc-mongo$playSuffix"         %  hmrcMongoVersion
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"             %% s"bootstrap-test$playSuffix"     % bootstrapVersion,
    "uk.gov.hmrc.mongo"       %% s"hmrc-mongo-test$playSuffix"    % hmrcMongoVersion,
    "org.scalamock"           %%  "scalamock"                     % "5.2.0",
    "org.jsoup"               %   "jsoup"                         % "1.18.1",
    "com.vladsch.flexmark"    %   "flexmark-all"                  % "0.64.8"
  ).map(_ % Test)

  val it: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% s"bootstrap-test$playSuffix" % bootstrapVersion % Test
  )

  def apply(): Seq[ModuleID] = compile ++ test

}
