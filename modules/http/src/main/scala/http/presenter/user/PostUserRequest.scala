package http.presenter.user

import domain.support.entities.User
import shared.ddd.IdGenerator

case class PostUserRequest(
  uid: String
)(implicit idGen: IdGenerator[String]) {
  def toEntity: User = User.from(uid)
}
