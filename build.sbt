import Settings._

ThisBuild / scalaVersion := "2.13.1"

val baseName = "notera-core"

lazy val domain = (project in file("modules/domain"))
  .settings(
    name := s"$baseName-domain"
  )
  .settings(coreSettings)
  .dependsOn(shared)

lazy val shared = (project in file("modules/shared"))
  .settings(
    name := s"$baseName-shared"
  )
  .settings(coreSettings)

lazy val root = (project in file("."))
  .settings(name := baseName)
  .settings(coreSettings)
  .aggregate(domain, shared)
