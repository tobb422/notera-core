package domain.core.entities

import java.time.ZonedDateTime

import domain.support.entities.User
import shared.ddd.{Entity, IdGenerator, Identifier}

case class Tag(
  id: Tag.Id,
  userId: User.Id,
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
  object Id {
    protected[Tag] def getNextId()(implicit idGen: IdGenerator[String]): Id =
      Id(idGen.generate())
  }

  case class Color(value: String)
}
