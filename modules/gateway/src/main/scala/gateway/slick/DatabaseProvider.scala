package gateway.slick

import cats.~>
import slick.jdbc.JdbcProfile

trait DatabaseProvider[F[_]] {
  val profile: JdbcProfile
  val database: profile.backend.DatabaseDef
  val context: scala.concurrent.ExecutionContext
  def transformation: profile.api.DBIO ~> F
}
