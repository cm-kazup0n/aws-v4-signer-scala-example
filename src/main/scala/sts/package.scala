import cats.effect.Async
import software.amazon.awssdk.services.sts._
import software.amazon.awssdk.services.sts.model.{
  AssumeRoleRequest,
  AssumeRoleResponse
}

import scala.jdk.FutureConverters.CompletionStageOps

package object sts {
  def assumeRoleF[F[_]: Async](implicit
      assumeRoleContext: AssumeRoleContext
  ): F[AssumeRoleResponse] = {
    val stsClient = StsAsyncClient.builder().build()
    val req = AssumeRoleRequest
      .builder()
      .roleArn(assumeRoleContext.roleArn)
      .roleSessionName(assumeRoleContext.roleSessionName)
      .build()
    val res = stsClient.assumeRole(req)
    Async[F].fromFuture {
      Async[F].delay(res.asScala)
    }
  }

}
