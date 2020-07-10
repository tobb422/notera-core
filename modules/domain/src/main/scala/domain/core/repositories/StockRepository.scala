package domain.core.repositories

import domain.core.entities.Stock

trait StockRepository[F[_]] {
  def resolve(id: Stock.Id): F[Either[String, Stock]]
  def list: F[List[Stock]]
//  def save(value: Stock): F[Stock]
//  def delete(id: Stock.Id): F[Either[String, Stock]]
}

object StockRepository {
  def apply[F[_]: StockRepository]: StockRepository[F] = implicitly[StockRepository[F]]
}
