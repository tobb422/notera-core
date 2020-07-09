package http.http4s

import cats.effect.ConcurrentEffect
import org.http4s._

import http.http4s.routes.StockRoute

class Http4sService[F[_]: ConcurrentEffect] {
  private val stock = new StockRoute[F]

  val routes: HttpRoutes[F] = stock.routes
}
