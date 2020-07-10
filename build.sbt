import Settings._

ThisBuild / scalaVersion := "2.13.1"

val baseName = "notera-core"

lazy val shared = (project in file("modules/shared"))
  .settings(
    name := s"$baseName-shared"
  )
  .settings(coreSettings)

lazy val domain = (project in file("modules/domain"))
  .settings(
    name := s"$baseName-domain"
  )
  .settings(coreSettings)
  .dependsOn(shared)

lazy val gateway = (project in file("modules/gateway"))
  .settings(
    name := s"$baseName-gateway",
    libraryDependencies ++= Seq(
      ULID.ulid4s,
      Cats.core,
      Cats.effect
    )
  )
  .settings(coreSettings)
  .dependsOn(domain)

lazy val http = (project in file("modules/http"))
  .settings(
    name := s"$baseName-http",
    libraryDependencies ++= Seq(
      Http4s.dsl,
      Http4s.blazeServer,
      Logback.classic,
      Circe.core,
      Circe.parser,
      Circe.generic
    )
  )
  .settings(coreSettings)
  .dependsOn(gateway)

lazy val root = (project in file("."))
  .settings(name := baseName)
  .settings(coreSettings)
  .aggregate(domain, shared, gateway, http)
