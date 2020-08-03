package http

import cats.implicits._
import http.controllers.error.{APIError, Forbidden}
import shared.auth.TokenVerifier

package object auth {
  def authenticate(token: Option[String])(
    implicit tokenVerifier: TokenVerifier
  ): Either[APIError, String] = {
    val ttt = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjU1NGE3NTQ3Nzg1ODdjOTRjMTY3M2U4ZWEyNDQ2MTZjMGMwNDNjYmMiLCJ0eXAiOiJKV1QifQ.eyJuYW1lIjoi6aas5aC05pWP5rCXIiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9hLS9BT2gxNEdpVFpydmhxSUNHNWp6SmNQbFhaeUNQczJ2MnJudHp1TWVRRHNQWiIsImlzcyI6Imh0dHBzOi8vc2VjdXJldG9rZW4uZ29vZ2xlLmNvbS9ub3RlcmEtYXV0aCIsImF1ZCI6Im5vdGVyYS1hdXRoIiwiYXV0aF90aW1lIjoxNTk2NDM1MDY1LCJ1c2VyX2lkIjoiNFRjMVBlRFZrRVlmemFheVE5MWtnbUVuSW9GMyIsInN1YiI6IjRUYzFQZURWa0VZZnphYXlROTFrZ21FbklvRjMiLCJpYXQiOjE1OTY0MzUwNjYsImV4cCI6MTU5NjQzODY2NiwiZW1haWwiOiJ0b2JiNDIyQGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJmaXJlYmFzZSI6eyJpZGVudGl0aWVzIjp7Imdvb2dsZS5jb20iOlsiMTA1ODI0NDY2Mjg3ODYyMDU4OTU2Il0sImVtYWlsIjpbInRvYmI0MjJAZ21haWwuY29tIl19LCJzaWduX2luX3Byb3ZpZGVyIjoiZ29vZ2xlLmNvbSJ9fQ.gSSk_5Vi0qXJhMnWnDyqN2UJdts2Nodokqacc7xFop3-_b4J39GPBjHc2pGbtc7HXki1PzOFdd9xw5EXP0ImyW36K2qVDivfJROMVMatFhWY3vbaH0NirxV8GvLwBr-nl6Yzo95Cmc0OuSyvwnLc8WmF5LlE_wjg_DrD7gklyjlO32ZT-myYvHzAS_jdt6QpJI-WPE9xBfkqO7lq1Xuage6GokEgHFVpd5wcmR3zSvnIi3V5QvnEh0mkjH5FymdPptpN1OK9nLhQK7q_Ta-5GJRTskQaW1G221FdMik_1DD5EIAG-88TDlMdNP9OHVPKKtEu6Mez3f_WOE20ZneL8g"
    for {
      t <- token match {
        case Some(t) => Right(t)
        case None => Left(Forbidden(message = Some("Couldn't find an Authorization header")))
      }
      uid <- tokenVerifier.validate(ttt).leftMap(e => Forbidden(e.errorCode))
    } yield uid
  }
}
