plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
}


android {
  compileSdk = Versions.Android.COMPILE
  buildToolsVersion = Versions.Android.TOOLS

  defaultConfig {
    applicationId = "com.pocketimps.test.bandlab.wavetrimmer"

    minSdk = Versions.Android.MIN
    targetSdk = Versions.Android.TARGET

    versionCode = 1
    versionName = "1.0.$versionCode"

    setProperty("archivesBaseName", "wavetrimmer-$versionName.${getGitHash()}")
    resourceConfigurations += "en"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  kapt.useBuildCache = true

  sourceSets {
    getByName("main").java.srcDir("src/main/kotlin")
  }

  signingConfigs.create("default") {
    storeFile = File("$rootDir/signing.keystore")
    storePassword = "android"
    keyAlias = "androiddebugkey"
    keyPassword = "android"
  }

  buildTypes {
    getByName("debug") {
      applicationIdSuffix = ".debug"
      versionNameSuffix = "-debug"
      signingConfig = signingConfigs.getByName("default")
    }

    getByName("release") {
      isMinifyEnabled = false
      signingConfig = signingConfigs.getByName("default")
    }
  }

  packagingOptions {
    resources.excludes += setOf("**.properties",
                                "kotlin*/**",
                                "third_party/**",
                                "**/**.version",
                                "**/**.kotlin_module")
    if (!isDebug())
      resources.excludes += "DebugProbesKt.bin"
  }

  dependenciesInfo {
    includeInApk = false
  }

  buildFeatures.viewBinding = true
  lint.checkReleaseBuilds = false

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  kotlinOptions {
    jvmTarget = "1.8"
  }
}


dependencies {
  implementation(project(":core"))

  implementation("io.insert-koin:koin-android:${Versions.Utils.KOIN}")

  // Kotlin
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.Kotlin.LANGUAGE}")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.Kotlin.COROUTINES}")

  // Support libs
  implementation("androidx.core:core-ktx:1.9.0-alpha02")
  implementation("androidx.appcompat:appcompat:1.4.1")
  implementation("androidx.activity:activity-ktx:1.6.0-alpha01")
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.0-alpha05")
  implementation("com.google.android.material:material:1.6.0-alpha03")

  // Utils
  implementation("com.pocketimps:extlib:${Versions.Utils.EXTLIB}")

  // Testing
  testImplementation("junit:junit:${Versions.Utils.JUNIT}")
}
