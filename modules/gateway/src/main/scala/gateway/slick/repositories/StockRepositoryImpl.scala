package gateway.slick.repositories

import cats.implicits._
import cats.{Monad, ~>}
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile
import domain.core.entities._
import domain.core.repositories.StockRepository
import domain.support.entities.User
import gateway.slick.tables.StockTable

import scala.util.{Failure, Success}

class StockRepositoryImpl[F[_]: Monad](
  jdbcProfile: JdbcProfile,
  execute: DBIO ~> F
)(implicit ec: scala.concurrent.ExecutionContext) extends StockRepository[F] {
  import jdbcProfile.api._

  private val stocks = new StockTable(jdbcProfile).query

  override def resolve(id: Stock.Id, uid: User.Id): F[Either[String, Stock]] = {
    val res = stocks.filter(s => s.id === id.value && s.userId === uid.value).result.headOption.map {
      case Some(stock) => Right(stock)
      case None => Left("")
    }
    execute.apply(res)
  }

  override def list(uid: User.Id): F[List[Stock]] = {
    val res = stocks.filter(_.userId === uid.value).result
    execute.apply(res)
  }

  override def save(value: Stock): F[Either[String, Stock]] = {
    val res = stocks.insertOrUpdate(value).asTry.map {
      case Success(_) => value.asRight
      case Failure(e) => e.toString.asLeft
    }
    execute.apply(res)
  }

  def delete(id: Stock.Id): F[Either[String, Unit]] = {
    val res = stocks.filter(_.id === id.value).delete.asTry.map {
      case Success(_) => ().asRight
      case Failure(e) => e.toString.asLeft
    }
    execute.apply(res)
  }
}
