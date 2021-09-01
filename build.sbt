// Build file for Scala and Chisel projects

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

lazy val root = (project in file("."))
  .settings(
    organization := "FreeChips",
    name := "chisel-project",
    version := "0.0.1",
    scalaVersion := "2.12.10",
    maxErrors := 3,
    libraryDependencies ++= Seq(
      "edu.berkeley.cs" %% "chisel3"         % "3.2-SNAPSHOT",
      "edu.berkeley.cs" %% "chisel-testers2" % "0.1-SNAPSHOT"
    )
  )

// Refine scalac params from tpolecat
scalacOptions --= Seq(
  "-Xfatal-warnings"
)
scalacOptions ++= Seq(
  "-Xsource:2.11"
)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)

// Aliases
addCommandAlias("com", "all compile test:compile")
addCommandAlias("rel", "reload")
addCommandAlias("fix", "all compile:scalafix test:scalafix")
addCommandAlias("fmt", "all scalafmtSbt scalafmtAll")
