scalaVersion := "2.11.4"

val scalazVersion = "7.1.0"

resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % scalazVersion,
  "org.scalaz" %% "scalaz-effect" % scalazVersion,
  "org.scalaz" %% "scalaz-typelevel" % scalazVersion,
  "org.scalaz" %% "scalaz-scalacheck-binding" % scalazVersion % "test",
  "org.scalaz.stream" %% "scalaz-stream" % "0.6a",
  "com.github.nscala-time" %% "nscala-time" % "1.6.0",
// "joda-time" % "joda-time" % "2.3",
  "com.github.scala-incubator.io" %% "scala-io-core" % "0.4.3",
  "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.3",
  "com.typesafe" % "config" % "1.0.0",
  "io.netty" % "netty" % "3.9.4.Final"
)

scalacOptions += "-feature"

initialCommands in console := "import scalaz._, Scalaz._, scalaz.concurrent._, scalaz.stream._"
