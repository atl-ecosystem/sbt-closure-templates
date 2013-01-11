// vim: sw=2 ts=2 softtabstop=2 expandtab :
import sbt._
import sbt.Keys._

import sbt.ScriptedPlugin._
import com.typesafe.sbt.SbtScalariform._

object Build extends Build {
  lazy val root = Project(
    id = "sbt-closure-templates",
    base = file("."),
    settings = Project.defaultSettings ++
    scriptedSettings ++
    scalariformSettings ++
    Seq(
      libraryDependencies += "com.google.template" % "soy" % "2012-12-21",
      resolvers += "Gawker Public Group" at "https://vip.gawker.com/nexus/content/groups/public/",
      credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
      organization := "com.kinja.sbt",
      version := "0.2-SNAPSHOT",
      scalacOptions ++= Seq("-unchecked", "-deprecation"),
      sbtPlugin := true
    )
  )
}
