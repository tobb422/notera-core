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
  private val repo = TagRepository[F]

  def getTags(uid: User.Id): F[Either[APIError, TagsResponse]] =
    EitherT.right[APIError](repo.list(uid))
      .map(tags => TagsResponse(tags.map(TagResponse.fromEntity))).value

  def getTag(id: Tag.Id, uid: User.Id): F[Either[APIError, TagResponse]] =
    EitherT(find(id, uid))
      .leftMap(e => NotFound(e.getMessage): APIError)
      .map(TagResponse.fromEntity).value

  def createTag(req: PostTagRequest, uid: User.Id): F[Either[APIError, TagResponse]] =
    EitherT(repo.save(req.toEntity(uid)))
      .leftMap(e => BadRequest(e.getMessage): APIError)
      .map(TagResponse.fromEntity).value

  def updateTag(req: PutTagRequest, id: Tag.Id, uid: User.Id): F[Either[APIError, TagResponse]] =
    (for {
      entity <- EitherT(find(id, uid)).leftMap(e => NotFound(e.getMessage): APIError)
      tag <- EitherT(repo.save(req.updateEntity(entity)))
        .leftMap(e => BadRequest(e.getMessage): APIError)
        .map(TagResponse.fromEntity)
    } yield tag).value

  def deleteTag(id: Tag.Id, uid: User.Id): F[Either[APIError, Unit]] =
    EitherT(repo.delete(id, uid))
      .leftMap(e => BadRequest(e.getMessage): APIError).value

  private def find(id: Tag.Id, uid: User.Id) = repo.resolve(id, uid)
}
