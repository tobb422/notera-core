package domain.core.repositories

import domain.core.entities.{Stock, Tag}
import domain.support.entities.User
import shared.ddd._

trait StockRepository[F[_]] {
  type FailedToResolveByStockId = FailedToResolveById[Stock]
  type FailedToSaveStock = FailedToSaveEntity[Stock]
  type FailedToDeleteStock = FailedToDeleteEntity[Stock]

  def resolve(id: Stock.Id, uid: User.Id): F[Either[FailedToResolveByStockId, Stock]]
  def list(uid: User.Id): F[Seq[Stock]]
  def save(value: Stock, tagsId: Seq[Tag.Id] = Seq.empty[Tag.Id]): F[Either[FailedToSaveStock, Stock]]
  def delete(id: Stock.Id): F[Either[FailedToDeleteStock, Unit]]
}

object StockRepository {
  def apply[F[_]: StockRepository]: StockRepository[F] = implicitly[StockRepository[F]]
}
