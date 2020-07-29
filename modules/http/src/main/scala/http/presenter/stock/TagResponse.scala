package http.presenter.stock

import java.time.ZonedDateTime

import domain.core.entities.Tag

case class TagResponse (
  id: String,
  uid: String,
  name: String,
  color: String,
  createdAt: ZonedDateTime,
  updatedAt: ZonedDateTime
)

object TagResponse {
  def fromEntity(tag: Tag): TagResponse = {
    TagResponse(
      tag.id.value,
      tag.userId.value,
      tag.name,
      tag.color.value,
      tag.createdAt,
      tag.updatedAt
    )
  }
}
