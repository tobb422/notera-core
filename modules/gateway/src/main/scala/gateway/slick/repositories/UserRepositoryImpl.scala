package gateway.slick.repositories

import cats.implicits._
import cats.{Monad, ~>}
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile
import domain.support.entities.User
import domain.support.repositories.UserRepository
import gateway.slick.tables.UserTable
import shared.ddd.{FailedToResolveById, FailedToSaveEntity}

import scala.util.{Failure, Success}


class UserRepositoryImpl[F[_]: Monad](
  jdbcProfile: JdbcProfile,
  execute: DBIO ~> F
)(implicit ec: scala.concurrent.ExecutionContext) extends UserRepository[F] {
  import jdbcProfile.api._

  private val users = new UserTable(jdbcProfile).query

  override def resolve(id: User.Id): F[Either[FailedToResolveByUserId, User]] = {
    val res = users.filter(t => t.id === id.value).result.headOption.map {
      case Some(user) => Right(user)
      case None => FailedToResolveById[User](id).asLeft
    }
    execute.apply(res)
  }

  override def insert(value: User): F[Either[FailedToSaveUser, User]] = {
    val res = users.insertOrUpdate(value).asTry.map {
      case Success(_) => value.asRight
      case Failure(e) => FailedToSaveEntity[User](message = Option(e.getMessage)).asLeft
    }
    execute.apply(res)
  }
}
