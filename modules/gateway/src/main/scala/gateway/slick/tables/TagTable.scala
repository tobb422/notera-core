package gateway.slick.tables

import java.sql.Timestamp
import java.time.ZoneId

import gateway.slick.SlickTable
import slick.jdbc.JdbcProfile
import domain.core.entities.{Tag => DTag}
import domain.support.entities.User
import slick.lifted.ProvenShape

protected[slick] class TagTable(val jdbcProfile: JdbcProfile) extends SlickTable {
  import jdbcProfile.api._

  override val tableName: String = "tags"

  class Schema(tag: Tag) extends Table[DTag](tag, tableName) {
    def id = column[String]("id", O.PrimaryKey)
    def userId = column[String]("user_id")
    def name = column[String]("name")
    def color = column[String]("color")
    def createdAt = column[Timestamp]("created_at")
    def updatedAt = column[Timestamp]("updated_at")

    def * : ProvenShape[DTag] =
      (id, userId, name, color, createdAt, updatedAt) <> (
        {
          case (id, userId, name, color, createdAt, updatedAt) =>
            new DTag(
              DTag.Id(id),
              User.Id(userId),
              name,
              DTag.Color(color),
              createdAt.toLocalDateTime.atZone(ZoneId.of("Asia/Tokyo")),
              updatedAt.toLocalDateTime.atZone(ZoneId.of("Asia/Tokyo"))
            )
        },
        {
          t: DTag =>
            Some(
              (
                t.id.value,
                t.userId.value,
                t.name,
                t.color.value,
                Timestamp.valueOf(t.createdAt.toLocalDateTime),
                Timestamp.valueOf(t.updatedAt.toLocalDateTime)
              )
            )
        }
      )
  }

  lazy val query = TableQuery[Schema]
}
