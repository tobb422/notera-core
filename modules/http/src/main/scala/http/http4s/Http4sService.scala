package http.http4s

import cats.effect.ConcurrentEffect
import cats.Applicative
import org.http4s._
import org.http4s.dsl.io._

class Http4sService[F[_]: ConcurrentEffect] {
  private val helloWorldService = {
    HttpRoutes.of[F] {
      case GET -> Root / "hello" / name =>
        Applicative.apply[F].pure(Response[F](Ok))
    }
  }

  val routes: HttpRoutes[F] = helloWorldService
}
