package gateway.slick.tables

import slick.jdbc.JdbcProfile
import gateway.slick.SlickTable
import slick.lifted.ProvenShape

case class StockTag(stockId: String, tagId: String)

protected[slick] class StockTagTable(val jdbcProfile: JdbcProfile) extends SlickTable {
  import jdbcProfile.api._

  override val tableName: String = "stock_tags"

  class Schema(tag: Tag) extends Table[StockTag](tag, tableName) {
    def stockId = column[String]("stock_id")
    def tagId = column[String]("tag_id")

    def * : ProvenShape[StockTag] =
      (stockId, tagId) <> (StockTag.tupled, StockTag.unapply)
  }

  lazy val query = TableQuery[Schema]
}
