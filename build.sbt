import com.typesafe.sbt.packager.archetypes.systemloader.ServerLoader.SystemV
import wartremover.{Wart, Warts}

ThisBuild / scalaVersion := props.ScalaVersion
ThisBuild / version := props.ProjectVersion
ThisBuild / organization := props.Org
ThisBuild / organizationName := props.OrgName
ThisBuild / developers := List(
  Developer(
    props.GitHubUsername,
    "Kevin Lee",
    "kevin.lee@clearscore.com",
    url(s"https://github.com/${props.GitHubUsername}"),
  )
)
ThisBuild / homepage := url(s"https://github.com/${props.GitHubUsername}/${props.RepoName}").some
ThisBuild / scmInfo :=
  ScmInfo(
    url(s"https://github.com/${props.GitHubUsername}/${props.RepoName}"),
    s"https://github.com/${props.GitHubUsername}/${props.RepoName}.git",
  ).some

ThisBuild / javaOptions += "-Dcats.effect.warnOnNonMainThreadDetected=false"

ThisBuild / semanticdbEnabled := true

lazy val root = (project in file("."))
  .settings(
    name := props.ProjectName
  )
  .settings(noPublish)
  .aggregate(core, app)

lazy val core = subProject("core")
  .settings(
    libraryDependencies ++=
      libs.refined4s ++
        libs.extras ++
        libs.catsAndCatsEffect ++
        List(
          libs.laikaCore,
          libs.jsoup,
        )
  )

lazy val app = subProject("app")
  .enablePlugins(JavaAppPackaging)
  .settings(debianPackageInfo)
  .settings(
    maintainer := "Kevin Lee <kevin.lee@clearscore.com>",
    description := "A link validator for web pages",
    Compile / run / fork := false, // This is required to get an input from the console.
  )
  .settings(
    libraryDependencies ++= libs.refined4s ++ libs.catsAndCatsEffect ++ libs.decline,
  )
  .dependsOn(
    core % props.IncludeTest
  )

lazy val props =
  new {
    val ScalaVersion = "3.7.2"
    val Org          = "io.kevinlee"
    val OrgName      = ""

    val GitHubUsername = "kevin-lee"
    val RepoName       = "link-validator-example"
    val ProjectName    = RepoName
    val ProjectVersion = "0.1.0-SNAPSHOT"

    val Refined4sVersion = "1.9.0"

    val ExtrasVersion = "0.49.0"

    val HedgehogVersion = "0.13.0"

    val CatsVersion       = "2.13.0"
    val CatsEffectVersion = "3.6.3"

    val DeclineVersion = "2.5.0"

    val LaikaVersion = "1.3.2"

    val JsoupVersion = "1.21.2"

    val IncludeTest: String = "compile->compile;test->test"
  }

lazy val libs =
  new {

    lazy val refined4s = List(
      "io.kevinlee" %% "refined4s-core"          % props.Refined4sVersion,
      "io.kevinlee" %% "refined4s-cats"          % props.Refined4sVersion,
      "io.kevinlee" %% "refined4s-chimney"       % props.Refined4sVersion,
      "io.kevinlee" %% "refined4s-circe"         % props.Refined4sVersion,
      "io.kevinlee" %% "refined4s-extras-render" % props.Refined4sVersion,
      "io.kevinlee" %% "refined4s-pureconfig"    % props.Refined4sVersion,
      "io.kevinlee" %% "refined4s-doobie-ce3"    % props.Refined4sVersion,
      "io.kevinlee" %% "refined4s-tapir"         % props.Refined4sVersion,
    )

    lazy val extras = List(
      "io.kevinlee" %% "extras-render"       % props.ExtrasVersion,
      "io.kevinlee" %% "extras-string"       % props.ExtrasVersion,
      "io.kevinlee" %% "extras-cats"         % props.ExtrasVersion,
      "io.kevinlee" %% "extras-scala-io"     % props.ExtrasVersion,
      "io.kevinlee" %% "extras-hedgehog-ce3" % props.ExtrasVersion % Test
    )

    lazy val catsAndCatsEffect = List(
      "org.typelevel" %% "cats-core"   % props.CatsVersion,
      "org.typelevel" %% "cats-effect" % props.CatsEffectVersion,
    )

    lazy val decline = List(
      "com.monovore" %% "decline" % props.DeclineVersion
    )

    lazy val laikaCore = "org.typelevel" %% "laika-core" % props.LaikaVersion

    lazy val jsoup = "org.jsoup" % "jsoup" % props.JsoupVersion

    lazy val tests = new {
      lazy val hedgehogLibs = List(
        "qa.hedgehog" %% "hedgehog-core"   % props.HedgehogVersion,
        "qa.hedgehog" %% "hedgehog-runner" % props.HedgehogVersion,
        "qa.hedgehog" %% "hedgehog-sbt"    % props.HedgehogVersion,
      ).map(_ % Test)
    }
  }

// format: off
def prefixedProjectName(name: String) = s"${props.ProjectName}${if (name.isEmpty) "" else s"-$name"}"
// format: on

def subProject(projectName: String): Project = {
  val prefixedName = prefixedProjectName(projectName)
  Project(prefixedName, file(s"modules/$prefixedName"))
    .settings(
      name := prefixedName,
      fork := true,
      libraryDependencies ++= libs.tests.hedgehogLibs,
      wartremoverErrors ++= Warts.unsafe,
      wartremoverExcluded ++= (Compile / sourceManaged).value.get,
      /* Exclude specific warts if needed */
      wartremoverErrors --= Seq(
        Wart.Any,
        Wart.Nothing,
      ),
      Compile / console / scalacOptions :=
        (console / scalacOptions)
          .value
          .distinct
          .filterNot(option => option.contains("wartremover") || option.contains("import")),
      Test / console / scalacOptions :=
        (console / scalacOptions)
          .value
          .distinct
          .filterNot(option => option.contains("wartremover") || option.contains("import")),
    )
}

lazy val debianPackageInfo: SettingsDefinition = List(
  Linux / maintainer := "Kevin Lee <kevin.lee@clearscore.com>",
  Linux / packageSummary := "My App",
  packageDescription := "My app is ...",
  Debian / serverLoading := SystemV.some,
)
