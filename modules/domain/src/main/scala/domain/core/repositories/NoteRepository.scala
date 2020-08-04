package domain.core.repositories

import domain.core.entities.{Note, Stock}
import domain.support.entities.User
import shared.ddd.{FailedToDeleteEntity, FailedToResolveById, FailedToSaveEntity}

trait NoteRepository[F[_]] {
  type FailedToResolveByNoteId = FailedToResolveById[Note]
  type FailedToResolveByStockId = FailedToResolveById[Stock]
  type FailedToSaveNote = FailedToSaveEntity[Note]
  type FailedToDeleteNote = FailedToDeleteEntity[Note]

  def resolve(id: Note.Id, uid: User.Id): F[Either[FailedToResolveByNoteId, Note]]
  def resolveByStockId(sid: Stock.Id, uid: User.Id): F[Seq[Note]]
  def save(value: Note): F[Either[FailedToSaveNote, Note]]
  def delete(id: Note.Id): F[Either[FailedToDeleteNote, Unit]]
}

object NoteRepository {
  def apply[F[_]: NoteRepository]: NoteRepository[F] = implicitly[NoteRepository[F]]
}
