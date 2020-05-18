import sbt._

object Http4s {
  private val ver = "0.21.3"
  val dsl = "org.http4s" %% "http4s-dsl" % ver
  val blazeServer = "org.http4s" %% "http4s-blaze-server" % ver
}

object Logback {
  val version = "1.2.3"
  val classic = "ch.qos.logback" % "logback-classic" % version
}
