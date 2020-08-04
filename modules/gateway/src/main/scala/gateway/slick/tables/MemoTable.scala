package gateway.slick.tables

import java.sql.Timestamp
import java.time.ZoneId

import domain.core.entities.{Memo, Stock}
import domain.support.entities.User
import gateway.slick.SlickTable
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

protected[slick] class MemoTable(val jdbcProfile: JdbcProfile) extends SlickTable {
  import jdbcProfile.api._

  override val tableName: String = "memos"

  class Schema(tag: Tag) extends Table[Memo](tag, tableName) {
    def id = column[String]("id", O.PrimaryKey)
    def userId = column[String]("user_id")
    def stockId = column[String]("stock_id")
    def content = column[String]("content")
    def createdAt = column[Timestamp]("created_at")
    def updatedAt = column[Timestamp]("updated_at")

    def * : ProvenShape[Memo] =
      (id, userId, stockId, content, createdAt, updatedAt) <> (
        {
          case (id, userId, stockId, content, createdAt, updatedAt) =>
            new Memo(
              Memo.Id(id),
              User.Id(userId),
              Stock.Id(stockId),
              content,
              createdAt.toLocalDateTime.atZone(ZoneId.of("Asia/Tokyo")),
              updatedAt.toLocalDateTime.atZone(ZoneId.of("Asia/Tokyo"))
            )
        },
        {
          m: Memo =>
            Some(
              (
                m.id.value,
                m.userId.value,
                m.stockId.value,
                m.content,
                Timestamp.valueOf(m.createdAt.toLocalDateTime),
                Timestamp.valueOf(m.updatedAt.toLocalDateTime)
              )
            )
        }
      )
  }

  lazy val query = TableQuery[Schema]
}
