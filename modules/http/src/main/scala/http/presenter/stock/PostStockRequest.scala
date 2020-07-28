package http.presenter.stock

import domain.common.entities.Url
import domain.core.entities.{Stock, StockItem}
import shared.ddd.IdGenerator

case class PostStockRequest(
  title: String,
  url: String,
  image: String
)(implicit idGen: IdGenerator[String]) {
  def toEntity: Stock =
    Stock.from(StockItem(title, Url(url), Url(image)))
}
