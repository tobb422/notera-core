package http.controllers

import cats.Monad
import cats.data.EitherT
import domain.support.entities.User
import domain.support.repositories.UserRepository
import http.controllers.error.{APIError, BadRequest, NotFound}
import http.presenter.user.{PostUserRequest, UserResponse}
import shared.ddd.IdGenerator

class UserService[F[_]: Monad: UserRepository](
  implicit val idGen: IdGenerator[String]
) {

  def getUser(id: User.Id): F[Either[APIError, UserResponse]] =
    EitherT(UserRepository[F].resolve(id))
      .leftMap(e => NotFound(e.getMessage): APIError)
      .map(UserResponse.fromEntity).value

  def createUser(req: PostUserRequest): F[Either[APIError, UserResponse]] =
    EitherT(UserRepository[F].insert(req.toEntity))
      .leftMap(e => BadRequest(e.getMessage): APIError).map(UserResponse.fromEntity).value
}
