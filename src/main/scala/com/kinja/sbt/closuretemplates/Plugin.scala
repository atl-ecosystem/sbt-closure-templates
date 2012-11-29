// vim: sw=2 ts=2 softtabstop=2 expandtab :
package com.kinja.sbt.closuretemplates

import sbt._
import sbt.Keys._

object SbtSoy extends Plugin {

  import SoyKeys._

  def soySettings: Seq[Setting[_]] = Seq(
    soyDirectory in Compile <<= (baseDirectory)(_ / "app" / "views"),
    soyDirectory in Test <<= (baseDirectory)(_ / "test" / "views"),

    soyLocaleDirectory in Compile <<= (baseDirectory)(_ / "app" / "locales"),
    soyLocaleDirectory in Test <<= (baseDirectory)(_ / "test" / "locales"),

    soyJsDirectory in Compile <<= resourceManaged in Compile,
    soyJsDirectory in Test <<= resourceManaged in Test,

    soyJsDirectorySuffix := "public/javascripts/templates/closure/{LOCALE}",

    unmanagedResourceDirectories in Compile <++=
      Seq(soyLocaleDirectory in Compile).join,
    unmanagedResourceDirectories in Test <++=
      Seq(soyLocaleDirectory in Test).join,

    soyExtension := "soy",
    soyLocales := Seq("en_US"),

    soyEntryPoints in Compile <<= (soyDirectory in Compile, soyExtension)(
      (base, ext) => base / "closure" ** ("*." + ext)),
    soyEntryPoints in Test <<= (soyDirectory in Test, soyExtension)(
      (base, ext) => base / "closure" ** ("*." + ext)),

    soyCompiler in Compile <<= (
      soyExtension,
      soyDirectory in Compile,
      soyJsDirectory in Compile,
      soyJsDirectorySuffix,
      cacheDirectory,
      soyEntryPoints in Compile,
      soyLocales,
      soyLocaleDirectory in Compile) map SoyTemplates.Compiler,

    soyCompiler in Test <<= (
      soyExtension,
      soyDirectory in Test,
      soyJsDirectory in Test,
      soyJsDirectorySuffix,
      cacheDirectory,
      soyEntryPoints in Test,
      soyLocales,
      soyLocaleDirectory in Test) map SoyTemplates.Compiler
  )

  object SoyKeys {
    val soyDirectory = SettingKey[File]("soy-directory",
      "Default directory containing .soy templates.")
    val soyLocaleDirectory = SettingKey[File]("soy-locale-directory",
      "Default directory containing .xlf message files.")
    val soyJsDirectory = SettingKey[File]("soy-js-directory",
      "Base directory for generated javascripts.")
    val soyJsDirectorySuffix = SettingKey[String]("soy-js-directory-suffix",
      "The final directory for generated javascripts: soyJsDirectory / soyJsDirectorySuffix")
    val soyEntryPoints = SettingKey[PathFinder]("soy-entry-points")
    val soyLocales = SettingKey[Seq[String]]("soy-locales")
    val soyOptions = SettingKey[Seq[String]]("soy-options")
    val soyCompiler = TaskKey[Seq[java.io.File]]("soy-compiler")
    val soyExtension = SettingKey[String]("soy-extension")
  }

}
