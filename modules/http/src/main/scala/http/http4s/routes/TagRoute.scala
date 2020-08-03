package http.http4s.routes

import cats.effect._
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import domain.core.repositories.TagRepository
import domain.support.entities.User
import http.controllers.TagService
import http.http4s.routes.error.ErrorHandling
import http.presenter.tag.{PostTagRequest, PutTagRequest}
import org.http4s.EntityDecoder
import org.http4s.dsl.Http4sDsl
import shared.ddd.IdGenerator

class TagRoute[F[_]: Sync: ConcurrentEffect: TagRepository](
  implicit val idGen: IdGenerator[String]
) extends Http4sDsl[F] {
  private val service = new TagService[F]
  private val errorHandling = new ErrorHandling[F]

  implicit val postDecoder: EntityDecoder[F, PostTagRequest] = jsonOf[F, PostTagRequest]
  implicit val putDecoder: EntityDecoder[F, PutTagRequest] = jsonOf[F, PutTagRequest]

  val routes: AuthedRoutes[User, F] = AuthedRoutes.of[User, F] {
    case GET -> Root / "tags" as user =>
      service.getTags(user.id.value).flatMap {
        case Left(res) => errorHandling.toRoutes(res)
        case Right(res) => Ok(res.asJson)
      }

    case GET -> Root / "tags" / id as user =>
      service.getTag(id, user.id.value).flatMap {
        case Left(res) => errorHandling.toRoutes(res)
        case Right(res) => Ok(res.asJson)
      }

    case authReq @ POST -> Root / "tag" as user =>
      (for {
        r <- authReq.req.as[PostTagRequest]
        stock <- service.createTag(r, user.id.value)
      } yield stock).flatMap {
        case Left(res) => errorHandling.toRoutes(res)
        case Right(res) => Created(res.asJson)
      }

    case authReq @ PUT -> Root / "tags" / id as user =>
      (for {
        r <- authReq.req.as[PutTagRequest]
        stock <- service.updateTag(r, id, user.id.value)
      } yield stock).flatMap {
        case Left(res) => errorHandling.toRoutes(res)
        case Right(res) => Ok(res.asJson)
      }

    case DELETE -> Root / "tags" / id as _ =>
      service.deleteTag(id).flatMap {
        case Left(res) => errorHandling.toRoutes(res)
        case Right(_) => NoContent()
      }
  }
}
