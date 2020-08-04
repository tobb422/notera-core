package gateway.slick.repositories

import cats.implicits._
import cats.~>
import domain.core.entities.{Note, Stock}
import domain.core.repositories.NoteRepository
import domain.support.entities.User
import gateway.slick.tables.NoteTable
import shared.ddd.{FailedToDeleteEntity, FailedToResolveById, FailedToSaveEntity}
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile

import scala.util.{Failure, Success}

class NoteRepositoryImpl[F[_]](
  jdbcProfile: JdbcProfile,
  execute: DBIO ~> F
)(implicit ec: scala.concurrent.ExecutionContext) extends NoteRepository[F] {
  import jdbcProfile.api._

  private val notes = new NoteTable(jdbcProfile).query

  override def resolve(id: Note.Id, uid: User.Id): F[Either[FailedToResolveByNoteId, Note]] = {
    val res = notes.filter(m => m.id === id.value && m.userId === uid.value).result.headOption.map {
      case Some(note) => Right(note)
      case None => FailedToResolveById[Note](id).asLeft
    }

    execute.apply(res)
  }

  override def resolveByStockId(sid: Stock.Id, uid: User.Id): F[Seq[Note]] = {
    val res = notes.filter(m => m.stockId === sid.value && m.userId === uid.value).result
    execute.apply(res)
  }

  override def save(value: Note): F[Either[FailedToSaveNote, Note]] = {
    val res = notes.insertOrUpdate(value).asTry.map {
      case Success(_) => value.asRight
      case Failure(e) => FailedToSaveEntity[Note](message = Option(e.getMessage)).asLeft
    }

    execute.apply(res)
  }

  override def delete(id: Note.Id): F[Either[FailedToDeleteNote, Unit]] = {
    val res = notes.filter(_.id === id.value).delete.asTry.map {
      case Success(_) => ().asRight
      case Failure(e) => FailedToDeleteEntity[Note](message = Option(e.getMessage)).asLeft
    }

    execute.apply(res)
  }
}
