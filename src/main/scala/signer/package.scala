import cats.syntax.all._
import jkugiya.awstools.signer.v4.credentials.AwsCredentials
import jkugiya.awstools.signer.v4.{Header, HttpRequest, Signer}
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse
import sttp.client3.{Identity, RequestT}
import sttp.model.{Header => SttpHeader}

import java.time.{ZoneId, ZonedDateTime}
import java.time.format.DateTimeFormatter
import scala.util.Try

package object signer {
  import implicits._

  type SigningResult[A] = Either[SigningException, A]

  def sign[T](
      request: RequestT[Identity, T, Any],
      stsResponse: AssumeRoleResponse
  )(implicit
      context: SigningContext
  ): SigningResult[RequestT[Identity, T, Any]] = {
    val ACCESS_KEY = stsResponse.credentials().accessKeyId()
    val SECRET_KEY = stsResponse.credentials().secretAccessKey()
    val signer = Signer(
      region = context.region,
      service = context.service,
      AwsCredentials(ACCESS_KEY, SECRET_KEY)
    )

    // 署名に必要なヘッダーを追加
    val headers: Seq[SttpHeader] = request
      .header(
        headerKeys.HeaderAmzDate,
        ZonedDateTime
          .now(ZoneId.of("UTC"))
          .format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssX"))
      )
      .header(
        headerKeys.HeaderAmzSecToken,
        stsResponse.credentials().sessionToken()
      )
      .header("host", request.uri.host.getOrElse(""))
      .headers

    val req =
      HttpRequest(method = request.method.method, uri = request.uri.toJavaUri)

    for {
      contentSha256 <- Digester.digest(request.body)
      sign <- signer.trySign(
        req,
        contentSha256.conteSha256,
        // sttpのヘッダーからsignerのヘッダーへ変換
        headers.map(sttpHeaderToSignerHeader)
      )
      // AuthZヘッダーを追加して、sttpのヘッダーに戻す
      hs = headers :+ SttpHeader(
        name = headerKeys.HeaderAuth,
        value = sign
      )
    } yield request.copy(headers = hs)

  }

  implicit class SignerOps(signer: Signer) {
    def trySign(
        request: HttpRequest,
        contentSha256: String,
        headers: Seq[Header]
    ): SigningResult[String] =
      Try(signer.sign(request, contentSha256, headers: _*)).toEither
        .leftMap(new SigningException(_))
  }

  object implicits {
    implicit def signerHeaderToSttpHeader(h: Header): SttpHeader =
      SttpHeader(h.key, h.value)

    implicit def sttpHeaderToSignerHeader(h: SttpHeader): Header =
      Header(h.name, h.value)
  }

  object headerKeys {
    val HeaderAmzDate = "X-Amz-Date"
    val HeaderAuth = "Authorization"
    val HeaderAmzSecToken = "x-amz-security-token"
  }
}
