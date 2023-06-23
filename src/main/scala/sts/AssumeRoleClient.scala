package sts

import cats.effect.Async
import software.amazon.awssdk.services.sts.StsAsyncClient
import software.amazon.awssdk.services.sts.model.{
  AssumeRoleRequest,
  AssumeRoleResponse
}

import scala.jdk.FutureConverters.CompletionStageOps
class AssumeRoleClient(private val stsClient: StsAsyncClient) {
  def assumeRole[F[_]: Async](
      assumeRoleContext: AssumeRoleContext
  ): F[AssumeRoleResponse] = Async[F].fromFuture(
    Async[F].delay(
      stsClient
        .assumeRole(
          AssumeRoleRequest
            .builder()
            .roleArn(assumeRoleContext.roleArn)
            .roleSessionName(assumeRoleContext.roleSessionName)
            .build()
        )
        .asScala
    )
  )
}
