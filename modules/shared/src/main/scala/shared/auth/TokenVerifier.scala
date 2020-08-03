package shared.auth

sealed abstract class ValidationError(val errorCode: String)

case object InvalidTokenError extends ValidationError("invalid_token")
case class TokenInfo(uid: String, name: String, email: String)

trait TokenVerifier {
  def validate(token: String): Either[ValidationError, String]
  def info(token: String): Either[ValidationError, TokenInfo]
}
