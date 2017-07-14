name := "akkaScalaExamples"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-cluster_2.11" % "2.5.2",
  "com.typesafe.akka" % "akka-actor_2.11" % "2.5.2",
  "com.typesafe.akka" % "akka-persistence_2.11" % "2.5.1",
  "com.typesafe.akka" % "akka-cluster-tools_2.11" % "2.5.2"
)

        