buildscript {
  repositories {
    google()
    mavenCentral()
    maven("https://dl.bintray.com/android/android-tools")
    maven("https://storage.googleapis.com/r8-releases/raw")
    maven("https://kotlin.bintray.com/kotlinx")
  }

  dependencies {
    classpath(kotlin("gradle-plugin", Versions.Kotlin.LANGUAGE))
    classpath("com.android.tools.build:gradle:7.1.2")
    classpath("com.android.tools:r8:3.1.51")
  }
}
