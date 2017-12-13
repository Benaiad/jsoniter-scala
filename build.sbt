import com.typesafe.sbt.pgp.PgpKeys._
import sbt.Keys.scalacOptions
import sbt.url

lazy val commonSettings = Seq(
  organization := "com.github.plokhotnyuk.jsoniter-scala",
  organizationHomepage := Some(url("https://github.com/plokhotnyuk")),
  homepage := Some(url("https://github.com/plokhotnyuk/jsoniter-scala")),
  licenses := Seq(("MIT License", url("https://opensource.org/licenses/mit-license.html"))),
  startYear := Some(2017),
  developers := List(
    Developer(
      id = "plokhotnyuk",
      name = "Andriy Plokhotnyuk",
      email = "plokhotnyuk@gmail.com",
      url = url("https://twitter.com/aplokhotnyuk")
    )
  ),
  scalaVersion := "2.12.4",
  crossScalaVersions := Seq("2.12.4", "2.11.12"),
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Xfuture",
    "-Xlint",
    "-Xmacro-settings:print-codecs"
  )
)

lazy val noPublishSettings = Seq(
  skip in publish := true,
  publishTo := Some(if (isSnapshot.value) Opts.resolver.sonatypeSnapshots else Opts.resolver.sonatypeStaging)
)

lazy val publishSettings = Seq(
  publishTo := Some(if (isSnapshot.value) Opts.resolver.sonatypeSnapshots else Opts.resolver.sonatypeStaging),
  sonatypeProfileName := "com.github.plokhotnyuk",
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/plokhotnyuk/jsoniter-scala"),
      "scm:git@github.com:plokhotnyuk/jsoniter-scala.git"
    )
  ),
  publishMavenStyle := true,
  pomIncludeRepository := { _ => false },
  // FIXME: remove setting of overwrite flag when the following issue will be fixed: https://github.com/sbt/sbt/issues/3725
  publishConfiguration := publishConfiguration.value.withOverwrite(isSnapshot.value),
  publishSignedConfiguration := publishSignedConfiguration.value.withOverwrite(isSnapshot.value),
  publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(isSnapshot.value),
  publishLocalSignedConfiguration := publishLocalSignedConfiguration.value.withOverwrite(isSnapshot.value)
)

lazy val `jsoniter-scala` = project.in(file("."))
  .aggregate(core, macros, benchmark)
  .settings(commonSettings: _*)
  .settings(noPublishSettings: _*)

lazy val core = project
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalacheck" %% "scalacheck" % "1.13.5" % Test,
      "org.scalatest" %% "scalatest" % "3.0.4" % Test
    )
  )

lazy val macros = project
  .dependsOn(core)
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "org.scalacheck" %% "scalacheck" % "1.13.5" % Test,
      "org.scalatest" %% "scalatest" % "3.0.4" % Test
    )
  )

lazy val benchmark = project
  .enablePlugins(JmhPlugin)
  .dependsOn(macros)
  .settings(commonSettings: _*)
  .settings(noPublishSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "com.github.ichoran" %% "kse" % "0.6-SNAPSHOT",
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.2",
      "com.fasterxml.jackson.module" % "jackson-module-afterburner" % "2.9.2",
      "io.circe" %% "circe-generic" % "0.9.0-M2",
      "io.circe" %% "circe-generic-extras" % "0.9.0-M2",
      "io.circe" %% "circe-parser" % "0.9.0-M2",
      "com.typesafe.play" %% "play-json" % "2.6.8",
      "org.julienrf" %% "play-json-derived-codecs" % "4.0.0",
      "pl.project13.scala" % "sbt-jmh-extras" % "0.3.0",
      "org.scalatest" %% "scalatest" % "3.0.4" % Test
    )
  )
