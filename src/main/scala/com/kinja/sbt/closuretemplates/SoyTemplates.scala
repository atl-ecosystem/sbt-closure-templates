// vim: sw=2 ts=2 softtabstop=2 expandtab :
package com.kinja.sbt.closuretemplates

import sbt._

import scala.collection.JavaConversions._
import com.google.template.soy.SoyFileSet
import com.google.template.soy.msgs.SoyMsgBundle
import com.google.template.soy.msgs.SoyMsgBundleHandler
import com.google.template.soy.xliffmsgplugin.XliffMsgPlugin
import com.google.template.soy.jssrc.SoyJsSrcOptions

/**
 * Wrapper around the Google Closure Template compiler
 */
object SoyTemplates {

  import SbtSoy.SoyKeys._

  def createMsgBundle(locale: String, localeDir: File): SoyMsgBundle = {
    val bundleHandler = new SoyMsgBundleHandler(new XliffMsgPlugin())
    bundleHandler.createFromFile(new java.io.File(localeDir, locale + ".xlf"))
  }

  /**
   * Compile list of files
   *
   * @param   source    Soy source files
   * @param   options   Options to the javascript compiler
   * @param   bundle    Message bundle to the current language
   *
   * @return  List of (File -> compiled source) pairs
   */
  def compile(sources: Seq[File],
    options: SoyJsSrcOptions,
    bundle: SoyMsgBundle): Seq[(File, String)] =
    {
      val builder: SoyFileSet.Builder = new SoyFileSet.Builder()
      sources.map(builder.add)
      sources zip builder.build().compileToJsSrc(options, bundle)
    }

  /**
   * Compiler interface
   *
   * @param ext              Extension of Closure templates
   * @param src              Template root directory
   * @param resources        Managed resource directory
   * @param jsTargetPattern  Target directory of generated JS file
   *                         (should be contains the {LOCALE} placeholder)
   * @param cache            Cache directory
   * @param files            All template files
   * @param locales          List of locales
   * @param localeDir        Directory of locale files
   *
   * @return The list of generated and copied files
   */
  val Compiler = (
    ext: String,
    src: File,
    resources: File,
    jsTargetPattern: String,
    cache: File,
    files: PathFinder,
    locales: Seq[String],
    localeDir: File
  ) => {
    import java.io._
    val cacheFile = cache / ext
    val currentInfos =
      (src ** ("*." + ext)).get.map(f => f -> FileInfo.lastModified(f)).toMap
    val (previousRelation, previousInfo) =
      Sync.readInfo(cacheFile)(FileInfo.lastModified.format)

    if (previousInfo != currentInfos) {

      // Delete previous generated files
      previousRelation._2s.foreach(IO.delete)

      val options = new SoyJsSrcOptions()

      val sourceFiles: Seq[sbt.File] =
        (files x relativeTo(Seq(src / "closure"))).map(_._1)

      val generated: Seq[(File, File)] = locales.flatMap { locale =>
        compile(sourceFiles, options, createMsgBundle(locale, localeDir)).map {
          case (sourceFile, compiledContent) => {

            // relative template path aka. name
            val name = sourceFile.getAbsolutePath.substring(
              src.getAbsolutePath.length
            ).substring(1)

            val out = new File(
              resources,
              jsTargetPattern.replace("{LOCALE}", locale) +
                name.replace("closure", "").replace("." + ext, ".js")
            )

            IO.write(out, compiledContent)
            val resFile = new File(resources, name)
            IO.copyFile(sourceFile, resFile)
            (resFile, out)
          }
        }
      }

      // this file will contains the available templates
      val listFile = new File(resources, "closure_templates.txt")
      // write the list of available tempates
      IO.write(listFile, generated.map(_._1).distinct.toList.map(
        _.getAbsolutePath.substring(resources.getAbsolutePath.length)).mkString(IO.Newline))

      Sync.writeInfo(cacheFile,
        Relation.empty[File, File] ++ generated ++ List((listFile, listFile)),
        currentInfos)(FileInfo.lastModified.format)

      // Return new files
      generated.map(_._1).distinct.toList ++
        generated.map(_._2).distinct.toList ++
        List(listFile)
    } else {
      // Return previously generated files
      previousRelation._1s.toSeq ++ previousRelation._2s.toSeq
    }
  }
}
