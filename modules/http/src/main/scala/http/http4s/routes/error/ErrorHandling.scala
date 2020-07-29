package http.http4s.routes.error

import cats.effect._
import http.controllers.error._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.Response
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class ErrorHandling[F[_]: Sync] extends Http4sDsl[F]  {
  def toRoutes(result: APIError): F[Response[F]] =
    result match {
      case r: BadRequest => BadRequest(r.asJson)
      case r: Forbidden => Forbidden(r.asJson)
      case r: NotFound => NotFound(r.asJson)
      case r: Conflict => Conflict(r.asJson)
      case r: UnprocessableEntity => UnprocessableEntity(r.asJson)
      case _ => InternalServerError("internal server error".asJson)
    }
}
