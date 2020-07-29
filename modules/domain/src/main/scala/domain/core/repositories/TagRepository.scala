package domain.core.repositories

import domain.core.entities.Tag
import domain.support.entities.User
import shared.ddd._

trait TagRepository[F[_]] {
  type FailedToResolveByTagId = FailedToResolveById[Tag]
  type FailedToSaveTag = FailedToSaveEntity[Tag]
  type FailedToDeleteTag = FailedToDeleteEntity[Tag]

  def resolve(id: Tag.Id, uid: User.Id): F[Either[FailedToResolveByTagId, Tag]]
  def list(uid: User.Id): F[Seq[Tag]]
  def save(value: Tag): F[Either[FailedToSaveTag, Tag]]
  def delete(id: Tag.Id): F[Either[FailedToDeleteTag, Unit]]
}

object TagRepository {
  def apply[F[_]: TagRepository]: TagRepository[F] = implicitly[TagRepository[F]]
}
