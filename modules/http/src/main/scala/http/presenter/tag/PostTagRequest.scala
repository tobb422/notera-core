package http.presenter.tag

import domain.core.entities.Tag
import domain.support.entities.User
import shared.ddd.IdGenerator

case class PostTagRequest(
  name: String,
  color: String
)(implicit idGen: IdGenerator[String]) {
  def toEntity(uid: String): Tag =
    Tag.from(User.Id(uid), name, color)
}
