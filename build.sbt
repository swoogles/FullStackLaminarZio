import org.scalajs.linker.interface.ModuleSplitStyle

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.1"
ThisBuild / resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

lazy val `test-vite` = project.in(file("."))
  .enablePlugins(ScalaJSPlugin) // Enable the Scala.js plugin in this project
  .enablePlugins(ScalablyTypedConverterExternalNpmPlugin)
  .dependsOn(shared.js)
  .settings(
    scalacOptions ++= Seq("-encoding", "utf-8", "-deprecation", "-feature"),

    // Tell Scala.js that this is an application with a main method
    scalaJSUseMainModuleInitializer := true,

    /* Configure Scala.js to emit modules in the optimal way to
     * connect to Vite's incremental reload.
     * - emit ECMAScript modules
     * - emit as many small modules as possible for classes in the "testvite" package
     * - emit as few (large) modules as possible for all other classes
     *   (in particular, for the standard library)
     */
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(ModuleSplitStyle.SmallModulesFor(List("testvite")))
    },

    // Depend on Laminar
    libraryDependencies ++= Seq(
      "com.raquo" %%% "laminar" % "15.0.1",
      "com.softwaremill.sttp.client3" %%% "core" % "3.9.3",
      "dev.zio" %%% "zio" % "2.0.21",
      "dev.zio" %%% "zio-json" % "0.6.2",
      "dev.zio" %%% "zio-schema" % "1.0.1",
      "dev.zio" %%% "zio-schema-json" % "1.0.1",
      "dev.zio" %%% "zio-schema-protobuf" % "1.0.1",
    ),

    // Tell ScalablyTyped that we manage `npm install` ourselves
    externalNpm := baseDirectory.value,
  )

lazy val backend =
  project.in(file("backend"))
    .dependsOn(shared.jvm)
    .settings(
      name := "backend",
      libraryDependencies ++=
        Seq(
          "dev.zio" %% "zio" % "2.0.21",
        )

    )

lazy val shared = crossProject(JSPlatform, JVMPlatform).in(file("shared"))
  .settings(
    name := "shared",
    libraryDependencies ++=
      Seq(
        "dev.zio" %%% "zio-http" % "3.0.0-RC4+78-28c9de1d+20240227-1615-SNAPSHOT", // Local
        "dev.zio" %%% "zio-schema" % "1.0.1",
        "dev.zio" %%% "zio-schema-protobuf" % "1.0.1",
        "dev.zio" %%% "zio-json" % "0.6.2",
      )
  )
