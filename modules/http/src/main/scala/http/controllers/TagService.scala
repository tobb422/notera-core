package http.controllers

import cats.Monad
import cats.data.EitherT
import domain.core.entities.Tag
import domain.core.repositories.TagRepository
import domain.support.entities.User
import http.controllers.error.{APIError, BadRequest, NotFound}
import http.presenter.stock.{PostTagRequest, TagResponse, TagsResponse}
import shared.ddd.IdGenerator

class TagService[F[_]: Monad: TagRepository](
  implicit val idGen: IdGenerator[String]
) {
  def getTags(uid: String): F[Either[APIError, TagsResponse]] = {
    val res = for {
      tags <- EitherT.right[APIError](TagRepository[F].list(User.Id(uid)))
    } yield TagsResponse(tags.map(TagResponse.fromEntity))

    res.value
  }

  def getTag(id: String, uid: String): F[Either[APIError, TagResponse]] = {
    val res = for {
      tag <- EitherT(
        TagRepository[F].resolve(Tag.Id(id), User.Id(uid))
      ).leftMap(e => NotFound(e.getMessage): APIError)
    } yield TagResponse.fromEntity(tag)

    res.value
  }

  def postTag(req: PostTagRequest, uid: String): F[Either[APIError, TagResponse]] = {
    val res = for {
      tag <- EitherT(
        TagRepository[F].save(req.toEntity(uid))
      ).leftMap(e => BadRequest(e.getMessage): APIError)
    } yield TagResponse.fromEntity(tag)

    res.value
  }

  def deleteTag(id: String): F[Either[APIError, Unit]] = {
    val res = for {
      _ <- EitherT(
        TagRepository[F].delete(Tag.Id(id))
      ).leftMap(e => BadRequest(e.getMessage): APIError)
    } yield ()

    res.value
  }
}