package http

import cats.effect.{ContextShift, IO, Timer}
import http.http4s.Http4sServer

import domain.core.repositories.StockRepository
import gateway.id.ULIDGenerator
import gateway.slick.repositories.StockRepositoryImpl
import shared.ddd.IdGenerator

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App {
  implicit val cs: ContextShift[IO] = IO.contextShift(global)
  implicit val timer: Timer[IO]     = IO.timer(global)

  implicit val idGen: IdGenerator[String] = new ULIDGenerator
  implicit val stockRepository: StockRepository[IO] = new StockRepositoryImpl[IO]

  val server: Server[IO] = new Http4sServer[IO]
  server.serve("0.0.0.0", 8080).unsafeRunSync()
}
