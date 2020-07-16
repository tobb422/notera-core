package http.http4s.routes

import cats.effect._
import cats.implicits._
import fs2.Stream
import fs2.text.utf8Encode
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import domain.core.repositories.StockRepository
import http.controllers.StockService
import http.presenter.stock.PostStockRequest
import shared.ddd.IdGenerator

class StockRoute[F[_]: ConcurrentEffect: StockRepository](
  implicit val idGen: IdGenerator[String]
) {
  private val service = new StockService[F]
  implicit val decoder: EntityDecoder[F, PostStockRequest] = jsonOf[F, PostStockRequest]

  private val getStocks =
    HttpRoutes.of[F] {
      case GET -> Root / "stocks" =>
        service.getStocks.map {
          case Left(res) => Response(BadRequest, body = Stream(res).through(utf8Encode))
          case Right(res) => Response(Ok, body = Stream(res.asJson.spaces2).through(utf8Encode))
        }
    }

  private val getStock = {
    HttpRoutes.of[F] {
      case GET -> Root / "stocks" / id =>
        service.getStock(id).map {
          case Left(res) => Response(BadRequest, body = Stream(res).through(utf8Encode))
          case Right(res) => Response(Ok, body = Stream(res.asJson.spaces2).through(utf8Encode))
        }
    }
  }

  private val postStock = {
    HttpRoutes.of[F] {
      case req @ POST -> Root / "stock" => {
        val result = for {
          r <- req.as[PostStockRequest]
          stock <- service.postStock(r)
        } yield stock

        result.map {
          case Left(res) => Response(BadRequest, body = Stream(res).through(utf8Encode))
          case Right(res) => Response(Created, body = Stream(res.asJson.spaces2).through(utf8Encode))
        }
      }
    }
  }

  private val deleteStock = {
    HttpRoutes.of[F] {
      case DELETE -> Root / "stocks" / id =>
        service.deleteStock(id).map {
          case Left(res) => Response(BadRequest, body = Stream(res).through(utf8Encode))
          case Right(_) => Response(NoContent)
        }
    }
  }

  val routes: HttpRoutes[F] = getStocks <+> getStock <+> postStock <+> deleteStock
}
