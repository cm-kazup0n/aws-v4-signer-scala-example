package example.s3

import sttp.client3.UriContext
import sttp.model.Uri

final case class PutObjectRequest(
    region: S3Region,
    bucket: String,
    key: String,
    body: PutObjectRequestBody
) {
  val endpoint: Uri = uri"https://$bucket.s3-${region.name}.amazonaws.com/$key"
}
