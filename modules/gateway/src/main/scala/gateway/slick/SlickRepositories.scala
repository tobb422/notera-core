package gateway.slick

import cats.Monad
import domain.Repositories
import domain.core.repositories.StockRepository
import gateway.slick.repositories.StockRepositoryImpl

import scala.concurrent.ExecutionContext

class SlickRepositories[F[_]: Monad: DatabaseProvider] extends Repositories[F] {
  private val provider = implicitly[DatabaseProvider[F]]
  implicit private val context: ExecutionContext = provider.context

  override implicit val stockRepository: StockRepository[F] =
    new StockRepositoryImpl[F](provider.profile, provider.transformation)
}
