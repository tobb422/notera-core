package shared.ddd

import java.time.ZonedDateTime

trait Entity {
  type Id <: Identifier
  val id: Id
  val createdAt: ZonedDateTime
  val updatedAt: ZonedDateTime
}

trait Identifier {
  type IdType
  val value: IdType
}
