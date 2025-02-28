@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://api.xposed.info") }
        maven { setUrl("https://maven.aliyun.com/repository/public") }
        maven { setUrl("https://maven.aliyun.com/repository/google") }
        maven { setUrl("https://maven.aliyun.com/repository/gradle-plugin") }
        //python library
        maven { setUrl("https://chaquo.com/maven") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
//        mavenLocal { content { includeGroup("com.highcapable.yukihookapi") } }
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://api.xposed.info") }
        maven { setUrl("https://maven.aliyun.com/repository/public") }
        maven { setUrl("https://maven.aliyun.com/repository/google") }
        maven { setUrl("https://maven.aliyun.com/repository/gradle-plugin") }
        //python library
        maven { setUrl("https://chaquo.com/maven") }
    }
}
rootProject.name = "LuckyTool"
include(":app")
