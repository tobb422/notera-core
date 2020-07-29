package gateway.slick.tables

import java.sql.Timestamp
import java.time.ZoneId

import slick.jdbc.JdbcProfile
import domain.core.entities.{Stock, StockItem}
import domain.common.entities.Url
import domain.support.entities.User
import gateway.slick.SlickTable
import slick.lifted.ProvenShape

protected[slick] class StockTable(val jdbcProfile: JdbcProfile) extends SlickTable {
  import jdbcProfile.api._

  override val tableName: String = "stocks"

  class Schema(tag: Tag) extends Table[Stock](tag, tableName) {
    def id = column[String]("id", O.PrimaryKey)
    def userId = column[String]("user_id")
    def title = column[String]("title")
    def url = column[String]("url")
    def image = column[String]("image")
    def createdAt = column[Timestamp]("created_at")
    def updatedAt = column[Timestamp]("updated_at")

    def * : ProvenShape[Stock] =
      (id, userId, (title, url, image), createdAt, updatedAt).shaped <> (
        {
          case (id, userId, item, createdAt, updatedAt) =>
            new Stock(
              Stock.Id(id),
              User.Id(userId),
              StockItem(item._1, Url(item._2), Url(item._3)),
              List(),
              createdAt.toLocalDateTime.atZone(ZoneId.of("Asia/Tokyo")),
              updatedAt.toLocalDateTime.atZone(ZoneId.of("Asia/Tokyo"))
            )
        },
        {
          s: Stock =>
            Some(
              (
                s.id.value,
                s.userId.value,
                (s.item.title, s.item.url.value, s.item.image.value),
                Timestamp.valueOf(s.createdAt.toLocalDateTime),
                Timestamp.valueOf(s.updatedAt.toLocalDateTime),
              )
            )
        }
      )
  }

  lazy val query = TableQuery[Schema]
}
