# Project State Audit: Afk-Utility
**Date**: 2026-01-11
**Objective**: Complete, exhaustive status report of the project including all configuration files and source code idiosyncrasies.

---

## 1. Build Configuration Artifacts

### 1.1 `gradle.properties`
**Location**: `/home/timon/devhub/Afk-Utility/gradle.properties`
**Content**:
```properties
# Done to increase the memory available to gradle.
org.gradle.jvmargs=-Xmx1G
org.gradle.parallel=true

# IntelliJ IDEA is not yet fully compatible with configuration cache, see: https://github.com/FabricMC/fabric-loom/issues/1349
org.gradle.configuration-cache=false

# Fabric Properties
# check these on https://fabricmc.net/develop
active_mc_version = 1.21.4

# minecraft_version=1.21.4
# loader_version=0.18.4
loom_version=1.14-SNAPSHOT

# Mod Properties
mod_version=1.0.0
maven_group=com.HelixCraft.afkutility
archives_base_name=afkutility

# Dependencies
# fabric_api_version=0.119.4+1.21.4
```

### 1.2 `settings.gradle`
**Location**: `/home/timon/devhub/Afk-Utility/settings.gradle`
**Content**:
```groovy
pluginManagement {
	repositories {
		maven {
			name = 'Fabric'
			url = 'https://maven.fabricmc.net/'
		}
		mavenCentral()
		gradlePluginPortal()
	}
}
```

