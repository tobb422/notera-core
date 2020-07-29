package http.http4s

import cats.effect.ConcurrentEffect
import domain.Repositories
import org.http4s._
import http.http4s.routes.StockRoute
import shared.ddd.IdGenerator

class Http4sService[F[_]: ConcurrentEffect: Repositories](
  implicit val idGen: IdGenerator[String]
) {
  val repositories: Repositories[F] = implicitly[Repositories[F]]
  import repositories._

  private val stock = new StockRoute[F]

  val routes: HttpRoutes[F] = stock.routes
}
