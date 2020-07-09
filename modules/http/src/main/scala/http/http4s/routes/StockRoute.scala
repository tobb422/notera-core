package http.http4s.routes

import cats.effect._
import cats.implicits._
import fs2.Stream
import fs2.text.utf8Encode
import org.http4s._
import org.http4s.dsl.io._

import http.controllers.StockService

class StockRoute[F[_]: ConcurrentEffect] {
  private val service = new StockService[F]

  private val getStocks =
    HttpRoutes.of[F] {
      case GET -> Root / "stocks" =>
        service.getStocks.map {
          case Left(_) => Response(BadRequest)
          case Right(res) => Response(Ok, body = Stream(res).through(utf8Encode))
        }
    }

  private val getStock = {
    HttpRoutes.of[F] {
      case GET -> Root / "stocks" / IntVar(id) =>
        Response[F](Ok).pure[F]
    }
  }

  private val postStock = {
    HttpRoutes.of[F] {
      case POST -> Root / "stock" =>
        Response[F](Created).pure[F]
    }
  }

  private val putStock = {
    HttpRoutes.of[F] {
      case PUT -> Root / "stocks" / IntVar(id) =>
        Response[F](Ok).pure[F]
    }
  }

  private val deleteStock = {
    HttpRoutes.of[F] {
      case DELETE -> Root / "stocks" / IntVar(id) =>
        Response[F](NoContent).pure[F]
    }
  }

  val routes: HttpRoutes[F] = getStocks <+> getStock <+> postStock <+> putStock <+> deleteStock
}
