ThisBuild / version := "0.2.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.2"

lazy val root = (project in file("."))
  .settings(
    name := "BankApp",
    idePackagePrefix := Some("org.scala.bank")
  )
libraryDependencies += "org.postgresql" % "postgresql" % "42.6.0"
libraryDependencies +="javax.servlet" % "javax.servlet-api" % "4.0.1"
libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % "0.14.5",
  "io.circe" %% "circe-generic" % "0.14.5",
  "org.typelevel" %% "cats-core" % "2.9.0"
)

