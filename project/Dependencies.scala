import sbt._

object Http4s {
  private val ver = "0.21.3"
  val dsl = "org.http4s" %% "http4s-dsl" % ver
  val blazeServer = "org.http4s" %% "http4s-blaze-server" % ver
}

object Logback {
  private val ver = "1.2.3"
  val classic = "ch.qos.logback" % "logback-classic" % ver
}

object ULID {
  val ulid4s = "net.petitviolet" %% "ulid4s" % "0.4.0"
}

object TestLib {
  val scalaTest = "org.scalatest" %% "scalatest" % "3.2.0"
}

object Circe {
  private val ver = "0.12.3"
  val core = "io.circe" %% "circe-core" % ver
  val parser = "io.circe" %% "circe-parser" % ver
  val generic = "io.circe" %% "circe-generic" % ver
//  val genericExtra = "io.circe" %% "circe-generic-extras" % ver
}

object Cats {
  val core = "org.typelevel" %% "cats-core" % "2.0.0"
  val effect = "org.typelevel" %% "cats-effect" % "2.1.3"
}
