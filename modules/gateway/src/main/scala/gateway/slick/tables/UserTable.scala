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
    def uid = column[String]("uid")
    def createdAt = column[Timestamp]("created_at")
    def updatedAt = column[Timestamp]("updated_at")

    def * : ProvenShape[User] =
      (id, uid, createdAt, updatedAt) <> (
        {
          case (id, uid, createdAt, updatedAt) =>
            new User(
              User.Id(id),
              uid,
              createdAt.toLocalDateTime.atZone(ZoneId.of("Asia/Tokyo")),
              updatedAt.toLocalDateTime.atZone(ZoneId.of("Asia/Tokyo"))
            )
        },
        {
          t: User =>
            Some(
              (
                t.id.value,
                t.uid,
                Timestamp.valueOf(t.createdAt.toLocalDateTime),
                Timestamp.valueOf(t.updatedAt.toLocalDateTime)
              )
            )
        }
      )
  }

  lazy val query = TableQuery[Schema]
}
