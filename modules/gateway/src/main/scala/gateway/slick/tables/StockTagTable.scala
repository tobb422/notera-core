package gateway.slick.tables

import java.sql.Timestamp
import java.time.{ZoneId, ZonedDateTime}

import slick.jdbc.JdbcProfile
import gateway.slick.SlickTable
import slick.lifted.ProvenShape

case class StockTag(stockId: String, tagId: String, createdAt: ZonedDateTime)

protected[slick] class StockTagTable(val jdbcProfile: JdbcProfile) extends SlickTable {
  import jdbcProfile.api._

  override val tableName: String = "stock_tags"

  class Schema(tag: Tag) extends Table[StockTag](tag, tableName) {
    def stockId = column[String]("stock_id")
    def tagId = column[String]("tag_id")
    def createdAt = column[Timestamp]("created_at")

    def * : ProvenShape[StockTag] =
      (stockId, tagId, createdAt) <> (
        {
          case (stockId, tagId, createdAt) => StockTag(
            stockId,
            tagId,
            createdAt.toLocalDateTime.atZone(ZoneId.of("Asia/Tokyo")),
          )
        },
        {
          s: StockTag =>
            Some(
              (
                s.stockId,
                s.tagId,
                Timestamp.valueOf(s.createdAt.toLocalDateTime)
              )
            )

        }
      )
  }

  lazy val query = TableQuery[Schema]
}
