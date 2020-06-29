package domain.stock.entities.core.stockItem

import domain.stock.entities.common.Url
import domain.stock.entities.core.StockItem

case class Article(
  title: String,
  url: Url,
  image: Url
) extends StockItem
