package domain

import domain.core.repositories._
import domain.support.repositories.UserRepository

trait Repositories[F[_]] {
  implicit val stockRepository: StockRepository[F]
  implicit val tagRepository: TagRepository[F]
  implicit val userRepository: UserRepository[F]
}
