package gateway.slick.repositories

import java.time.ZonedDateTime

import cats.{Monad, ~>}
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile

import domain.common.entities._
import domain.core.entities._
import domain.core.repositories.StockRepository

class StockRepositoryImpl[F[_]: Monad](
  jdbcProfile: JdbcProfile,
  execute: DBIO ~> F
) extends StockRepository[F] {
  import jdbcProfile.api._

  private val stocks = new StockTable(jdbcProfile).query

  def tmpStock: Stock =
    Stock(
      Stock.Id("stockId"),
      StockItem(
        "テスト",
        Url("http://url.com"),
        Url("http://image.com"),
      ),
      List(Tag(
        Tag.Id("tagId"),
        "tagName", Tag.Color("#fff"),
        ZonedDateTime.now(),
        ZonedDateTime.now()
      )),
      ZonedDateTime.now(),
      ZonedDateTime.now()
    )

  override def resolve(id: Stock.Id): F[Either[String, Stock]] =
      Monad.apply[F].pure(Right[String, Stock](tmpStock))

  def list: F[List[Stock]] =
    Monad.apply[F].pure(List(tmpStock))

  def save(value: Stock): F[Stock] =
    Monad.apply[F].pure(value)

  def delete(id: Stock.Id): F[Either[String, Unit]] =
    Monad.apply[F].pure(Right[String, Unit](()))
}
