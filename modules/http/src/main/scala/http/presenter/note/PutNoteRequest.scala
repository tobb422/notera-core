package http.presenter.note

import java.time.ZonedDateTime

import domain.core.entities.{Note, Stock}

case class PutNoteRequest(
  stockId: Stock.Id,
  content: String
) {
  def update(memo: Note): Note =
    memo.copy(
      id = memo.id,
      stockId = memo.stockId,
      userId = memo.userId,
      content = content,
      createdAt = memo.createdAt,
      updatedAt = ZonedDateTime.now()
    )
}
