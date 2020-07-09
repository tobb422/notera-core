package http.controllers

import cats.Monad
import cats.data.EitherT

import scala.util.Either

class StockService[F[_]: Monad] {
  def getStocks: F[Either[String, String]] = {
    val either: Either[String, String] = Right("getStocks")
    val result = for {
      response <- EitherT.fromEither[F](either)
    } yield response

    result.value
  }
}
