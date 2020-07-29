package http.presenter.stock.resources

import domain.core.entities.Tag

case class TagResource(
  id: String,
  name: String,
  color: String,
)

object TagResource {
  def fromEntity(tag: Tag): TagResource =
    TagResource(tag.id.value, tag.name, tag.color.value)
}