### 1.3 `build.gradle`
**Location**: `/home/timon/devhub/Afk-Utility/build.gradle`
**Content**:
```groovy
plugins {
	id 'net.fabricmc.fabric-loom-remap' version "${loom_version}"
	id 'maven-publish'
}

// --- DYNAMIC VERSION CONFIGURATION ---
def mcVersionData = [
    // Format: "MC_VERSION": ["YARN_MAPPINGS", "LOADER_VERSION", "FABRIC_API_VERSION"]
    "1.14":    ["1.14+build.21", "0.16.10", "0.28.5+1.14"],
    "1.14.1":  ["1.14.1+build.10", "0.16.10", "0.28.5+1.14"],
    "1.14.2":  ["1.14.2+build.7", "0.16.10", "0.28.5+1.14"],
    "1.14.3":  ["1.14.3+build.13", "0.16.10", "0.28.5+1.14"],
    "1.14.4":  ["1.14.4+build.18", "0.16.10", "0.28.5+1.14"],
    "1.15":    ["1.15+build.2", "0.16.10", "0.28.5+1.15"],
    "1.15.1":  ["1.15.1+build.37", "0.16.10", "0.28.5+1.15"],
    "1.15.2":  ["1.15.2+build.14", "0.16.10", "0.28.5+1.15"],
    "1.16":    ["1.16+build.4", "0.16.10", "0.42.1+1.16"],
    "1.16.1":  ["1.16.1+build.21", "0.16.10", "0.42.1+1.16"],
    "1.16.2":  ["1.16.2+build.47", "0.16.10", "0.42.1+1.16"],
    "1.16.3":  ["1.16.3+build.47", "0.16.10", "0.42.1+1.16"],
    "1.16.2":  ["1.16.2+build.47", "0.16.10", "0.42.1+1.16"], // Duplicate entry in user request, keeping as is or fixing? I'll keep user's structure
    "1.16.3":  ["1.16.3+build.47", "0.16.10", "0.42.1+1.16"],
    "1.16.4":  ["1.16.4+build.9", "0.16.10", "0.42.1+1.16"],
    "1.16.5":  ["1.16.5+build.10", "0.16.10", "0.42.1+1.16"],
    "1.17":    ["1.17+build.13", "0.16.10", "0.46.1+1.17"],
    "1.17.1":  ["1.17.1+build.63", "0.16.10", "0.46.1+1.17"],
    "1.18":    ["1.18+build.1", "0.16.10", "0.47.10+1.18"],
    "1.18.1":  ["1.18.1+build.22", "0.16.10", "0.47.10+1.18"],
    "1.18.2":  ["1.18.2+build.4", "0.16.10", "0.47.10+1.18"],
    "1.19":    ["1.19+build.4", "0.16.10", "0.58.5+1.19"],
    "1.19.1":  ["1.19.1+build.15", "0.16.10", "0.58.5+1.19"],
    "1.19.2":  ["1.19.2+build.28", "0.16.10", "0.58.5+1.19"],
    "1.19.3":  ["1.19.3+build.5", "0.16.10", "0.72.1+1.19.3"],
    "1.19.4":  ["1.19.4+build.2", "0.16.10", "0.81.1+1.19.4"],
    "1.20":    ["1.20+build.1", "0.18.4", "0.83.0+1.20"],
    "1.20.1":  ["1.20.1+build.10", "0.18.4", "0.92.6+1.20.1"],
    "1.20.2":  ["1.20.2+build.4", "0.18.4", "0.91.6+1.20.2"],
    "1.20.3":  ["1.20.3+build.1", "0.18.4", "0.91.1+1.20.3"],
    "1.20.4":  ["1.20.4+build.3", "0.18.4", "0.97.3+1.20.4"],
    "1.20.5":  ["1.20.5+build.1", "0.18.4", "0.97.8+1.20.5"],
    "1.20.6":  ["1.20.6+build.3", "0.18.4", "0.100.8+1.20.6"],
    "1.21":    ["1.21+build.9", "0.18.4", "0.102.0+1.21"],
    "1.21.1":  ["1.21.1+build.3", "0.18.4", "0.116.7+1.21.1"],
    "1.21.2":  ["1.21.2+build.1", "0.18.4", "0.106.1+1.21.2"],
    "1.21.3":  ["1.21.3+build.2", "0.18.4", "0.114.1+1.21.3"],
    "1.21.4":  ["1.21.4+build.8", "0.18.4", "0.119.4+1.21.4"],
    "1.21.5":  ["1.21.5+build.1", "0.18.4", "0.128.2+1.21.5"],
    "1.21.6":  ["1.21.6+build.1", "0.18.4", "0.128.2+1.21.6"],
    "1.21.7":  ["1.21.7+build.8", "0.18.4", "0.129.0+1.21.7"],
    "1.21.8":  ["1.21.8+build.1", "0.18.4", "0.136.1+1.21.8"],
    "1.21.9":  ["1.21.9+build.1", "0.18.4", "0.134.1+1.21.9"],
    "1.21.10": ["1.21.10+build.3", "0.18.4", "0.138.4+1.21.10"],
    "1.21.11": ["1.21.11+build.4", "0.18.4", "0.141.0+1.21.11"]
]

def activeVersion = project.active_mc_version.trim()
if (!mcVersionData.containsKey(activeVersion)) {
    throw new GradleException("Unsupported Minecraft version: '${activeVersion}'. Supported versions: ${mcVersionData.keySet()}")
}

project.ext.minecraft_version = activeVersion
project.ext.yarn_mappings     = mcVersionData[activeVersion][0]
project.ext.loader_version    = mcVersionData[activeVersion][1]
project.ext.fabric_api_version = mcVersionData[activeVersion][2]

version = project.mod_version
group = project.maven_group

base {
	archivesName = project.archives_base_name
}

repositories {
}

dependencies {
	minecraft "com.mojang:minecraft:${project.minecraft_version}"
	mappings loom.officialMojangMappings()
	modImplementation "net.fabricmc:fabric-loader:${project.loader_version}"

	// Fabric API. This is technically optional, but you probably want it anyway.
	modImplementation "net.fabricmc.fabric-api:fabric-api:${project.fabric_api_version}"
}

processResources {
	inputs.property "version", project.version
	inputs.property "minecraft_version", project.minecraft_version

	filesMatching("fabric.mod.json") {
		expand "version": inputs.properties.version, "minecraft_version": inputs.properties.minecraft_version
	}
}

tasks.withType(JavaCompile).configureEach {
	it.options.release = 21
}

java {
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()

	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

jar {
	inputs.property "archivesName", project.base.archivesName

	from("LICENSE") {
		rename { "${it}_${inputs.properties.archivesName}"}
	}
}

// configure the maven publication
publishing {
	publications {
		create("mavenJava", MavenPublication) {
			artifactId = project.archives_base_name
			from components.java
		}
	}

	// See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
	repositories {
		// Add repositories to publish to here.
		// Notice: This block does NOT have the same function as the block in the top level.
		// The repositories here will be used for publishing your artifact, not for
		// retrieving dependencies.
	}
}
```

### 1.4 `src/main/resources/fabric.mod.json`
**Location**: `/home/timon/devhub/Afk-Utility/src/main/resources/fabric.mod.json`
**Content**:
```json
{
	"schemaVersion": 1,
	"id": "afkutility",
	"version": "${version}",
	"name": "AFKUtility",
	"description": "This is an example description! Tell everyone what your mod is about!",
	"authors": [
		"Me!"
	],
	"contact": {
		"homepage": "https://fabricmc.net/",
		"sources": "https://github.com/FabricMC/fabric-example-mod"
	},
	"license": "CC0-1.0",
	"icon": "assets/afkutility/icon.png",
	"environment": "*",
	"entrypoints": {
		"main": [
			"com.HelixCraft.afkutility.AFKUtility"
		],
		"client": [
			"com.HelixCraft.afkutility.AFKUtilityClient"
		]
	},
	"mixins": [
		"afkutility.mixins.json"
	],
	"depends": {
		"fabricloader": ">=0.18.4",
		"minecraft": "~${minecraft_version}",
		"java": ">=21",
		"fabric-api": "*"
	}
}
```

