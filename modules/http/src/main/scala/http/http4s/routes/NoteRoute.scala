package http.http4s.routes

import cats.effect.{ConcurrentEffect, Sync}
import cats.implicits._
import domain.core.entities.{Note, Stock}
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.EntityDecoder
import org.http4s.dsl.Http4sDsl
import domain.core.repositories.NoteRepository
import domain.support.entities.User
import http.controllers.NoteService
import http.http4s.routes.error.ErrorHandling
import http.presenter.note.{PostNoteRequest, PutNoteRequest}
import shared.ddd.IdGenerator

class NoteRoute[F[_]: Sync: ConcurrentEffect: NoteRepository](
  implicit val idGen: IdGenerator[String]
) extends Http4sDsl[F] {
  private val service = new NoteService[F]
  private val errorHandling = new ErrorHandling[F]

  implicit val postDecoder: EntityDecoder[F, PostNoteRequest] = jsonOf[F, PostNoteRequest]
  implicit val putDecoder: EntityDecoder[F, PutNoteRequest] = jsonOf[F, PutNoteRequest]

  val routes: AuthedRoutes[User, F] = AuthedRoutes.of[User, F] {
    case GET -> Root / "memo" / id as user =>
      service.getNote(Note.Id(id), user.id).flatMap {
        case Left(res) => errorHandling.toRoutes(res)
        case Right(res) => Ok(res.asJson)
      }

    case GET -> Root / "stocks" / stockId / "memos" as user =>
      service.getNotes(Stock.Id(stockId), user.id).flatMap {
        case Left(res) => errorHandling.toRoutes(res)
        case Right(res) => Ok(res.asJson)
      }

    case authReq @ POST -> Root / "memo" as user =>
      (for {
        r <- authReq.req.as[PostNoteRequest]
        memo <- service.createNote(r, user.id)
      } yield memo).flatMap {
        case Left(res) => errorHandling.toRoutes(res)
        case Right(res) => Created(res.asJson)
      }

    case authReq @ PUT -> Root  / "memos" / id as user =>
      (for {
        r <- authReq.req.as[PutNoteRequest]
        memo <- service.updateNote(r, Note.Id(id), user.id)
      } yield memo).flatMap {
        case Left(res) => errorHandling.toRoutes(res)
        case Right(res) => Ok(res.asJson)
      }

    case DELETE -> Root / "memos" / id as user =>
      service.deleteNote(Note.Id(id), user.id).flatMap {
        case Left(res) => errorHandling.toRoutes(res)
        case Right(_) => NoContent()
      }
  }
}
