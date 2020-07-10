package http.presenter.stock.resources

import io.circe.Encoder
import io.circe.generic.semiauto._

import domain.core.entities.Tag

case class TagResource(
  id: String,
  name: String,
  color: String
)

object TagResource {
  implicit val encoder: Encoder[TagResource] = deriveEncoder

  def fromEntity(tag: Tag): TagResource =
    TagResource(tag.id.value, tag.name, tag.color.value)
}
