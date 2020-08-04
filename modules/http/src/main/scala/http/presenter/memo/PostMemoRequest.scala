package http.presenter.memo

import domain.core.entities.{Memo, Stock}
import domain.support.entities.User
import shared.ddd.IdGenerator

case class PostMemoRequest (
  stockId: Stock.Id,
  content: String
)(implicit idGen: IdGenerator[String]) {
  def toEntity(uid: User.Id): Memo =
    Memo.from(uid, stockId, content)
}
