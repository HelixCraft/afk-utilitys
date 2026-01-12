pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://maven.kikugie.dev/releases")
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.7.11"
}

stonecutter {
    kotlinController = true
    centralScript = "stonecutter.gradle.kts"
    
    create(rootProject) {
        // Define ALL Minecraft versions (1.21 - 1.21.11)
        versions("1.21", "1.21.1", "1.21.2", "1.21.3", "1.21.4", 
                 "1.21.5", "1.21.6", "1.21.7", "1.21.8", "1.21.9", 
                 "1.21.10", "1.21.11")
        
        // VCS version for Git (newest version)
        vcsVersion = "1.21.11"
    }
}

rootProject.name = "Afk-Utility"
println("Settings configured, adding versions...")
