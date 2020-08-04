package http.http4s.routes

import cats.effect.{ConcurrentEffect, Sync}
import cats.implicits._
import domain.core.entities.{Memo, Stock}
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.EntityDecoder
import org.http4s.dsl.Http4sDsl
import domain.core.repositories.MemoRepository
import domain.support.entities.User
import http.controllers.MemoService
import http.http4s.routes.error.ErrorHandling
import http.presenter.memo.{PostMemoRequest, PutMemoRequest}
import shared.ddd.IdGenerator

class MemoRoute[F[_]: Sync: ConcurrentEffect: MemoRepository](
  implicit val idGen: IdGenerator[String]
) extends Http4sDsl[F] {
  private val service = new MemoService[F]
  private val errorHandling = new ErrorHandling[F]

  implicit val postDecoder: EntityDecoder[F, PostMemoRequest] = jsonOf[F, PostMemoRequest]
  implicit val putDecoder: EntityDecoder[F, PutMemoRequest] = jsonOf[F, PutMemoRequest]

  val routes: AuthedRoutes[User, F] = AuthedRoutes.of[User, F] {
    case GET -> Root / "memo" / id as user =>
      service.getMemo(Memo.Id(id), user.id).flatMap {
        case Left(res) => errorHandling.toRoutes(res)
        case Right(res) => Ok(res.asJson)
      }

    case GET -> Root / "stocks" / stockId / "memos" as user =>
      service.getMemos(Stock.Id(stockId), user.id).flatMap {
        case Left(res) => errorHandling.toRoutes(res)
        case Right(res) => Ok(res.asJson)
      }

    case authReq @ POST -> Root / "memo" as user =>
      (for {
        r <- authReq.req.as[PostMemoRequest]
        memo <- service.createMemo(r, user.id)
      } yield memo).flatMap {
        case Left(res) => errorHandling.toRoutes(res)
        case Right(res) => Created(res.asJson)
      }

    case authReq @ PUT -> Root  / "memos" / id as user =>
      (for {
        r <- authReq.req.as[PutMemoRequest]
        memo <- service.updateMemo(r, Memo.Id(id), user.id)
      } yield memo).flatMap {
        case Left(res) => errorHandling.toRoutes(res)
        case Right(res) => Ok(res.asJson)
      }

    case DELETE -> Root / "memos" / id as _ =>
      service.deleteMemo(Memo.Id(id)).flatMap {
        case Left(res) => errorHandling.toRoutes(res)
        case Right(_) => NoContent()
      }
  }
}
