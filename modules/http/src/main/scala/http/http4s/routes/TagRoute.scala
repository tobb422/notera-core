package http.http4s.routes

import cats.effect._
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import domain.core.repositories.TagRepository
import http.controllers.TagService
import http.http4s.routes.error.ErrorHandling
import http.presenter.tag.{PostTagRequest, PutTagRequest}
import org.http4s.EntityDecoder
import org.http4s.dsl.Http4sDsl
import shared.ddd.IdGenerator

class TagRoute[F[_]: Sync: ConcurrentEffect: TagRepository](
  implicit val idGen: IdGenerator[String]
) extends Http4sDsl[F] {
  private val tmpUid = "uid01234567890uid123456789"
  private val service = new TagService[F]
  private val errorHandling = new ErrorHandling[F]

  implicit val postDdecoder: EntityDecoder[F, PostTagRequest] = jsonOf[F, PostTagRequest]
  implicit val putDecoder: EntityDecoder[F, PutTagRequest] = jsonOf[F, PutTagRequest]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "tags" =>
      service.getTags(tmpUid).flatMap {
        case Left(res) => errorHandling.toRoutes(res)
        case Right(res) => Ok(res.asJson)
      }

    case GET -> Root / "tags" / id =>
      service.getTag(id, tmpUid).flatMap {
        case Left(res) => errorHandling.toRoutes(res)
        case Right(res) => Ok(res.asJson)
      }

    case req @ POST -> Root / "tag" =>
      (for {
        r <- req.as[PostTagRequest]
        stock <- service.createTag(r, tmpUid)
      } yield stock).flatMap {
        case Left(res) => errorHandling.toRoutes(res)
        case Right(res) => Created(res.asJson)
      }

    case req @ PUT -> Root / "tags" / id =>
      (for {
        r <- req.as[PutTagRequest]
        stock <- service.updateTag(r, id, tmpUid)
      } yield stock).flatMap {
        case Left(res) => errorHandling.toRoutes(res)
        case Right(res) => Ok(res.asJson)
      }

    case DELETE -> Root / "tags" / id =>
      service.deleteTag(id).flatMap {
        case Left(res) => errorHandling.toRoutes(res)
        case Right(_) => NoContent()
      }
  }
}
