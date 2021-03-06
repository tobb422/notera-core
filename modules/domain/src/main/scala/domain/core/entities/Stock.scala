package domain.core.entities

import java.time.ZonedDateTime

import domain.support.entities.User
import shared.ddd.{Entity, IdGenerator, Identifier}

case class Stock(
  id: Stock.Id,
  userId: User.Id,
  item: StockItem,
  tags: Seq[Tag],
  createdAt: ZonedDateTime,
  updatedAt: ZonedDateTime
) extends Entity {
  override type Id = Stock.Id

  def mergeTags(tags: Seq[Tag]): Stock =
    copy(tags = tags)
}

object Stock {
  case class Id(value: String) extends Identifier {
    override type IdType = String
  }
  object Id {
    protected[Stock] def getNextId()(implicit idGen: IdGenerator[String]): Id =
      Id(idGen.generate())
  }

  def from(uid: User.Id, item: StockItem, tags: Seq[Tag] = Seq.empty[Tag])(implicit idGen: IdGenerator[String]): Stock = Stock(
    id = Id.getNextId(),
    userId = uid,
    item = item,
    tags = tags,
    createdAt = ZonedDateTime.now(),
    updatedAt = ZonedDateTime.now()
  )
}
