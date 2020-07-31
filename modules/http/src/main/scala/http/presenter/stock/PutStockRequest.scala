package http.presenter.stock

import domain.common.entities.Url
import domain.core.entities.{Stock, StockItem, Tag}

case class PutStockRequest(
  title: Option[String],
  url: Option[String],
  image: Option[String],
  tagIds: Option[Seq[String]]
) {
  def updateItem(stock: Stock): Stock =
    stock.copy(
      item = StockItem(
        title.getOrElse(stock.item.title),
        Url(url.getOrElse(stock.item.url.value)),
        Url(image.getOrElse(stock.item.image.value))
      ),
    )
  def toTagIdsEntity: Seq[Tag.Id] = tagIds.getOrElse(Seq.empty[String]).map(Tag.Id(_))
}
