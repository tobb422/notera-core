package http.presenter.tag

import domain.core.entities.Tag
import domain.support.entities.User
import shared.ddd.IdGenerator

case class PostTagRequest(
  name: String,
  color: String
)(implicit idGen: IdGenerator[String]) {
  def toEntity(uid: User.Id): Tag =
    Tag.from(uid, name, color)
}
