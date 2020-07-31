package http.http4s.routes

import cats.effect._
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import domain.core.repositories.{StockRepository, TagRepository}
import http.controllers.StockService
import http.http4s.routes.error.ErrorHandling
import http.presenter.stock.PostStockRequest
import org.http4s.dsl.Http4sDsl
import shared.ddd.IdGenerator

class StockRoute[F[_]: Sync: ConcurrentEffect: StockRepository: TagRepository](
  implicit val idGen: IdGenerator[String]
) extends Http4sDsl[F] {
  private val tmpUid = "uid01234567890uid123456789"
  private val service = new StockService[F]
  private val errorHandling = new ErrorHandling[F]

  implicit val postDecoder: EntityDecoder[F, PostStockRequest] = jsonOf[F, PostStockRequest]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "stocks" =>
      service.getStocks(tmpUid).flatMap {
        case Left(res) => errorHandling.toRoutes(res)
        case Right(res) => Ok(res.asJson)
      }

    case GET -> Root / "stocks" / id =>
      service.getStock(id, tmpUid).flatMap {
        case Left(res) => errorHandling.toRoutes(res)
        case Right(res) => Ok(res.asJson)
      }

    case req @ POST -> Root / "stock" =>
      (for {
        r <- req.as[PostStockRequest]
        stock <- service.createStock(r, tmpUid)
      } yield stock).flatMap {
        case Left(res) => errorHandling.toRoutes(res)
        case Right(res) => Created(res.asJson)
      }

    case DELETE -> Root / "stocks" / id =>
      service.deleteStock(id).flatMap {
        case Left(res) => errorHandling.toRoutes(res)
        case Right(_) => NoContent()
      }
  }
}
