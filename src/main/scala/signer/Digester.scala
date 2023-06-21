package signer

import cats.syntax.either._
import cats.syntax.traverse._
import signer.hash.SHA256Digest
import sttp.client3._

import java.nio.charset.StandardCharsets

object Digester {

  def digest(request: RequestBody[Any]): SigningResult[Digester.Digest] =
    extractRequestBody(request).flatMap(SHA256Digest.digest).map(Digest)

  private def extractRequestBody(
      request: RequestBody[Any]
  ): SigningResult[Array[Byte]] =
    request match {
      case NoBody               => "".getBytes(StandardCharsets.UTF_8).asRight
      case StringBody(s, _, _)  => s.getBytes(StandardCharsets.UTF_8).asRight
      case ByteArrayBody(s, _)  => s.asRight
      case ByteBufferBody(b, _) => b.array().asRight
      // NOTE: blockするので注意
      case InputStreamBody(is, _) => is.readAllBytes().asRight
      case FileBody(f, _)         => f.readAsByteArray.asRight
      case MultipartBody(parts) =>
        parts.toList
          .map(p => extractRequestBody(p.body))
          .sequence
          .map(_.toArray.flatten)
      // ストリームの実体はprivateのため取り出せないので未サポート
      case s @ StreamBody(_) =>
        Either.left(
          new SigningException(
            new IllegalArgumentException(
              s"StreamBody(${s.show}) is not supported."
            )
          )
        )
    }

  final case class Digest(conteSha256: String)
}
