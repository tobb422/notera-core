package gateway.slick

import cats.Monad
import domain.Repositories
import domain.core.repositories._
import domain.support.repositories.UserRepository
import gateway.slick.repositories._

import scala.concurrent.ExecutionContext

class SlickRepositories[F[_]: Monad: DatabaseProvider] extends Repositories[F] {
  private val provider = implicitly[DatabaseProvider[F]]
  implicit private val context: ExecutionContext = provider.context

  override implicit val stockRepository: StockRepository[F] =
    new StockRepositoryImpl[F](provider.profile, provider.transformation)
  override implicit val tagRepository: TagRepository[F] =
    new TagRepositoryImpl[F](provider.profile, provider.transformation)
  override implicit val userRepository: UserRepository[F] =
    new UserRepositoryImpl[F](provider.profile, provider.transformation)
}
