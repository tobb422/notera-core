package gateway.slick.tables

import java.time.ZonedDateTime
import slick.jdbc.JdbcProfile

import domain.core.entities.{Stock, StockItem}
import domain.common.entities.Url

import gateway.slick.SlickTable


protected[slick] class StockTable(val jdbcProfile: JdbcProfile) extends SlickTable {
  import jdbcProfile.api._

  override val tableName: String = "stocks"

  class Schema(tag: Tag) extends Table[Stock](tag, tableName) {
    def id = column[String]("id", O.PrimaryKey)
    def title = column[String]("title")
    def url = column[String]("url")
    def image = column[String]("image")
    def createdAt = column[ZonedDateTime]("created_at")
    def updatedAt = column[ZonedDateTime]("updated_at")

    def * =
      (id, title, url, image, createdAt, updatedAt) <> ({
        case (id, title, url, image, createdAt, updatedAt) =>
          new Stock(
            Stock.Id(id),
            StockItem(
              title,
              Url(url),
              Url(image),
            ),
            List(),
            createdAt,
            updatedAt
          )
      }, { s: Stock =>
        Some(
          (
            s.id.value,
            s.item.title,
            s.item.url.value,
            s.item.image.value,
            s.createdAt,
            s.updatedAt
          )
        )
      })
  }

  lazy val query = TableQuery[Schema]
}
