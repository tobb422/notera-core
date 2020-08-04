package domain.core.repositories

import domain.core.entities.{Memo, Stock}
import domain.support.entities.User
import shared.ddd.{FailedToDeleteEntity, FailedToResolveById, FailedToSaveEntity}

trait MemoRepository[F[_]] {
  type FailedToResolveByMemoId = FailedToResolveById[Memo]
  type FailedToResolveByStockId = FailedToResolveById[Stock]
  type FailedToSaveMemo = FailedToSaveEntity[Memo]
  type FailedToDeleteMemo = FailedToDeleteEntity[Memo]

  def resolve(id: Memo.Id, uid: User.Id): F[Either[FailedToResolveByMemoId, Memo]]
  def resolveByStockId(sid: Stock.Id, uid: User.Id): F[Seq[Memo]]
  def save(value: Memo): F[Either[FailedToSaveMemo, Memo]]
  def delete(id: Memo.Id): F[Either[FailedToDeleteMemo, Unit]]
}

object MemoRepository {
  def apply[F[_]: MemoRepository]: MemoRepository[F] = implicitly[MemoRepository[F]]
}
