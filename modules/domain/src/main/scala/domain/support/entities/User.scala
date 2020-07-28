package domain.support.entities

import java.time.ZonedDateTime

import shared.ddd.{Entity, IdGenerator, Identifier}

case class User (
  id: User.Id,
  createdAt: ZonedDateTime,
  updatedAt: ZonedDateTime
) extends Entity {
  override type Id = User.Id
}

object User {
  case class Id(value: String) extends Identifier {
    override type IdType = String
  }
  object Id {
    protected[User] def getNextId()(implicit idGen: IdGenerator[String]): Id =
      Id(idGen.generate())
  }
}
