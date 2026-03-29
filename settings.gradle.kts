pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "telegram"

// Use local engine checkout if available, otherwise fall back to published Maven artifact
fun tryIncludeBuild(path: String) {
    val dir = rootProject.projectDir.resolve("../$path")
    if (dir.exists()) {
        logger.lifecycle("pickr-telegram: using local composite build for $path")
        includeBuild(dir)
    }
}

tryIncludeBuild("engine")
