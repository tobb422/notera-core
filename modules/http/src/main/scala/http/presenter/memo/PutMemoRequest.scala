package http.presenter.memo

import java.time.ZonedDateTime

import domain.core.entities.{Memo, Stock}

case class PutMemoRequest(
  stockId: Stock.Id,
  content: String
) {
  def update(memo: Memo): Memo =
    memo.copy(
      id = memo.id,
      stockId = memo.stockId,
      userId = memo.userId,
      content = content,
      createdAt = memo.createdAt,
      updatedAt = ZonedDateTime.now()
    )
}
