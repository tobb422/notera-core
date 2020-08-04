package http.presenter.memo

import java.time.ZonedDateTime

import domain.core.entities.Memo

case class MemoResponse(
  id: String,
  userId: String,
  stockId: String,
  content: String,
  createdAt: ZonedDateTime,
  updatedAt: ZonedDateTime
)

object MemoResponse {
  def fromEntity(memo: Memo): MemoResponse = MemoResponse(
    memo.id.value,
    memo.userId.value,
    memo.stockId.value,
    memo.content,
    memo.createdAt,
    memo.updatedAt
  )
}
