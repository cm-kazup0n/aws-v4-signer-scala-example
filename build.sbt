ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.11"

lazy val root = (project in file("."))
  .settings(
    name := "example-for-aws-v4-signer-scala",
    libraryDependencies ++= Dependencies.dependencies,
    assembly / mainClass := Some("example.Main"),
    fork := false,
    ThisBuild / assemblyMergeStrategy := { _ =>
      MergeStrategy.first
    },
    scalacOptions ++= Seq( // use ++= to add to existing options
      "-encoding", "utf8", // if an option takes an arg, supply it on the same line
      "-feature", // then put the next option on a new line for easy editing
      "-language:implicitConversions",
      "-language:existentials",
      "-unchecked",
      "-Werror",
      "-Xlint", // exploit "trailing comma" syntax so you can add an option without editing this line
    )
  )
