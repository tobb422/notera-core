package domain.stock.entities.core

import java.time.ZonedDateTime

import shared.ddd.{Entity, Identifier}

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
}
