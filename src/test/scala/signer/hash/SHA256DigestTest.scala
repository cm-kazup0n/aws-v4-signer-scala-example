package signer.hash

import org.scalatest.funsuite.AnyFunSuiteLike

import java.nio.charset.StandardCharsets

class SHA256DigestTest extends AnyFunSuiteLike {
  test("empty hash") {
    assert(
      SHA256Digest
        .digest(
          "".getBytes(StandardCharsets.UTF_8)
        )
        .getOrElse(
          ""
        ) == "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
    )
  }
}
