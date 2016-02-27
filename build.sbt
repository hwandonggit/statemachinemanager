name := "workflow"

version := "0.1"

scalaVersion := "2.11.7"

lazy val akkaVersion = "2.4.0"

fork in Test := true

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  "org.iq80.leveldb" % "leveldb" % "0.7",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "io.spray" %%  "spray-json" % "1.3.2",
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "com.typesafe.akka" % "akka-http-experimental_2.11" % "2.0.3",
  "com.typesafe.akka" % "akka-http-spray-json-experimental_2.11" % "2.0.3",
  "mysql" % "mysql-connector-java" % "5.1.37",
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "com.typesafe.akka" % "akka-http-testkit-experimental_2.11" % "2.0.3" % "test",
  "commons-io" % "commons-io" % "2.4" % "test"
  )
