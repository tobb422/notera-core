package http.http4s.route

import cats.effect.ConcurrentEffect
import cats.implicits._
import cats.Applicative
import org.http4s._
import org.http4s.dsl.io._

class StockService[F[_]: ConcurrentEffect] {
  private val getStocks = {
    HttpRoutes.of[F] {
      case GET -> Root / "stocks" =>
        Applicative.apply[F].pure(Response[F](Ok))
    }
  }

  private val getStock = {
    HttpRoutes.of[F] {
      case GET -> Root / "stocks" / IntVar(id) =>
        Applicative.apply[F].pure(Response[F](Ok))
    }
  }

  private val postStock = {
    HttpRoutes.of[F] {
      case POST -> Root / "stock" =>
        Applicative.apply[F].pure(Response[F](Created))
    }
  }

  private val putStock = {
    HttpRoutes.of[F] {
      case PUT -> Root / "stocks" / IntVar(id) =>
        Applicative.apply[F].pure(Response[F](Ok))
    }
  }

  private val deleteStock = {
    HttpRoutes.of[F] {
      case DELETE -> Root / "stocks" / IntVar(id) =>
        Applicative.apply[F].pure(Response[F](NoContent))
    }
  }

  val routes: HttpRoutes[F] = getStocks <+> getStock <+> postStock <+> putStock <+> deleteStock
}
