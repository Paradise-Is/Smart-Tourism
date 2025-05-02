pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        //声明 Kotlin Android 插件的版本
        id("org.jetbrains.kotlin.android") version "1.8.21"
        //声明 Google services 插件的版本
        id("com.google.gms.google-services") version "4.3.15"
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        //添加JitPack
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "Smart Tourism"
include(":app")

 