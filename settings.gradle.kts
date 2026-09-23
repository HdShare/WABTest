@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenLocal()
        mavenCentral()
        maven("https://api.xposed.info") {
            content {
                includeGroup("de.robv.android.xposed")
            }
        }
        maven("https://raw.githubusercontent.com/HighCapable/maven-repository/main/repository/releases") {
            content {
                includeGroupByRegex("com\\.highcapable.*")
            }
        }
    }
}

rootProject.name = "WABTest"
include(":app")
