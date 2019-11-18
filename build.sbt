val VERSION = "0.1.10"

usePgpKeyHex("755d525885532e9e")

def removeDependencies(groups: String*)(xml: scala.xml.Node) = {
  import scala.xml._
  import scala.xml.transform._
  (new RuleTransformer(
    new RewriteRule {
      override def transform(n: Node): Seq[Node] = n match {
        case dependency @ Elem(_, "dependency", _, _, _*) =>
          if(dependency.child.collect { case e: Elem => e}.headOption.exists { e =>
            groups.exists(group => e.toString == s"<groupId>$group</groupId>")
          }) Nil else dependency
        case x => x
      }
    }
  ))(xml)
}

lazy val commonSettings = Seq(
  version := VERSION,
  scalaVersion := "2.12.10",
  crossVersion := CrossVersion.full,
  crossScalaVersions := Seq("2.11.12", "2.12.8", "2.12.9", scalaVersion.value)
)

lazy val socco =
  (project in file(".")).
  settings(
    commonSettings,
    organization := "com.criteo.socco",
    name := "socco-plugin",
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding", "UTF-8",
      "-feature",
      "-unchecked",
      "-Xlint",
      "-Yno-adapted-args",
      "-Ywarn-dead-code",
      "-Xfuture",
      "-Ywarn-unused-import"
    ),
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-compiler" % scalaVersion.value,
      "org.planet42" %% "laika-core" % "0.7.0"
    ),
    credentials += Credentials(
      "Sonatype Nexus Repository Manager",
      "oss.sonatype.org",
      "criteo-oss",
      sys.env.getOrElse("SONATYPE_PASSWORD", "")
    ),
    sonatypeProfileName := "com.criteo",
    pgpPassphrase := sys.env.get("SONATYPE_PASSWORD").map(_.toArray),
    pgpSecretRing := file(".travis/secring.gpg"),
    pgpPublicRing := file(".travis/pubring.gpg"),
    pomExtra in Global := {
      <url>https://github.com/criteo/socco</url>
      <licenses>
        <license>
          <name>Apache 2</name>
          <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
        </license>
      </licenses>
      <scm>
        <connection>scm:git:github.com/criteo/socco.git</connection>
        <developerConnection>scm:git:git@github.com:criteo/socco.git</developerConnection>
        <url>github.com/criteo/socco</url>
      </scm>
      <developers>
        <developer>
          <name>Guillaume Bort</name>
          <email>g.bort@criteo.com</email>
          <url>https://github.com/guillaumebort</url>
          <organization>Criteo</organization>
          <organizationUrl>http://www.criteo.com</organizationUrl>
        </developer>
      </developers>
    },
    pomPostProcess := removeDependencies("org.planet42", "org.scala-lang"),
    // Vendorise internal libs
    publishArtifact in (Compile, packageBin) := false,
    artifact in (Compile, assembly) := {
      val core = (artifact in (Compile, packageBin)).value
      val vendorised = (artifact in (Compile, assembly)).value
      vendorised
    },
    assemblyExcludedJars in assembly := {
      (fullClasspath in assembly).value.filter {
        case jar if jar.data.getName.startsWith("scala-reflect-") => true
        case jar if jar.data.getName.startsWith("scala-library-") => true
        case jar if jar.data.getName.startsWith("scala-compiler-") => true
        case _ => false
      }
    },

    // Used to generate examples
    commands += Command.command("generateExamples") { (state) =>
      def enablePlugin(userStyle: Option[String] = None, linkScala: Boolean = false) = {
        val X = Project.extract(state)
        val version = X.get(scalaVersion)
        s"""
          set scalacOptions in examples := Seq(
            "-Xplugin:target/scala-${version.split("[.]").take(2).mkString(".")}/socco-plugin-assembly-$VERSION.jar",
            ${userStyle.map(style => "\"-P:socco:style:examples/src/main/styles/" + style + ".css\",").getOrElse("")}
            ${if(linkScala) "\"-P:socco:package_scala:http://www.scala-lang.org/api/current/\"," else ""}
            "-P:socco:out:examples/target/html${userStyle.map(style => s"/$style").getOrElse("")}",
            "-P:socco:package_fs2:https://oss.sonatype.org/service/local/repositories/releases/archive/co/fs2/fs2-core_2.12/0.9.5/fs2-core_2.12-0.9.5-javadoc.jar/!",
            "-P:socco:package_io.circe:http://circe.github.io/circe/api/"
          )
        """.trim.replaceAll("\n", " ")
      }

      "examples/clean" :: "assembly" ::
        enablePlugin() :: "examples/compile" ::
        enablePlugin(Some("userStyle1")) :: "examples/compile" ::
        enablePlugin(Some("userStyle2"), true) :: "examples/compile" ::
        state
    }
  ).
  settings(addArtifact(artifact in (Compile, assembly), assembly): _*)

lazy val examples =
  project.
  settings(
    commonSettings,
    publishArtifact := false,
    libraryDependencies ++= Seq(
      "co.fs2" %% "fs2-core" % "0.9.5"
    ),
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser",
      "io.circe" %% "circe-optics"
    ).map(_ % "0.7.1")
  )
