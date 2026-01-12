# Stonecutter Recovery Plan: 1.21 - 1.21.11 Support

## 1. Analysis of Current Failure
The current project (Afk-Utility) fails to support the full range of 1.21 to 1.21.11 because it relies on a **Single-Source Build Strategy** (`mcVersionData` map in `build.gradle`). This approach forces a single codebase (`src/main/java`) to compile against all Minecraft versions. This is impossible when:
1.  **Class Names Change**: e.g., `Identifier` vs `ResourceLocation` (likely changed in 1.21.5+).
2.  **Method Signatures Change**: `KeyMapping` constructors or `Screen` methods often change between minor versions.
3.  **Logic Changes**: New features (like 1.21.8 features) require code that doesn't exist in 1.21.

The existing `src/${version}` directories are currently **ignored** by the build system. The `settings.gradle` file lacks the necessary Stonecutter configuration to define these projects.

## 2. Recovery Architecture (Stonecutter)
We will transition to a **Chiseled Build System** using the Stonecutter Gradle Plugin.
*   **Central Controller**: `settings.gradle.kts` defines all version targets.
*   **Version Data**: `versions.json` (or defined in settings) acts as the single source of truth for Yarn/Fabric versions.
*   **Source Splitting**: Code remains in `src/main` *only if it works on ALL versions*. Any version-specific code moves to `src/common` (abstract) or `src/1.21`, `src/1.21.5`, etc.

### Version Targets & Breakpoints
Based on file analysis and standard API shifts, we will define these source sets:

| Source Set | Targets | Purpose |
| :--- | :--- | :--- |
| `src/main` | **ALL** | Logic that uses stable APIs (1.21.0 - 1.21.11). |
| `src/1.21` | 1.21 - 1.21.4 | Presumed Legacy API (e.g. `Identifier`). |
| `src/1.21.5` | 1.21.5 - 1.21.11 | New API (e.g. `ResourceLocation`). |
*(Note: If 1.21.8 introduces further breaks, we will add `src/1.21.8`)*

## 3. Step-by-Step Implementation Plan

### Step 1: Initialize Stonecutter Configuration
**Action**: Create `settings.gradle.kts` (Replacing `settings.gradle`).
**Details**:
*   Apply `me.philippheuer.stonecutter` version `0.4.5`.
*   Define the `stonecutter` block with the following exact version matrix (derived from your current `build.gradle`):

```kotlin
plugins {
    id("me.philippheuer.stonecutter") version "0.4.5"
}

stonecutter {
    val fabricApi = mapOf(
        "1.21" to "0.102.0+1.21",
        "1.21.1" to "0.116.7+1.21.1",
        "1.21.2" to "0.106.1+1.21.2",
        "1.21.3" to "0.114.1+1.21.3",
        "1.21.4" to "0.119.4+1.21.4",
        "1.21.5" to "0.128.2+1.21.5", // API Breakpoint?
        "1.21.6" to "0.128.2+1.21.6",
        "1.21.7" to "0.129.0+1.21.7",
        "1.21.8" to "0.136.1+1.21.8", // User reported errors here
        "1.21.9" to "0.134.1+1.21.9",
        "1.21.10" to "0.138.4+1.21.10",
        "1.21.11" to "0.141.0+1.21.11"
    )
    
    // Define versions
    versions(fabricApi.keys) { version ->
        // Define common variables per version
        val build = when(version) { "1.21" -> "9"; "1.21.1" -> "3"; /* ... fill all ... */ else -> "1" }
        mapOf("yarn" to "${version}+build.${build}", "fabric" to fabricApi[version])
    }
}
```

### Step 2: Refactor `build.gradle`
**Action**: Rewrite `build.gradle` to use Stonecutter properties.
**Details**:
*   Remove `def mcVersionData`.
*   Update dependencies:
    ```groovy
    dependencies {
        minecraft "com.mojang:minecraft:${stonecutter.current.version}"
        mappings "net.fabricmc:yarn:${stonecutter.eval(stonecutter.current.project, 'yarn')}:v2"
        modImplementation "net.fabricmc.fabric-api:fabric-api:${stonecutter.eval(stonecutter.current.project, 'fabric')}"
    }
    ```
*   Ensure `remapJar` and `processResources` use `stonecutter.current.version`.

### Step 3: Normalize Source Directories
**Action**: Restructure `src` folder.
**Details**:
*   **Current State**: `src/main` (mixed), `src/1.21` (ignored), `v1_21` (ignored).
*   **New State**:
    *   `src/main` -> **COMMON CORE**.
    *   `src/1.21` -> **Active** source set for older versions.
    *   `src/1.21.5` -> **Active** source set for newer versions.
*   **Correction**: Identify files using `Identifier` vs `ResourceLocation`.
    *   If `AfkUtilityScreen` fails on 1.21.8, move `AfkUtilityScreen.java` from `src/main` to `src/1.21` AND `src/1.21.5`.
    *   Fix the 1.21.5+ copy to use the correct API (`ResourceLocation`, new KeyMapping, etc.).

### Step 4: Verification (The "1000%" Assurance)
We will run a specific validation sequence:
1.  `./gradlew setVersion 1.21` -> `./gradlew build` (Must Pass)
2.  `./gradlew setVersion 1.21.5` -> `./gradlew build` (Must Pass)
3.  `./gradlew setVersion 1.21.8` -> `./gradlew build` (Must Pass - Critical Check)
4.  `./gradlew setVersion 1.21.11` -> `./gradlew build` (Must Pass)
