package http.http4s.routes

import cats.effect._
import cats.implicits._
import domain.core.entities.Stock
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import domain.core.repositories.{StockRepository, TagRepository}
import domain.support.entities.User
import http.controllers.StockService
import http.http4s.routes.error.ErrorHandling
import http.presenter.stock.{PostStockRequest, PutStockRequest}
import org.http4s.dsl.Http4sDsl
import shared.ddd.IdGenerator

class StockRoute[F[_]: Sync: ConcurrentEffect: StockRepository: TagRepository](
  implicit val idGen: IdGenerator[String]
) extends Http4sDsl[F] {
  private val service = new StockService[F]
  private val errorHandling = new ErrorHandling[F]

  implicit val postDecoder: EntityDecoder[F, PostStockRequest] = jsonOf[F, PostStockRequest]
  implicit val putDecoder: EntityDecoder[F, PutStockRequest] = jsonOf[F, PutStockRequest]

  val routes: AuthedRoutes[User, F] = AuthedRoutes.of[User, F] {
    case GET -> Root / "stocks" as user =>
      service.getStocks(user.id).flatMap {
        case Left(res) => errorHandling.toRoutes(res)
        case Right(res) => Ok(res.asJson)
      }

    case GET -> Root / "stocks" / id as user =>
      service.getStock(Stock.Id(id), user.id).flatMap {
        case Left(res) => errorHandling.toRoutes(res)
        case Right(res) => Ok(res.asJson)
      }

    case authReq @ POST -> Root / "stock" as user =>
      (for {
        r <- authReq.req.as[PostStockRequest]
        stock <- service.createStock(r, user.id)
      } yield stock).flatMap {
        case Left(res) => errorHandling.toRoutes(res)
        case Right(res) => Created(res.asJson)
      }

    case authReq @ PUT -> Root / "stocks" / id as user =>
      (for {
        r <- authReq.req.as[PutStockRequest]
        stock <- service.updateStock(r, Stock.Id(id), user.id)
      } yield stock).flatMap {
        case Left(res) => errorHandling.toRoutes(res)
        case Right(res) => Ok(res.asJson)
      }

    case DELETE -> Root / "stocks" / id as user =>
      service.deleteStock(Stock.Id(id), user.id).flatMap {
        case Left(res) => errorHandling.toRoutes(res)
        case Right(_) => NoContent()
      }
  }
}
