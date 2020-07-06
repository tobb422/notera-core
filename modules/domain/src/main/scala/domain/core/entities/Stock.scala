package domain.core.entities

import java.time.ZonedDateTime

import shared.ddd.{Entity, IdGenerator, Identifier}

case class Stock(
  id: Stock.Id,
  item: StockItem,
  tags: List[Tag.Id],
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

  def from(item: StockItem)(implicit idGen: IdGenerator[String]): Stock = Stock(
    id = Id.getNextId(),
    item = item,
    tags = List(),
    createdAt = ZonedDateTime.now(),
    updatedAt = ZonedDateTime.now()
  )
}
