package http.presenter.stock

import java.time.ZonedDateTime

import io.circe.Encoder
import io.circe.generic.semiauto._

import domain.core.entities.Stock
import http.presenter.stock.resources.TagResource

case class StockResponse(
  id: String,
  title: String,
  url: String,
  image: String,
  tags: Seq[TagResource],
  createdAt: ZonedDateTime,
  updatedAt: ZonedDateTime
)

object StockResponse {
  implicit val encoder: Encoder[StockResponse] = deriveEncoder

  def fromEntity(stock: Stock): StockResponse = {
    StockResponse(
      stock.id.value,
      stock.item.title,
      stock.item.url.value,
      stock.item.image.value,
      stock.tags.map(TagResource.fromEntity),
      stock.createdAt,
      stock.updatedAt
    )
  }
}
