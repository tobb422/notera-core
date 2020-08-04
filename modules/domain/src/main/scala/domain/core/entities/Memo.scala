package domain.core.entities

import java.time.ZonedDateTime

import domain.support.entities.User
import shared.ddd.{Entity, IdGenerator, Identifier}

case class Memo(
  id: Memo.Id,
  userId: User.Id,
  stockId: Stock.Id,
  content: String,
  createdAt: ZonedDateTime,
  updatedAt: ZonedDateTime
) extends Entity {
  override type Id = Memo.Id
}

object Memo {
  case class Id(value: String) extends Identifier {
    override type IdType = String
  }
  object Id {
    protected[Memo] def getNextId()(implicit idGen: IdGenerator[String]): Id =
      Id(idGen.generate())
  }

  def from(uid: User.Id, sid: Stock.Id, content: String)(implicit idGen: IdGenerator[String]): Memo = Memo(
    id = Id.getNextId(),
    userId = uid,
    stockId = sid,
    content = content,
    createdAt = ZonedDateTime.now(),
    updatedAt = ZonedDateTime.now()
  )
}
