package domain.stock.entities.core.stockItem

import domain.common.entities.Url
import domain.core.entities.StockItem

case class Book(
  title: String,
  url: Url,
  image: Url
) extends StockItem