package http.http4s

import cats.effect.{ContextShift, IO}
import cats.~>
import gateway.slick.DatabaseProvider

import scala.concurrent.ExecutionContext

class DatabaseProviderIO(implicit cx: ContextShift[IO]) extends DatabaseProvider[IO] {
  override val profile = slick.jdbc.PostgresProfile

  import profile.api._
  override val database: profile.backend.DatabaseDef = Database.forConfig("db")
  override val context: ExecutionContext            = scala.concurrent.ExecutionContext.global
  override def transformation: profile.api.DBIO ~> IO = new (DBIO ~> IO) {
    override def apply[A](fa: profile.api.DBIO[A]): IO[A] =
      IO.fromFuture(IO { database.run(fa.transactionally) })
  }
}
