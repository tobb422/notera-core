package gateway.slick

import slick.jdbc.JdbcProfile

protected[slick] trait SlickTable {
  val jdbcProfile: JdbcProfile
  val tableName: String
}
