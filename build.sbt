lazy val userStudies = project.in(file("."))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    scalaVersion := "2.13.6",
    scalaJSUseMainModuleInitializer := true,
    resolvers += "jitpack" at "https://jitpack.io",
    resolvers += ("STG old bintray repo" at "http://www.st.informatik.tu-darmstadt.de/maven/").withAllowInsecureProtocol(true),
    libraryDependencies ++= Seq(
      "com.github.rescala-lang.rescala" %%% "rescala"     % "d88a5a61d95d7",
      "com.github.rescala-lang.rescala" %%% "replication"     % "d88a5a61d95d7",
      "org.scala-js"                    %%% "scalajs-dom" % "1.1.0",
      "com.lihaoyi"                     %%% "scalatags"   % "0.9.4",
      "com.github.scala-loci.scala-loci" %%% "scala-loci-communicator-webrtc" % "33e7a65a3ca29551e440abfdb6903a062c7dab70",
      "com.github.scala-loci.scala-loci" %%% "scala-loci-serializer-jsoniter-scala" % "33e7a65a3ca29551e440abfdb6903a062c7dab70",
      "com.github.plokhotnyuk.jsoniter-scala" %%% "jsoniter-scala-core" % "2.9.1" exclude ("io.github.cquiroz", s"scala-java-time-tzdb_sjs1_${scalaVersion.value.substring(0, 4)}"),
      "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % "2.9.1",
    ),
  )
