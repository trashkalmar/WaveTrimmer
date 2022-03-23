import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.util.*


fun Project.getGitHash(): String {
  val stdout = ByteArrayOutputStream()
  val res = exec {
    commandLine("git", "rev-parse", "--short", "HEAD")
    standardOutput = stdout
  }

  return if (res.exitValue == 0)
    stdout.toString().trim()
  else
    "XXXXXXX"
}


fun Project.isDebug() = (gradle.startParameter.taskNames.any { it.endsWith("Debug") })
