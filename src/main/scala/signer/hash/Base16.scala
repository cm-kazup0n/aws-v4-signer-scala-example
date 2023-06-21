package signer.hash

import software.amazon.awssdk.utils.internal.Base16Lower
object Base16 {
  def encode(data: Array[Byte]): String = Base16Lower.encodeAsString(data: _*)
}
