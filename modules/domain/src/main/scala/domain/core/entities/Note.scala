package domain.core.entities

import java.time.ZonedDateTime

import domain.support.entities.User
import shared.ddd.{Entity, IdGenerator, Identifier}

case class Note(
  id: Note.Id,
  userId: User.Id,
  stockId: Stock.Id,
  content: String,
  createdAt: ZonedDateTime,
  updatedAt: ZonedDateTime
) extends Entity {
  override type Id = Note.Id
}

object Note {
  case class Id(value: String) extends Identifier {
    override type IdType = String
  }
  object Id {
    protected[Note] def getNextId()(implicit idGen: IdGenerator[String]): Id =
      Id(idGen.generate())
  }

  def from(uid: User.Id, sid: Stock.Id, content: String)(implicit idGen: IdGenerator[String]): Note = Note(
    id = Id.getNextId(),
    userId = uid,
    stockId = sid,
    content = content,
    createdAt = ZonedDateTime.now(),
    updatedAt = ZonedDateTime.now()
  )
}
