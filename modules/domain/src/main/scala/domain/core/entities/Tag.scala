package domain.core.entities

import java.time.ZonedDateTime

import shared.ddd.{Entity, Identifier}

case class Tag(
  id: Tag.Id,
  name: String,
  color: Tag.Color,
  createdAt: ZonedDateTime,
  updatedAt: ZonedDateTime
) extends Entity {
  override type Id = Tag.Id
}

object Tag {
  case class Id(value: String) extends Identifier {
    override type IdType = String
  }
  case class Color(value: String)
}
