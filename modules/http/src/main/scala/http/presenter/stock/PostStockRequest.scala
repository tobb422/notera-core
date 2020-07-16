package http.presenter.stock

import domain.common.entities.Url
import domain.core.entities.Stock
import domain.core.entities.stockItem.Article
import shared.ddd.IdGenerator

case class PostStockRequest(
  title: String,
  url: String,
  image: String
)(implicit idGen: IdGenerator[String]) {
  def toEntity: Stock =
    Stock.from(Article(title, Url(url), Url(image)))
}
