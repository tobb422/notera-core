package domain.core.repositories

import domain.core.entities.Stock
import domain.support.entities.User

trait StockRepository[F[_]] {
  def resolve(id: Stock.Id, uid: User.Id): F[Either[String, Stock]]
  def list(uid: User.Id): F[List[Stock]]
  def save(value: Stock): F[Either[String, Stock]]
  def delete(id: Stock.Id): F[Either[String, Unit]]
}

object StockRepository {
  def apply[F[_]: StockRepository]: StockRepository[F] = implicitly[StockRepository[F]]
}
