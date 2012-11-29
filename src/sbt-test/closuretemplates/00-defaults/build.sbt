// vim: sw=2 ts=2 softtabstop=2 expandtab ft=scala :

import com.kinja.sbt.closuretemplates.SbtSoy._

organization := "com.kinja.sbt"

name := "sbt-closure-templates-test"

version := "0.3"

soySettings

resourceGenerators in Compile <+= SoyKeys.soyCompiler in Compile

resourceGenerators in Test <+= SoyKeys.soyCompiler in Test

TaskKey[Unit]("check") <<= (
	resourceManaged in Compile, 
	SoyKeys.soyJsDirectorySuffix
) map { (resources, dirSuffix) =>
  val templates: String = IO.read(resources / "closure_templates.txt")
  if (templates != "/closure/main.soy") {
	  error(templates + " != /closure/main.soy")
  }
  val mainJs = dirSuffix.replace("{LOCALE}", "en_US") + "/main.js"
  if (!FileInfo.exists(new java.io.File( resources, mainJs)).exists) {
	error("Compile failed: " + resources + "/" + mainJs + " doesn't exists")
  }
  ()
}
