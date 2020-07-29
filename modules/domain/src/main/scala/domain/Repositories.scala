package domain

import domain.core.repositories._

trait Repositories[F[_]] {
  implicit val stockRepository: StockRepository[F]
  implicit val tagRepository: TagRepository[F]
}
