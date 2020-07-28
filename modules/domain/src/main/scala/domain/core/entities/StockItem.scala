package domain.core.entities

import domain.common.entities.Url

case class StockItem(
  title: String,
  url: Url,
  image: Url
)
