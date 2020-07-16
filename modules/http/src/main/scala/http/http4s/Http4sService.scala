package http.http4s

import cats.effect.ConcurrentEffect
import org.http4s._

import domain.core.repositories.StockRepository
import http.http4s.routes.StockRoute
import shared.ddd.IdGenerator

class Http4sService[F[_]: ConcurrentEffect: StockRepository](
  implicit val idGen: IdGenerator[String]
) {
  private val stock = new StockRoute[F]

  val routes: HttpRoutes[F] = stock.routes
}
