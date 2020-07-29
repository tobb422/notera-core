package gateway.slick.repositories

import cats.implicits._
import cats.{Monad, ~>}
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile
import domain.core.entities.{Tag => DTag}
import domain.core.repositories.TagRepository
import domain.support.entities.User
import gateway.slick.tables.TagTable
import shared.ddd.{FailedToDeleteEntity, FailedToResolveById, FailedToSaveEntity}

import scala.util.{Failure, Success}


class TagRepositoryImpl[F[_]: Monad](
  jdbcProfile: JdbcProfile,
  execute: DBIO ~> F
)(implicit ec: scala.concurrent.ExecutionContext) extends TagRepository[F] {
  import jdbcProfile.api._

  private val tags = new TagTable(jdbcProfile).query

  override def resolve(id: DTag.Id, uid: User.Id): F[Either[FailedToResolveByTagId, DTag]] = {
    val res = tags.filter(t => t.id === id.value && t.userId === uid.value).result.headOption.map {
      case Some(tag) => Right(tag)
      case None => FailedToResolveById[DTag](id).asLeft
    }
    execute.apply(res)
  }

  override def list(uid: User.Id): F[Seq[DTag]] = {
    val res = tags.filter(_.userId === uid.value).result
    execute.apply(res)
  }

  override def save(value: DTag): F[Either[FailedToSaveTag, DTag]] = {
    val res = tags.insertOrUpdate(value).asTry.map {
      case Success(_) => value.asRight
      case Failure(e) => FailedToSaveEntity[DTag](message = Option(e.getMessage)).asLeft
    }
    execute.apply(res)
  }

  def delete(id: DTag.Id): F[Either[FailedToDeleteTag, Unit]] = {
    val res = tags.filter(_.id === id.value).delete.asTry.map {
      case Success(_) => ().asRight
      case Failure(e) => FailedToDeleteEntity[DTag](message = Option(e.getMessage)).asLeft
    }
    execute.apply(res)
  }
}
