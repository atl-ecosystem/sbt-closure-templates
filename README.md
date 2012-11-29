# SBT plugin for Google Closure Templates

This plugin is designed for using Google Closure Templates with SBT 0.12+

## Install

1. Clone repo
2. Cd into sbt-closure-templates directory
3. `sbt publish-local`
4. Add this plugin to your application as a dependency: `"com.kinja.sbt" %% "sbt-closure-templates" % "0.1-SNAPSHOT"`

## Usage

Import the plugin

`mport com.kinja.sbt.closuretemplates.SbtSoy._`

Add the settings:

`resourceGenerators in Compile <+= SoyKeys.soyCompiler in Compile`

The default dictionary file pattern is `app/locale/{$locale}.xlf`

## How it works?

When plugin starts it recursively collect all files with .soy ending from your application's view directory  - the default is `app/view/closure` - copy as resource and compile them to javascript. Also, it writes the copied .soy files paths into the `/closure_templates.txt` resource. So, you can use this file as a list of available templates. Which is useful because we cannot find files in a jar package. For example our play2-closure Play! plugin using that file, too.
