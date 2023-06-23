package example.s3

import cats.effect.Async
import cats.syntax.all._
import signer.SigningContext
import signer.hash.SHA256Digest
import sts.AssumeRoleContext
import sttp.client3.quick._
import sttp.client3.{Response, SttpBackend}

import java.nio.charset.StandardCharsets

object PutObjectClient {

  def putObject[F[_]: Async](
      putObjectRequest: PutObjectRequest
  )(implicit
      assumeRoleContext: AssumeRoleContext,
      backend: SttpBackend[F, Any]
  ): F[Response[Either[String, String]]] = {
    implicit val signingContext: SigningContext =
      SigningContext(
        region = putObjectRequest.region.name,
        service = "s3"
      )
    val request = basicRequest
      .put(putObjectRequest.endpoint)
      .body(putObjectRequest.body.Body)
      .header(
        "x-amz-content-sha256",
        SHA256Digest.digestUnsafe(
          putObjectRequest.body.Body.getBytes(StandardCharsets.UTF_8)
        )
      )

    for {
      assumeRoleResponse <- sts.assumeRoleF
      signed <- Async[F].fromEither(
        signer.sign(request, assumeRoleResponse)
      )
      res <- signed.send(backend)
    } yield res
  }

}
