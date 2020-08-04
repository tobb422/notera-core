package http.presenter.note

import java.time.ZonedDateTime

import domain.core.entities.Note

case class NoteResponse(
  id: String,
  userId: String,
  stockId: String,
  content: String,
  createdAt: ZonedDateTime,
  updatedAt: ZonedDateTime
)

object NoteResponse {
  def fromEntity(memo: Note): NoteResponse = NoteResponse(
    memo.id.value,
    memo.userId.value,
    memo.stockId.value,
    memo.content,
    memo.createdAt,
    memo.updatedAt
  )
}
