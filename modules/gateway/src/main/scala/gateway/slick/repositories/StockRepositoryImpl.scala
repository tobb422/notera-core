package gateway.slick.repositories

import java.time.ZonedDateTime

import cats._
import cats.implicits._
import cats.{Monad, ~>}
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile
import domain.core.entities._
import domain.core.repositories.StockRepository
import domain.support.entities.User
import gateway.slick.tables.{StockTable, StockTag, StockTagTable, TagTable}
import shared.ddd.{FailedToDeleteEntity, FailedToResolveById, FailedToSaveEntity}

import scala.util.{Failure, Success}

class StockRepositoryImpl[F[_]: Monad](
  jdbcProfile: JdbcProfile,
  execute: DBIO ~> F
)(implicit ec: scala.concurrent.ExecutionContext) extends StockRepository[F] {
  import jdbcProfile.api._

  private val stocks = new StockTable(jdbcProfile).query
  private val stockTags = new StockTagTable(jdbcProfile).query
  private val tags = new TagTable(jdbcProfile).query

  override def resolve(id: Stock.Id, uid: User.Id): F[Either[FailedToResolveByStockId, Stock]] = {
    val res = (for {
      q <- stocks.filter(s => s.id === id.value && s.userId === uid.value)
        .joinLeft(stockTags).on(_.id === _.stockId)
        .joinLeft(tags).on(_._2.map(_.tagId) === _.id)
    } yield (q._1._1, q._2)).result.asTry.map {
      case Success(res) if res.isEmpty =>
        FailedToResolveById[Stock](id).asLeft
      case Success(res) =>
        val target = res.groupBy(_._1).head
        target._1.mergeTags(target._2.flatMap(_._2)).asRight
      case Failure(_) => FailedToResolveById[Stock](id).asLeft
    }

    execute.apply(res)
  }

  override def list(uid: User.Id): F[Seq[Stock]] = {
    val res = (for {
      q <- stocks.filter(_.userId === uid.value)
        .joinLeft(stockTags).on(_.id === _.stockId)
        .joinLeft(tags).on(_._2.map(_.tagId) === _.id)
    } yield (q._1._1, q._2)).result.map(
      _.groupBy(_._1).map(l => l._1.mergeTags(l._2.flatMap(_._2))).toSeq)

    execute.apply(res)
  }

  override def save(stock: Stock, tagIds: Seq[Tag.Id] = Seq.empty[Tag.Id]): F[Either[FailedToSaveStock, Stock]] = {
    val res = (for {
      _ <- stocks += stock
      _ <- stockTags ++= (for { id <- tagIds } yield StockTag(stock.id.value, id.value, ZonedDateTime.now()))
      t <- tags.filter(_.id inSet tagIds.map(_.value)).result
    } yield t).asTry.map {
      case Success(tag) => stock.mergeTags(for (t <- tag) yield t).asRight
      case Failure(e) => FailedToSaveEntity[Stock](message = Option(e.getMessage)).asLeft
    }

    execute.apply(res)
  }

  def delete(id: Stock.Id): F[Either[FailedToDeleteStock, Unit]] = {
    val res = stocks.filter(_.id === id.value).delete.asTry.map {
      case Success(_) => ().asRight
      case Failure(e) => FailedToDeleteEntity[Stock](message = Option(e.getMessage)).asLeft
    }
    execute.apply(res)
  }
}
