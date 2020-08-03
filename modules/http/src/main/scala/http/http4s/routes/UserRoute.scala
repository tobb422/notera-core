package http.http4s.routes

import cats.effect.{ConcurrentEffect, Sync}
import cats.implicits._
import domain.support.repositories.UserRepository
import http.controllers.UserService
import http.http4s.routes.error.ErrorHandling
import http.presenter.user.PostUserRequest
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.EntityDecoder
import org.http4s.dsl.Http4sDsl
import shared.ddd.IdGenerator

class UserRoute[F[_]: Sync: ConcurrentEffect: UserRepository](
  implicit val idGen: IdGenerator[String]
) extends Http4sDsl[F] {
  private val service = new UserService[F]
  private val errorHandling = new ErrorHandling[F]

  implicit val postDecoder: EntityDecoder[F, PostUserRequest] = jsonOf[F, PostUserRequest]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ POST -> Root / "user" =>
      (for {
        r <- req.as[PostUserRequest]
        user <- service.createUser(r)
      } yield user).flatMap {
        case Left(res) => errorHandling.toRoutes(res)
        case Right(res) => Created(res.asJson)
      }
  }
}
