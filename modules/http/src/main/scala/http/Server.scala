package http

trait Server[F[_]] {
  def serve(host: String, port: Int): F[_]
}
