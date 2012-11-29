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
		  libraryDependencies += "com.google.template" % "soy" % "2011-12-22",
      organization := "com.kinja.sbt",
      version := "0.1-SNAPSHOT",
      scalacOptions ++= Seq("-unchecked", "-deprecation"),
      sbtPlugin := true
    )
  )
}
