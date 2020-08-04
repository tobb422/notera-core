package domain.core.repositories

import domain.core.entities.Stock
import domain.support.entities.User
import shared.ddd._

trait StockRepository[F[_]] {
  type FailedToResolveByStockId = FailedToResolveById[Stock]
  type FailedToSaveStock = FailedToSaveEntity[Stock]
  type FailedToDeleteStock = FailedToDeleteEntity[Stock]

  def resolve(id: Stock.Id, uid: User.Id): F[Either[FailedToResolveByStockId, Stock]]
  def list(uid: User.Id): F[Seq[Stock]]
  def insert(value: Stock): F[Either[FailedToSaveStock, Stock]]
  def update(value: Stock): F[Either[FailedToSaveStock, Stock]]
  def delete(id: Stock.Id, uid: User.Id): F[Either[FailedToDeleteStock, Unit]]
}

object StockRepository {
  def apply[F[_]: StockRepository]: StockRepository[F] = implicitly[StockRepository[F]]
}
