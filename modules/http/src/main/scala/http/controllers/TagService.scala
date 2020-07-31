package http.controllers

import cats.Monad
import cats.data.EitherT
import domain.core.entities.Tag
import domain.core.repositories.TagRepository
import domain.support.entities.User
import http.controllers.error.{APIError, BadRequest, NotFound}
import http.presenter.tag.{PostTagRequest, PutTagRequest, TagResponse, TagsResponse}
import shared.ddd.IdGenerator

class TagService[F[_]: Monad: TagRepository](
  implicit val idGen: IdGenerator[String]
) {
  def getTags(uid: String): F[Either[APIError, TagsResponse]] =
    EitherT.right[APIError](TagRepository[F].list(User.Id(uid)))
      .map(tags => TagsResponse(tags.map(TagResponse.fromEntity))).value

  def getTag(id: String, uid: String): F[Either[APIError, TagResponse]] =
    EitherT(find(id, uid))
      .leftMap(e => NotFound(e.getMessage): APIError)
      .map(TagResponse.fromEntity).value

  def createTag(req: PostTagRequest, uid: String): F[Either[APIError, TagResponse]] =
    EitherT(TagRepository[F].save(req.toEntity(uid)))
      .leftMap(e => BadRequest(e.getMessage): APIError).map(TagResponse.fromEntity).value

  def updateTag(req: PutTagRequest, id: String, uid: String): F[Either[APIError, TagResponse]] =
    (for {
      entity <- EitherT(find(id, uid)).leftMap(e => NotFound(e.getMessage): APIError)
      tag <- EitherT(TagRepository[F].save(req.updateEntity(entity))).leftMap(e => BadRequest(e.getMessage): APIError).map(TagResponse.fromEntity)
    } yield tag).value

  def deleteTag(id: String): F[Either[APIError, Unit]] =
    EitherT(TagRepository[F].delete(Tag.Id(id)))
      .leftMap(e => BadRequest(e.getMessage): APIError).value

  private def find(id: String, uid: String) =
    TagRepository[F].resolve(Tag.Id(id), User.Id(uid))
}
