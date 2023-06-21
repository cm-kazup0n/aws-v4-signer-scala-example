package example

import cats.effect.std.Console
import cats.effect.{IO, IOApp}
import cats.syntax.all._
import example.EnvVars.get
import example.s3._
import sts.AssumeRoleContext
import sttp.client3.http4s._

object Main extends IOApp.Simple {

  private val assumeRoleContext: IO[AssumeRoleContext] =
    get[IO]("ROLE_ARN").map(roleArn =>
      AssumeRoleContext(roleArn = roleArn, roleSessionName = "s3-test")
    )

  override def run: IO[Unit] = for {
    res <- putObject("1,2,3,4,5")
    _ <- Console[IO].println(res)
  } yield ()

  private def putObject(content: String) =
    Http4sBackend.usingDefaultEmberClientBuilder[IO]().use { implicit backend =>
      assumeRoleContext.flatMap { implicit role =>
        for {
          request <- putObjectRequest(content)
          response <- PutObjectClient.putObject[IO](request)
        } yield response
      }
    }

  private def putObjectRequest(content: String): IO[PutObjectRequest] = (
    get[IO]("REGION").map(S3Region),
    get[IO]("BUCKET"),
    get[IO]("KEY")
  ).mapN(PutObjectRequest(_, _, _, PutObjectRequestBody(content)))
}
