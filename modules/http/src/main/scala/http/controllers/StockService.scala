package http.controllers

import scala.util.Either
import cats.Monad
import cats.data.EitherT
import domain.core.entities.Stock
import domain.core.repositories.StockRepository
import domain.support.entities.User
import http.presenter.stock.{PostStockRequest, StockResponse}
import shared.ddd.IdGenerator


class StockService[F[_]: Monad: StockRepository](
  implicit val idGen: IdGenerator[String]
) {
  def getStocks(uid: String): F[Either[String, List[StockResponse]]] = {
    val result = for {
      stocks <- EitherT.right[String](StockRepository[F].list(User.Id(uid)))
    } yield stocks.map(StockResponse.fromEntity)

    result.value
  }

  def getStock(id: String, uid: String): F[Either[String, StockResponse]] = {
    val result = for {
      stock <- EitherT(StockRepository[F].resolve(Stock.Id(id), User.Id(uid)))
    } yield StockResponse.fromEntity(stock)

    result.value
  }

  def postStock(req: PostStockRequest): F[Either[String, StockResponse]] = {
    val result = for {
      newStock <- EitherT.fromEither[F](Right[String, Stock](req.toEntity))
      stock <- EitherT(StockRepository[F].save(newStock))
    } yield StockResponse.fromEntity(stock)

    result.value
  }

  def deleteStock(id: String): F[Either[String, Unit]] =
    StockRepository[F].delete(Stock.Id(id))
}
