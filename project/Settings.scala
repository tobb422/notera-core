import sbt.Keys._
import sbt._

object Settings {
  val coreSettings: Def.SettingsDefinition = Seq(
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-unchecked",
      "-encoding",
      "utf8",
      "-Xfatal-warnings",
      "-Xlint",
      "-language:implicitConversions",
      "-language:higherKinds",
      "-language:existentials"
    )
  )
}