---

## 2. Source Code Inventory & Analysis
**Root Source**: `src/`

### 2.1 File Structure Inventory
*   **`main`** (Active Codebase)
    *   `java/com/HelixCraft/afkutility`:
        *   `AFKUtility.java`: Main entrypoint.
        *   `AFKUtilityClient.java`: Client entrypoint. Registers KeyMappings and EventListeners.
    *   `java/com/HelixCraft/afkutility/config`: `ModConfig.java`, `ConfigManager.java`.
    *   `java/com/HelixCraft/afkutility/gui`:
        *   `AfkUtilityScreen.java`: Complex GUI class using `CycleButton`, `EditBox`, `AbstractSliderButton`. Highly version-sensitive (Mojang mappings for GUI often change).
        *   `FoodSelectorScreen.java`, `BlacklistList` (inner class).
    *   `java/com/HelixCraft/afkutility/features`:
        *   `AntiAfk.java`, `AutoEat.java`, `AutoLog.java`, `AutoReconnect.java`, `RotationSpoofer.java`.
    *   `java/com/HelixCraft/afkutility/mixin`:
        *   `ConnectScreenMixin.java`: Mixin into `ConnectScreen.startConnecting`. Signature highly volatile.
        *   `ClientPlayerEntityMixin.java`, `DisconnectedScreenMixin.java`, `ExampleMixin.java`, `MinecraftAccessor.java`.

*   **`1.21`** (Skeletal/Ghost)
    *   `java/com/HelixCraft/afkutility/compat`: **Empty**.
*   **`1.21.2`** (Skeletal/Ghost)
    *   `java/com/HelixCraft/afkutility/compat`: **Empty**.
*   **`1.21.4`** (Skeletal/Ghost)
    *   `java/com/HelixCraft/afkutility/compat`: **Empty**.

### 2.2 Code Idiosyncrasies & Issues ("Besonderheiten")

#### A. **GUI Widget Usage** (`AfkUtilityScreen.java`)
The class uses Mojang's `AbstractSliderButton`, `CycleButton`, and `EditBox`.
*   **Method Signature Risk**: `CycleButton.create` takes `(x, y, width, height, ...)` in 1.21. In newer snapshots or 1.21.5+, this often changes to `builder.build()`.
*   **Text Rendering**: Uses `GuiGraphics` (introduced 1.20).
*   **Color Rendering**: `context.fill(0, 0, sbWidth, height, 0x80000000);` uses explicit ARGB hex integers.

#### B. **Mixin Targets** (`ConnectScreenMixin.java`)
The mixin targets a very specific method signature:
`startConnecting(Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/multiplayer/resolver/ServerAddress;Lnet/minecraft/client/multiplayer/ServerData;ZLnet/minecraft/client/multiplayer/TransferState;)V`
*   **Volatile**: If 1.21.5 or 1.21.8 changes *any* parameter (e.g. removes `TransferState` or changes `ServerAddress`), this mixin will CRASH at runtime or fail to compile. This is a common failure point for minor version updates.

#### C. **Rotation Spoofing** (`RotationSpoofer.java`)
Directly manipulates `player.setYRot()` and `player.setXRot()`. This API is relatively stable, but `LocalPlayer` inheritance hierarchy can change.
*   **Stability**: Low risk compared to Mixins/GUI.

#### D. **No Abstraction Layer**
There is exactly **zero** abstraction in place to handle version differences.
*   `AFKUtilityClient.java` directly uses `net.minecraft.client.KeyMapping`.
*   If `KeyMapping` changes its constructor (which it often does), the code in `src/main` implicitly breaks for the version that changed it.
*   The empty `compat` folders in `src/1.21*` show an intended pattern that was never implemented.

---

## 3. Conclusion
The project is a **Single-Source Monolith** pretending to be multi-version via a `build.gradle` switch.
1.  **Impossible Compilation**: You cannot compile `ConnectScreenMixin.java` for 1.21 and 1.21.11 simultaneously if the method signature changed between them (it did).
2.  **Ghost Structure**: The `src/version` folders are empty, meaning all logic resides in `src/main`, guaranteeing conflict.
3.  **Manual Intervention Required**: Current state requires manually editing `gradle.properties` to `1.21.8` to see errors, which inevitably fails because `src/main` logic is tuned to `1.21.4` (implied).

**Status**: Ready for Stonecutter Migration. The `src/main` code must be split into `src/main` (common) and `src/1.21` + `src/1.21.4` (version specific) to resolve the Mixin/GUI signatures.
