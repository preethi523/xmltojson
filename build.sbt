ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "xmltojson"
  )
libraryDependencies +="com.typesafe.play" %% "play-json" % "2.9.2"

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.0.1"
//
//libraryDependencies += "com.lucidchart" %% "xtract" % "2.2.1"