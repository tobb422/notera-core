package gateway.slick.tables

import java.sql.Timestamp
import java.time.ZoneId

import gateway.slick.SlickTable
import slick.jdbc.JdbcProfile
import domain.support.entities.User
import slick.lifted.ProvenShape

protected[slick] class UserTable(val jdbcProfile: JdbcProfile) extends SlickTable {
  import jdbcProfile.api._

  override val tableName: String = "users"

  class Schema(tag: Tag) extends Table[User](tag, tableName) {
    def id = column[String]("id", O.PrimaryKey)
    def createdAt = column[Timestamp]("created_at")
    def updatedAt = column[Timestamp]("updated_at")

    def * : ProvenShape[User] =
      (id, createdAt, updatedAt) <> (
        {
          case (id, createdAt, updatedAt) =>
            new User(
              User.Id(id),
              createdAt.toLocalDateTime.atZone(ZoneId.of("Asia/Tokyo")),
              updatedAt.toLocalDateTime.atZone(ZoneId.of("Asia/Tokyo"))
            )
        },
        {
          t: User =>
            Some(
              (
                t.id.value,
                Timestamp.valueOf(t.createdAt.toLocalDateTime),
                Timestamp.valueOf(t.updatedAt.toLocalDateTime)
              )
            )
        }
      )
  }

  lazy val query = TableQuery[Schema]
}
