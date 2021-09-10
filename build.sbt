// Build file for Scala and Chisel projects

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

lazy val root = (project in file("."))
  .settings(
    name := "seccamp-nn",
    version := "0.1",
    scalaVersion := "2.12.8",
    maxErrors := 3,
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.2",
      "edu.berkeley.cs" %% "chisel3"         % "3.0.+",
      "edu.berkeley.cs" %% "chisel-iotesters" % "1.1.+",
      "io.circe" %% "circe-core" % "0.7.0",
      "io.circe" %% "circe-parser" % "0.7.0",
      "io.circe" %% "circe-generic" % "0.7.0",
    )
  )

// Refine scalac params from tpolecat
scalacOptions --= Seq(
  "-Xfatal-warnings"
)
scalacOptions ++= Seq(
  "-Xsource:2.11",
  "-language:reflectiveCalls",
)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)

// Aliases
addCommandAlias("com", "all compile test:compile")
addCommandAlias("rel", "reload")
addCommandAlias("fix", "all compile:scalafix test:scalafix")
addCommandAlias("fmt", "all scalafmtSbt scalafmtAll")
