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
  def getStocks(uid: String): F[Either[APIError, StocksResponse]] =
    EitherT.right[APIError](StockRepository[F].list(User.Id(uid)))
      .map(stocks => StocksResponse(stocks.map(StockResponse.fromEntity))).value

  def getStock(id: String, uid: String): F[Either[APIError, StockResponse]] =
    EitherT(find(id, uid)).leftMap(e => NotFound(e.getMessage): APIError)
      .map(StockResponse.fromEntity).value

  def createStock(req: PostStockRequest, uid: String): F[Either[APIError, StockResponse]] = {
    val res = for {
      tags <- EitherT.right[APIError](TagRepository[F].list(User.Id(uid)))
      _ <- EitherT.fromEither[F](containsTag(req.toTagIdsEntity, tags.map(_.id)))
      entity <- EitherT.right[APIError](
        Monad.apply[F].pure(req.toStockEntity(uid)
          .mergeTags(tags.filter(t => req.toTagIdsEntity.contains(t.id)))
        ))
      stock <- EitherT(StockRepository[F].insert(entity))
        .leftMap(e => BadRequest(e.getMessage): APIError)
    } yield StockResponse.fromEntity(stock)

    res.value
  }

  def updateStock(req: PutStockRequest, id: String, uid: String): F[Either[APIError, StockResponse]] = {
    val res = for {
      tags <- EitherT.right[APIError](TagRepository[F].list(User.Id(uid)))
      _ <- EitherT.fromEither[F](containsTag(req.toTagIdsEntity, tags.map(_.id)))
      target <- EitherT(find(id, uid)).leftMap(e => NotFound(e.getMessage): APIError)
      entity <- EitherT.right[APIError](
        Monad.apply[F].pure(req.updateItem(target)
          .copy(tags = req.tagIds.getOrElse(target.tags).map(t => tags.find(_.id.value == t).get))))
      stock <- EitherT(StockRepository[F].update(entity))
        .leftMap(e => BadRequest(e.getMessage): APIError)
    } yield StockResponse.fromEntity(stock)

    res.value
  }

  def deleteStock(id: String): F[Either[APIError, Unit]] =
    EitherT(StockRepository[F].delete(Stock.Id(id)))
      .leftMap(e => BadRequest(e.getMessage): APIError).value

  private def find(id: String, uid: String) =
    StockRepository[F].resolve(Stock.Id(id), User.Id(uid))

  private def containsTag(targets: Seq[Tag.Id], tags: Seq[Tag.Id]): Either[APIError, Unit] =
    if (targets.isEmpty) {
      Right(())
    } else {
      Either.cond(targets.toSet subsetOf tags.toSet, (), BadRequest("invalid tag ids"))
    }
}
