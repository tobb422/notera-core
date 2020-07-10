package http.controllers

import scala.util.Either

import cats.Monad
import cats.data.EitherT

import domain.core.entities.Stock
import domain.core.repositories.StockRepository
import http.presenter.stock.GetStockResponse


class StockService[F[_]: Monad: StockRepository] {
  def getStocks: F[Either[String, List[GetStockResponse]]] = {
    val result = for {
      stocks <- EitherT.right[String](StockRepository[F].list)
    } yield stocks.map(GetStockResponse.fromEntity)

    result.value
  }

  def getStock(id: String): F[Either[String, GetStockResponse]] = {
    val result = for {
      stock <- EitherT(StockRepository[F].resolve(Stock.Id(id)))
    } yield GetStockResponse.fromEntity(stock)

    result.value
  }
}
