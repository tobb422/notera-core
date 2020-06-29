package domain.stock.entities

import java.time.ZonedDateTime

import shared.ddd.{Entity, Identifier}

case class StockItem(
  id: StockItem.Id,
  url: StockItem.Url,
  createdAt: ZonedDateTime,
  updatedAt: ZonedDateTime
) extends Entity {
  override type Id = StockItem.Id
}

object StockItem {
  case class Id(value: String) extends Identifier {
    override type IdType = String
  }

  case class Url(value: String)
}
