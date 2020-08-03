package http.presenter.user

import java.time.ZonedDateTime

import domain.support.entities.User

case class UserResponse(
  id: String,
  uid: String,
  createdAt: ZonedDateTime,
  updatedAt: ZonedDateTime
)

object UserResponse {
  def fromEntity(user: User): UserResponse = {
    UserResponse(
      user.id.value,
      user.uid,
      user.createdAt,
      user.updatedAt
    )
  }
}
