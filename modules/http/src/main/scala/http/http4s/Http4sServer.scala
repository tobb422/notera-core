package http.http4s

import cats.effect.{ConcurrentEffect, ContextShift, ExitCode, Timer}
import cats.implicits._
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder

class Http4sServer[F[_]: ContextShift: ConcurrentEffect: Timer] extends http.Server[F] {
  override def serve(host: String, port: Int): F[_] = {
    BlazeServerBuilder[F]
      .bindHttp(port, host)
      .withHttpApp(new Http4sService[F].routes.orNotFound)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }
}
