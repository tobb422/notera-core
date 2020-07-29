package http.controllers.error

sealed trait APIError {
  val code: String
}

case class FieldError(name: String, code: String)

case class Unauthorized(code: String = "unauthorized", message: Option[String] = None) extends APIError
case class Forbidden(code: String = "forbidden", message: Option[String] = None)    extends APIError
case class BadRequest(code: String = "bad request", message: Option[String] = None) extends APIError
case class NotFound(code: String = "not found", message: Option[String] = None)     extends APIError
case class Conflict(code: String = "conflict", message: Option[String] = None)      extends APIError
case class UnprocessableEntity(code: String = "unprocessable_entity", fieldError: Seq[FieldError] = Seq.empty) extends APIError
case class InternalServerError(code: String = "internal_server_error", message: Option[String] = None) extends APIError
