package http.http4s

import cats.Monad
import cats.data.{EitherT, Kleisli, OptionT}
import cats.effect.ConcurrentEffect
import cats.implicits._
import domain.Repositories
import domain.support.entities.User
import http.auth
import http.controllers.error.{APIError, Forbidden}
import http.http4s.routes.error.ErrorHandling
import org.http4s._
import http.http4s.routes.{StockRoute, TagRoute}
import org.http4s.headers.Authorization
import org.http4s.server.AuthMiddleware
import shared.auth.TokenVerifier
import shared.ddd.IdGenerator

class Http4sService[F[_]: Monad: ConcurrentEffect: Repositories](
  implicit val idGen: IdGenerator[String],
  implicit val tokenVerifier: TokenVerifier
) {
  val repositories: Repositories[F] = implicitly[Repositories[F]]
  import repositories._

  private val authUser: Kleisli[F, Request[F], Either[APIError, User]] = Kleisli { request =>
    val token = request.headers.get(Authorization).map(_.value)

    (for {
      uid <- EitherT.fromEither[F](auth.authenticate(token))
      user <- EitherT(repositories.userRepository.resolve(User.Id(uid)))
        .leftMap(e => Forbidden(e.getMessage): APIError)
    } yield user).value
  }
  private val errorHandling = new ErrorHandling[F]
  private val onFailure: AuthedRoutes[APIError, F] = Kleisli(req => OptionT.liftF(errorHandling.toRoutes(req.context)))
  private val middleware = AuthMiddleware(authUser, onFailure)

  private val stock = new StockRoute[F]
  private val tag = new TagRoute[F]

  val routes: HttpRoutes[F] = middleware(stock.routes <+> tag.routes)
}
