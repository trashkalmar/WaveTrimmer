import org.gradle.api.Project
import java.io.ByteArrayOutputStream


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
