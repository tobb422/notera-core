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
}

object Stock {
  case class Id(value: String) extends Identifier {
    override type IdType = String
  }
  object Id {
    protected[Stock] def getNextId()(implicit idGen: IdGenerator[String]): Id =
      Id(idGen.generate())
  }

  def from(uid: User.Id, item: StockItem)(implicit idGen: IdGenerator[String]): Stock = Stock(
    id = Id.getNextId(),
    userId = uid,
    item = item,
    tags = Seq(),
    createdAt = ZonedDateTime.now(),
    updatedAt = ZonedDateTime.now()
  )
}
