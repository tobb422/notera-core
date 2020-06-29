package domain.core.entities

import domain.common.entities.Url

trait StockItem {
  val title: String
  val url: Url
  val image: Url
}
