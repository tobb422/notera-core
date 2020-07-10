package domain.core.entities.stockItem

import domain.common.entities.Url
import domain.core.entities.StockItem

case class Book(
  title: String,
  url: Url,
  image: Url
) extends StockItem
