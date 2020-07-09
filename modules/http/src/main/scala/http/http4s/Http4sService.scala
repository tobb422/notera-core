package http.http4s

import cats.effect.ConcurrentEffect
import org.http4s._

import http.http4s.route.StockService

class Http4sService[F[_]: ConcurrentEffect] {
  private val stockService = new StockService[F]

  val routes: HttpRoutes[F] = stockService.routes
}
