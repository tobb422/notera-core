package gateway.auth

import com.google.firebase.{FirebaseApp, FirebaseOptions}
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.tasks.Tasks
import shared.auth.{InvalidTokenError, TokenInfo, TokenVerifier, ValidationError}

import scala.util.{Failure, Success, Try}

class FirebaseAuthService extends TokenVerifier {
  private val options = new FirebaseOptions.Builder()
    .setServiceAccount(this.getClass().getClassLoader().getResourceAsStream("firebase.json"))
    .setDatabaseUrl("https://notera-auth.firebaseio.com/")
    .build()
  private val app = Try { FirebaseApp.initializeApp(options) } match {
    case Success(res) => res
    case Failure(_) => FirebaseApp.getInstance()
  }
  private val firebaseAuth = FirebaseAuth.getInstance(app)

  override def validate(token: String): Either[ValidationError, String] =
    Try { Tasks.await(firebaseAuth.verifyIdToken(token)).getUid } match {
      case Success(res) => Right(res)
      case Failure(_) => Left(InvalidTokenError)
    }

  override def info(token: String): Either[ValidationError, TokenInfo] =
    Try { Tasks.await(firebaseAuth.verifyIdToken(token)) } match {
      case Success(res) => Right(TokenInfo(res.getUid, res.getName, res.getEmail))
      case Failure(_) => Left(InvalidTokenError)
    }
}
