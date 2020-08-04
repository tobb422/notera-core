package http.presenter.note

import domain.core.entities.{Note, Stock}
import domain.support.entities.User
import shared.ddd.IdGenerator

case class PostNoteRequest(
  stockId: Stock.Id,
  content: String
)(implicit idGen: IdGenerator[String]) {
  def toEntity(uid: User.Id): Note =
    Note.from(uid, stockId, content)
}
