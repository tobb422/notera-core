package http.presenter.stock

import domain.common.entities.Url
import domain.core.entities.{Stock, StockItem, Tag}
import domain.support.entities.User
import shared.ddd.IdGenerator

case class PostStockRequest(
  title: String,
  url: String,
  image: String,
  tagIds: Seq[String]
)(implicit idGen: IdGenerator[String]) {
  def toStockEntity(uid: User.Id): Stock =
    Stock.from(uid, StockItem(title, Url(url), Url(image)))

  def toTagIdsEntity: Seq[Tag.Id] = tagIds.map(Tag.Id(_))
}
