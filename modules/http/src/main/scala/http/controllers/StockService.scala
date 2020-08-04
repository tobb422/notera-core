package http.controllers

import scala.util.Either
import cats.Monad
import cats.data.EitherT
import domain.core.entities.Tag
import domain.core.entities.Stock
import domain.core.repositories.{StockRepository, TagRepository}
import domain.support.entities.User
import http.controllers.error.{APIError, BadRequest, NotFound}
import http.presenter.stock.{PostStockRequest, PutStockRequest, StockResponse, StocksResponse}
import shared.ddd.IdGenerator

class StockService[F[_]: Monad: StockRepository: TagRepository](
  implicit val idGen: IdGenerator[String]
) {
  def getStocks(uid: User.Id): F[Either[APIError, StocksResponse]] =
    EitherT.right[APIError](StockRepository[F].list(uid))
      .map(stocks => StocksResponse(stocks.map(StockResponse.fromEntity))).value

  def getStock(id: Stock.Id, uid: User.Id): F[Either[APIError, StockResponse]] =
    EitherT(find(id, uid))
      .leftMap(e => NotFound(e.getMessage): APIError)
      .map(StockResponse.fromEntity).value

  def createStock(req: PostStockRequest, uid: User.Id): F[Either[APIError, StockResponse]] =
    (for {
      tags <- EitherT.right[APIError](TagRepository[F].list(uid))
      _ <- EitherT.fromEither[F](containsTag(req.toTagIdsEntity, tags.map(_.id)))
      entity <- EitherT.right[APIError](
        Monad[F].pure(req.toStockEntity(uid)
          .mergeTags(tags.filter(t => req.toTagIdsEntity.contains(t.id)))
        ))
      stock <- EitherT(StockRepository[F].insert(entity))
        .leftMap(e => BadRequest(e.getMessage): APIError)
        .map(StockResponse.fromEntity)
    } yield stock).value

  def updateStock(req: PutStockRequest, id: Stock.Id, uid: User.Id): F[Either[APIError, StockResponse]] =
    (for {
      tags <- EitherT.right[APIError](TagRepository[F].list(uid))
      _ <- EitherT.fromEither[F](containsTag(req.toTagIdsEntity, tags.map(_.id)))
      target <- EitherT(find(id, uid)).leftMap(e => NotFound(e.getMessage): APIError)
      entity <- EitherT.right[APIError](
        Monad[F].pure(req.updateItem(target)
          .copy(tags = req.tagIds.getOrElse(target.tags).map(t => tags.find(_.id.value == t).get))))
      stock <- EitherT(StockRepository[F].update(entity))
        .leftMap(e => BadRequest(e.getMessage): APIError)
          .map(StockResponse.fromEntity)
    } yield stock).value

  def deleteStock(id: Stock.Id, uid: User.Id): F[Either[APIError, Unit]] =
    EitherT(StockRepository[F].delete(id, uid))
      .leftMap(e => BadRequest(e.getMessage): APIError).value

  private def find(id: Stock.Id, uid: User.Id) =
    StockRepository[F].resolve(id, uid)

  private def containsTag(targets: Seq[Tag.Id], tags: Seq[Tag.Id]): Either[APIError, Unit] =
    if (targets.isEmpty) {
      Right(())
    } else {
      Either.cond(targets.toSet subsetOf tags.toSet, (), BadRequest("invalid tag ids"))
    }
}
