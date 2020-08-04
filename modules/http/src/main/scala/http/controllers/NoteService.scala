package http.controllers

import cats.Monad
import cats.data.EitherT
import domain.core.entities.{Note, Stock}
import domain.core.repositories.NoteRepository
import domain.support.entities.User
import http.controllers.error.{APIError, BadRequest, NotFound}
import http.presenter.note.{NoteResponse, NotesResponse, PostNoteRequest, PutNoteRequest}
import shared.ddd.IdGenerator

class NoteService[F[_]: Monad: NoteRepository](
  implicit val idGen: IdGenerator[String]
) {
  def getNote(id: Note.Id, uid: User.Id): F[Either[APIError, NoteResponse]] =
    EitherT(NoteRepository[F].resolve(id, uid))
      .leftMap(e => NotFound(e.getMessage): APIError)
      .map(NoteResponse.fromEntity).value

  def getNotes(sid: Stock.Id, uid: User.Id): F[Either[APIError, NotesResponse]] =
    EitherT.right[APIError](NoteRepository[F].resolveByStockId(sid, uid))
    .map(notes => NotesResponse(notes.map(NoteResponse.fromEntity))).value

  def createNote(req: PostNoteRequest, uid: User.Id): F[Either[APIError, NoteResponse]] =
    EitherT(NoteRepository[F].save(req.toEntity(uid)))
      .leftMap(e => BadRequest(e.getMessage): APIError)
      .map(NoteResponse.fromEntity).value

  def updateNote(req: PutNoteRequest, id: Note.Id, uid: User.Id): F[Either[APIError, NoteResponse]] =
    (for {
      entity <- EitherT(NoteRepository[F].resolve(id, uid)).leftMap(e => NotFound(e.getMessage): APIError)
      note <- EitherT(NoteRepository[F].save(req.update(entity)))
        .leftMap(e => BadRequest(e.getMessage): APIError)
        .map(NoteResponse.fromEntity)
    } yield note).value

  def deleteNote(id: Note.Id, uid: User.Id): F[Either[APIError, Unit]] =
    EitherT(NoteRepository[F].delete(id, uid))
      .leftMap(e => BadRequest(e.getMessage): APIError).value
}
