import sbt._

object Dependencies {

  lazy val sttpVersion = "3.8.15"

  lazy val dependencies =
    sttpDeps ++ awsDeps ++ typelevelDeps ++ http4sDeps ++ testDeps

  lazy val sttpDeps = Seq(
    "core",
    "http4s-backend"
  ).map("com.softwaremill.sttp.client3" %% _ % sttpVersion)

  lazy val awsDeps = Seq(
    "software.amazon.awssdk" % "sts" % "2.20.87",
    "com.github.jkugiya" %% "aws-v4-signer-scala" % "0.14"
  )

  lazy val typelevelDeps = Seq(
    "org.typelevel" %% "cats-effect" % "3.5.0"
  )

  lazy val http4sDeps = Seq(
    "org.http4s" %% "http4s-ember-client" % "0.23.18"
  )

  lazy val testDeps =
    Seq(
      "org.scalatest" %% "scalatest" % "3.2.16" % "test"
    )
}
