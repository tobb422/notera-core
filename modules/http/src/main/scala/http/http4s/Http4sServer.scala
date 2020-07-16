package http.http4s

import cats.effect.{ConcurrentEffect, ContextShift, ExitCode, Timer}
import cats.implicits._
import org.http4s.implicits._
import org.http4s.Header
import org.http4s.server.blaze.BlazeServerBuilder
import domain.core.repositories.StockRepository
import shared.ddd.IdGenerator

class Http4sServer[F[_]: ContextShift: ConcurrentEffect: Timer: StockRepository](
  implicit idGen: IdGenerator[String]
) extends http.Server[F] {
  override def serve(host: String, port: Int): F[_] = {
    val services =
      new Http4sService[F].routes.map(
        _.putHeaders(
          Header("Content-Type", "application/json"),
          Header("charset", "UTF-8")
        )
      )

    BlazeServerBuilder[F]
      .bindHttp(port, host)
      .withHttpApp(services.orNotFound)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }
}
