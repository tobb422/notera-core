package domain.support.repositories

import domain.support.entities.User
import shared.ddd._

trait UserRepository[F[_]] {
  type FailedToResolveByUserId = FailedToResolveById[User]
  type FailedToSaveUser = FailedToSaveEntity[User]

  def resolve(id: User.Id): F[Either[FailedToResolveByUserId, User]]
  def insert(value: User): F[Either[FailedToSaveUser, User]]
}

object UserRepository {
  def apply[F[_]: UserRepository]: UserRepository[F] = implicitly[UserRepository[F]]
}
