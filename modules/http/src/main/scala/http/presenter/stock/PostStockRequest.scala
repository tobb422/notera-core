package http.presenter.stock

import domain.common.entities.Url
import domain.core.entities.{Stock, StockItem}
import domain.support.entities.User
import shared.ddd.IdGenerator

case class PostStockRequest(
  title: String,
  url: String,
  image: String
)(implicit idGen: IdGenerator[String]) {
  def toEntity(uid: String): Stock =
    Stock.from(User.Id(uid), StockItem(title, Url(url), Url(image)))
}
