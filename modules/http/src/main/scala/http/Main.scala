package http

import cats.effect.{ContextShift, IO, Timer}
import domain.Repositories
import gateway.auth.FirebaseAuthService
import http.http4s.{DatabaseProviderIO, Http4sServer}
import gateway.id.ULIDGenerator
import gateway.slick.SlickRepositories
import shared.auth.TokenVerifier
import shared.ddd.IdGenerator

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App {
  implicit val cs: ContextShift[IO] = IO.contextShift(global)
  implicit val timer: Timer[IO]     = IO.timer(global)

  implicit val idGen: IdGenerator[String] = new ULIDGenerator
  implicit val tokenVerifier: TokenVerifier = new FirebaseAuthService
  implicit val provider: DatabaseProviderIO = new DatabaseProviderIO
  implicit val repositories: Repositories[IO] = new SlickRepositories[IO]

  val server: Server[IO] = new Http4sServer[IO]
  server.serve("0.0.0.0", 8080).unsafeRunSync()
}
