package http.controllers

import cats.Monad
import cats.data.EitherT
import domain.core.entities.{Memo, Stock}
import domain.core.repositories.MemoRepository
import domain.support.entities.User
import http.controllers.error.{APIError, BadRequest, NotFound}
import http.presenter.memo.{MemoResponse, MemosResponse, PostMemoRequest, PutMemoRequest}
import shared.ddd.IdGenerator

class MemoService[F[_]: Monad: MemoRepository](
  implicit val idGen: IdGenerator[String]
) {
  def getMemo(id: Memo.Id, uid: User.Id): F[Either[APIError, MemoResponse]] =
    EitherT(MemoRepository[F].resolve(id, uid))
      .leftMap(e => NotFound(e.getMessage): APIError)
      .map(MemoResponse.fromEntity).value

  def getMemos(sid: Stock.Id, uid: User.Id): F[Either[APIError, MemosResponse]] =
    EitherT.right[APIError](MemoRepository[F].resolveByStockId(sid, uid))
    .map(memos => MemosResponse(memos.map(MemoResponse.fromEntity))).value

  def createMemo(req: PostMemoRequest, uid: User.Id): F[Either[APIError, MemoResponse]] =
    EitherT(MemoRepository[F].save(req.toEntity(uid)))
      .leftMap(e => BadRequest(e.getMessage): APIError)
      .map(MemoResponse.fromEntity).value

  def updateMemo(req: PutMemoRequest, id: Memo.Id, uid: User.Id): F[Either[APIError, MemoResponse]] =
    (for {
      entity <- EitherT(MemoRepository[F].resolve(id, uid)).leftMap(e => NotFound(e.getMessage): APIError)
      memo <- EitherT(MemoRepository[F].save(req.update(entity)))
        .leftMap(e => BadRequest(e.getMessage): APIError)
        .map(MemoResponse.fromEntity)
    } yield memo).value

  def deleteMemo(id: Memo.Id): F[Either[APIError, Unit]] =
    EitherT(MemoRepository[F].delete(id))
      .leftMap(e => BadRequest(e.getMessage): APIError).value
}
