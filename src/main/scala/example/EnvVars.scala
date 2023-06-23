package example

import cats.MonadError
import cats.effect.std.Env
import cats.syntax.all._

object EnvVars {

  def get[F[_]: Env](
      name: String
  )(implicit error: MonadError[F, Throwable]) = Env[F]
    .get(name)
    .flatMap(
      error
        .fromOption(_, new IllegalStateException(s"ENV $name is not set."))
    )
}
