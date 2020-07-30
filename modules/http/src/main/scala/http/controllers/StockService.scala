package http.controllers

import scala.util.Either
import cats.Monad
import cats.data.EitherT
import domain.core.entities.Stock
import domain.core.repositories.StockRepository
import domain.support.entities.User
import http.controllers.error.{APIError, BadRequest, NotFound}
import http.presenter.stock.{PostStockRequest, StockResponse, StocksResponse}
import shared.ddd.IdGenerator

class StockService[F[_]: Monad: StockRepository](
  implicit val idGen: IdGenerator[String]
) {
  def getStocks(uid: String): F[Either[APIError, StocksResponse]] = {
    val res = for {
      stocks <- EitherT.right[APIError](StockRepository[F].list(User.Id(uid)))
    } yield StocksResponse(stocks.map(StockResponse.fromEntity))

    res.value
  }

  def getStock(id: String, uid: String): F[Either[APIError, StockResponse]] = {
    val res = for {
      stock <- EitherT(
        StockRepository[F].resolve(Stock.Id(id), User.Id(uid))
      ).leftMap(e => NotFound(e.getMessage): APIError)
    } yield StockResponse.fromEntity(stock)

    res.value
  }

  def postStock(req: PostStockRequest, uid: String): F[Either[APIError, StockResponse]] = {
    val res = for {
      stock <- EitherT(
        StockRepository[F].save(req.toStockEntity(uid), req.toTagIdsEntity)
      ).leftMap(e => BadRequest(e.getMessage): APIError)
    } yield StockResponse.fromEntity(stock)

    res.value
  }

  def deleteStock(id: String): F[Either[APIError, Unit]] = {
    val res = for {
      _ <- EitherT(
        StockRepository[F].delete(Stock.Id(id))
      ).leftMap(e => BadRequest(e.getMessage): APIError)
    } yield ()

    res.value
  }
}
