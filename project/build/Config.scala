import sbt._

class ConfigProject(info: ProjectInfo) extends DefaultProject(info) {

  val junit = "junit" % "junit" % "4.8.2" % "test"
  val spec = "org.scala-tools.testing" % "specs_2.8.1" % "1.6.7" % "test"
  val snakeyaml = "org.yaml" % "snakeyaml" % "1.8"
  val poiOoxml = "org.apache.poi" % "poi-ooxml" % "3.6"
  val commonsIo = "commons-io" % "commons-io" % "2.0.1"
}
