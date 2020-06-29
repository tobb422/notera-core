package domain.stock.entities.core

import domain.stock.entities.common.Url

trait StockItem {
  val title: String
  val url: Url
  val image: Url
}
