package http.presenter.tag

import domain.core.entities.Tag

case class PutTagRequest(
  name: Option[String],
  color: Option[String]
) {
  def updateEntity(tag: Tag): Tag =
    tag.copy(
      name = name.getOrElse(tag.name),
      color = Tag.Color(color.getOrElse(tag.color.value))
    )
}
