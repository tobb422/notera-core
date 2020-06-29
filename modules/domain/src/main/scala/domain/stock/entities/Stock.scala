package domain.stock.entities

import java.time.ZonedDateTime

case class StockItem(id: String, url: String, createdAt: ZonedDateTime, updatedAt: ZonedDateTime)
case class Tag(id: String, name: String, color: String)

case class Stock(id: String, itemId: StockItem.Id, tags: Seq[Tag], createdAt: ZonedDateTime, updatedAt: ZonedDateTime)
