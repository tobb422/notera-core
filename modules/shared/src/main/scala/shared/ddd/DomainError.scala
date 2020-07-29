package shared.ddd

import scala.reflect._

trait DomainValidationError[T]
trait DomainManipulationError[T]

trait RepositoryError[T <: Entity] extends DomainManipulationError[T] {
  val code: String
  val message: Option[String]

  def getMessage: String = message.getOrElse(code)
}

case class FailedToResolveById[T <: Entity: ClassTag](
  id: T#Id,
  code: String = "failed_to_resolve_id"
) extends RepositoryError[T] {
  val message: Option[String] =
    Some(s"failed to resolve entity ${classTag[T].runtimeClass} with id: ${id.value}")
}

case class FailedToSaveEntity[T <: Entity: ClassTag](
  code: String = "failed_to_save_entity",
  message: Option[String]
) extends RepositoryError[T]

case class FailedToDeleteEntity[T <: Entity: ClassTag](
  code: String = "failed_to_delete_entity",
  message: Option[String]
) extends RepositoryError[T]
