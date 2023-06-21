package signer.hash

import signer.{SigningException, SigningResult}

import java.security.MessageDigest
import scala.util.{Failure, Success, Try}

object SHA256Digest {

  def digest(content: Array[Byte]): SigningResult[String] = Try(
    digestUnsafe(content)
  ) match {
    case Success(hash) => Right(hash)
    case Failure(t)    => Left(new SigningException(t))
  }

  def digestUnsafe(content: Array[Byte]): String = {
    // FIXME 最適化のためにMessageDigestインスタンスを再利用する
    val md = MessageDigest.getInstance("SHA-256")
    // NOTE SDKの内部APIなので注意
    Base16.encode(md.digest(content))
  }

}
