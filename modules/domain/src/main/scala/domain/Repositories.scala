package domain

import domain.core.repositories.StockRepository

trait Repositories[F[_]] {
  implicit val stockRepository: StockRepository[F]
}
