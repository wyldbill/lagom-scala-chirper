import sbt._
import sbt.Defaults.sbtPluginExtra

libraryDependencies ++= {
  val sbtV = (sbtBinaryVersion in update).value
  val scalaV = (scalaBinaryVersion in update).value

  val defaultPlugins: Seq[ModuleID] =
    Seq(
      sbtPluginExtra("com.lightbend.lagom" % "lagom-sbt-plugin" % "1.4.0", sbtV, scalaV),
      sbtPluginExtra("com.github.ddispaltro" % "sbt-reactjs" % "0.5.2", sbtV, scalaV)
    )

  val sbtConductRPlugin = sbtPluginExtra("com.lightbend.conductr" % "sbt-conductr" % "2.5.1", sbtV, scalaV)
  val additionalPlugins: Seq[ModuleID] =
    sys.props.get("buildTarget") match {
      case Some(v) if v.toLowerCase == "conductr" => Seq(sbtConductRPlugin)
      case None => Seq(sbtConductRPlugin)
      case _ => Seq.empty
    }

  defaultPlugins ++ additionalPlugins
}
