import cats.effect.Async
import software.amazon.awssdk.services.sts._
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse

package object sts {
  private val client = new AssumeRoleClient(StsAsyncClient.builder().build())

  def assumeRoleF[F[_]: Async](implicit
      assumeRoleContext: AssumeRoleContext
  ): F[AssumeRoleResponse] = client.assumeRole(assumeRoleContext)

}
