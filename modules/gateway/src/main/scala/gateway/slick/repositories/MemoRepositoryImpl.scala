package gateway.slick.repositories

import cats.implicits._
import cats.~>
import domain.core.entities.{Memo, Stock}
import domain.core.repositories.MemoRepository
import domain.support.entities.User
import gateway.slick.tables.MemoTable
import shared.ddd.{FailedToDeleteEntity, FailedToResolveById, FailedToSaveEntity}
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile

import scala.util.{Failure, Success}

class MemoRepositoryImpl[F[_]](
  jdbcProfile: JdbcProfile,
  execute: DBIO ~> F
)(implicit ec: scala.concurrent.ExecutionContext) extends MemoRepository[F] {
  import jdbcProfile.api._

  private val memos = new MemoTable(jdbcProfile).query

  override def resolve(id: Memo.Id, uid: User.Id): F[Either[FailedToResolveByMemoId, Memo]] = {
    val res = memos.filter(m => m.id === id.value && m.userId === uid.value).result.headOption.map {
      case Some(memo) => Right(memo)
      case None => FailedToResolveById[Memo](id).asLeft
    }

    execute.apply(res)
  }

  override def resolveByStockId(sid: Stock.Id, uid: User.Id): F[Seq[Memo]] = {
    val res = memos.filter(m => m.stockId === sid.value && m.userId === uid.value).result
    execute.apply(res)
  }

  override def save(value: Memo): F[Either[FailedToSaveMemo, Memo]] = {
    val res = memos.insertOrUpdate(value).asTry.map {
      case Success(_) => value.asRight
      case Failure(e) => FailedToSaveEntity[Memo](message = Option(e.getMessage)).asLeft
    }

    execute.apply(res)
  }

  override def delete(id: Memo.Id): F[Either[FailedToDeleteMemo, Unit]] = {
    val res = memos.filter(_.id === id.value).delete.asTry.map {
      case Success(_) => ().asRight
      case Failure(e) => FailedToDeleteEntity[Memo](message = Option(e.getMessage)).asLeft
    }

    execute.apply(res)
  }
}
