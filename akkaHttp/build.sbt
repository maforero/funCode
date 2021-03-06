name := "akkaHttp"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-core" % "10.0.4",
  "com.typesafe.akka" %% "akka-http" % "10.0.4",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.4",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.4",
  "com.typesafe.akka" %% "akka-http-jackson" % "10.0.4",
  "com.typesafe.akka" %% "akka-http-xml" % "10.0.4",
  "org.reactivemongo" % "reactivemongo_2.11" % "0.12.1",
  "org.slf4j" % "slf4j-simple" % "1.7.21",
  "com.typesafe.akka" % "akka-slf4j_2.11" % "2.4.17",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

libraryDependencies += "org.scalaj" % "scalaj-http_2.11" % "2.3.0"