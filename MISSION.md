# MISSION: Afk-Utility Multi-Version Migration (1.21 - 1.21.11)

## CRITICAL CONTEXT
You are tasked with implementing a **multi-version build system** for a Fabric Minecraft mod. This is NOT a simple refactoring - this is a **surgical precision operation** where ONE mistake breaks 12 builds.

## PROJECT STATE
- **Current**: Single-source monolith pretending to be multi-version via gradle.properties switch
- **Problem**: Same codebase cannot compile for 1.21 AND 1.21.11 due to API changes
- **Goal**: 12 separate .jar files (one per MC version) from a single source tree

## YOUR CONSTRAINTS
1. **NO HALLUCINATIONS**: Every change must be based on the plan below or explicit documentation
2. **INCREMENTAL VERIFICATION**: After EACH phase, run validation commands before proceeding
3. **PRESERVE FUNCTIONALITY**: The mod must work identically on all versions
4. **ATOMIC COMMITS**: Each phase = one Git commit with descriptive message
5. **FAIL-FAST**: If a build fails, STOP and report - do not attempt to "fix" without consulting the plan

## VALIDATION CHECKPOINTS
After implementing each phase, you MUST run:
```bash
# Verify Gradle recognizes Stonecutter
./gradlew tasks --all | grep stonecutter

# Verify version switching works
./gradlew :1.21:properties | grep "minecraft"
./gradlew :1.21.11:properties | grep "minecraft"

# Full build test (only after Phase 3+)
./gradlew chiseledBuild
```

## ROLLBACK PROTOCOL
If ANY step fails:
1. Run: `git status` and `git diff` to see changes
2. Document the EXACT error message
3. Run: `git reset --hard HEAD` to revert
4. Report failure before attempting alternative approaches

## CRITICAL FILES - MODIFY WITH EXTREME CARE
- `src/main/java/com/HelixCraft/afkutility/mixin/ConnectScreenMixin.java` (Mixin signatures are volatile)
- `src/main/java/com/HelixCraft/afkutility/gui/AfkUtilityScreen.java` (GUI widgets change between versions)
- `build.gradle` (One syntax error = all 12 builds fail)

## IMPLEMENTATION PLAN

---

# ✅ **DEFINITIVE RECOVERY PLAN: Afk-Utility Multi-Version Support (1.21 - 1.21.11)**

**Erstellt am:** 2026-01-11  
**Ziel:** 100% funktionsfähige Mod für ALLE Minecraft-Clients von 1.21 bis 1.21.11  
**Methode:** Stonecutter (dev.kikugie) + präzises Conditional Commenting

---

## **PHASE 0: KRITISCHE FAKTEN-ANALYSE**

### ✅ **Was durch Recherche herausgefunden wurde:**

1. **Stonecutter-Plugin:**
   - ✅ **Korrekter Identifier**: `dev.kikugie.stonecutter` version `0.7.11` (neueste stabile Version)
   - ❌ **NICHT**: `me.philippheuer.stonecutter` (existiert nicht)

2. **Mappings-Situation:**
   - ✅ **Aktuell verwendet**: `loom.officialMojangMappings()` (KORREKT für 1.21+)
   - ✅ **Yarn wird ab 1.21.11+ NICHT mehr updated** - bereits richtige Strategie
   - ✅ **Mojang Mappings sind ab 1.21 der Standard** - keine Migration nötig

3. **API-Änderungen zwischen 1.21 und 1.21.11:**
   - ✅ **1.21 - 1.21.10**: API ist **weitgehend stabil** (keine großen Breaks)
   - ✅ **1.21.11**: **KEINE ResourceLocation → Identifier Änderung** (das passiert erst in 26.1/unobfuscated)
   - ✅ **Für 1.21 - 1.21.11**: Primäre Probleme sind **GUI Widget-Signaturen** und **Mixin-Targets**

4. **Mixin-Volatilität:**
   - ⚠️ `ConnectScreenMixin.startConnecting()` - Signatur kann sich zwischen Minor-Versions ändern
   - ⚠️ `AfkUtilityScreen` - GUI-Widgets (`CycleButton.create()`, `AbstractSliderButton`) ändern sich häufig

---

## **PHASE 1: STONECUTTER-SETUP**

### **Schritt 1.1: `settings.gradle.kts` erstellen**

**Datei:** `/home/timon/devhub/Afk-Utility/settings.gradle.kts`
```kotlin
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
    centralScript = "build.gradle"
    
    create(rootProject) {
        // Definiere ALLE Minecraft-Versionen
        versions("1.21", "1.21.1", "1.21.2", "1.21.3", "1.21.4", 
                 "1.21.5", "1.21.6", "1.21.7", "1.21.8", "1.21.9", 
                 "1.21.10", "1.21.11")
        
        // VCS-Version für Git
        vcsVersion = "1.21.11"
    }
}
```

**VALIDATION AFTER THIS STEP:**
```bash
# Delete old settings.gradle if exists
rm settings.gradle

# Verify new file is recognized
./gradlew tasks --all | grep stonecutter
# Expected output: chiseledBuild, stonecutterSwap, etc.
```

### **Schritt 1.2: `stonecutter.gradle` erstellen**

**Datei:** `/home/timon/devhub/Afk-Utility/stonecutter.gradle`
```groovy
plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "1.21.11" /* [SC] DO NOT EDIT */

// Register chiseled build task für ALLE Versionen
stonecutter.registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
    group = "project"
    ofTask("build")
}
```

**VALIDATION AFTER THIS STEP:**
```bash
# Verify active version
./gradlew properties | grep "stonecutter"
# Expected: stonecutter.current.version=1.21.11

# Test version switching
./gradlew :1.21:properties
./gradlew :1.21.5:properties
```

---

## **PHASE 2: BUILD-SCRIPT-REFACTORING**

### **Schritt 2.1: `build.gradle` umschreiben**

**⚠️ BACKUP FIRST:** `cp build.gradle build.gradle.backup`

**Datei:** `/home/timon/devhub/Afk-Utility/build.gradle`
```groovy
plugins {
    id 'net.fabricmc.fabric-loom-remap' version "1.8-SNAPSHOT"
    id 'maven-publish'
}

// Stonecutter provides stonecutter.current.version
def mcVersion = stonecutter.current.version
def mcVersionData = [
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
    "1.21.11": ["1.21.11+build.4", "0.18.4", "0.141.1+1.21.11"]
]

project.ext.yarn_mappings = mcVersionData[mcVersion][0]
project.ext.loader_version = mcVersionData[mcVersion][1]
project.ext.fabric_api_version = mcVersionData[mcVersion][2]

version = project.mod_version
group = project.maven_group

base {
    archivesName = project.archives_base_name
}

repositories {}

dependencies {
    minecraft "com.mojang:minecraft:${mcVersion}"
    mappings loom.officialMojangMappings()
    modImplementation "net.fabricmc:fabric-loader:${project.loader_version}"
    modImplementation "net.fabricmc.fabric-api:fabric-api:${project.fabric_api_version}"
}

processResources {
    inputs.property "version", project.version
    inputs.property "minecraft_version", mcVersion
    filesMatching("fabric.mod.json") {
        expand "version": project.version, "minecraft_version": mcVersion
    }
}

tasks.withType(JavaCompile).configureEach {
    it.options.release = 21
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName}"}
    }
}

publishing {
    publications {
        create("mavenJava", MavenPublication) {
            artifactId = project.archives_base_name
            from components.java
        }
    }
    repositories {}
}
```

**VALIDATION AFTER THIS STEP:**
```bash
# Test single version build
./gradlew :1.21.11:build
# Expected: SUCCESS (even if code has version-specific issues)

# Test version data extraction
./gradlew :1.21:properties | grep fabric_api_version
# Expected: 0.102.0+1.21

./gradlew :1.21.11:properties | grep fabric_api_version
# Expected: 0.141.1+1.21.11
```

### **Schritt 2.2: Versioned `gradle.properties` erstellen**

**SKIP THIS STEP** - Nicht nötig, da alle Daten bereits in `build.gradle` zentral definiert sind.

---

## **PHASE 3: SOURCE-CODE-CONDITIONALS**

### **⚠️ CRITICAL: Read Stonecutter Comment Syntax**

Stonecutter uses special comment syntax:
- `//? if <condition> {` opens a conditional block
- `//?}` closes a conditional block  
- Code inside `/* */` is INCLUDED when condition is TRUE
- Code outside comments is INCLUDED when condition is FALSE

**Example:**
```java
//? if >=1.21.5 {
/*// This code runs on 1.21.5+
methodNewAPI();
 *///?} else {
// This code runs on 1.21 - 1.21.4
methodOldAPI();
//?}
```

### **Schritt 3.1: GUI-Code versionieren (`AfkUtilityScreen.java`)**

**⚠️ BACKUP FIRST:** `cp src/main/java/com/HelixCraft/afkutility/gui/AfkUtilityScreen.java src/main/java/com/HelixCraft/afkutility/gui/AfkUtilityScreen.java.backup`

**Problem:** `CycleButton.create()` and other GUI methods change signatures

**Identify version-specific code by searching for:**
- `CycleButton.create(`
- `AbstractSliderButton` constructor calls
- Any GUI component constructors

**Add conditionals as needed** (ONLY where actual API differences exist)

**VALIDATION AFTER THIS STEP:**
```bash
# Test on oldest version
./gradlew :1.21:build
# Expected: SUCCESS

# Test on newest version
./gradlew :1.21.11:build
# Expected: SUCCESS

# If either fails, check compiler errors for ACTUAL API differences
```

### **Schritt 3.2: Mixin-Targets versionieren**

**⚠️ BACKUP FIRST:** `cp src/main/java/com/HelixCraft/afkutility/mixin/ConnectScreenMixin.java src/main/java/com/HelixCraft/afkutility/mixin/ConnectScreenMixin.java.backup`

**Before modifying:**
1. Test compile on 1.21: `./gradlew :1.21:compileJava`
2. Test compile on 1.21.11: `./gradlew :1.21.11:compileJava`
3. **ONLY add conditionals if one fails with method signature error**

**If method signature differs, use:**
```java
@Mixin(ConnectScreen.class)
public class ConnectScreenMixin {
    //? if >=1.21.8 {
    /*@Inject(method = "startConnecting(Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/multiplayer/resolver/ServerAddress;Lnet/minecraft/client/multiplayer/ServerData;Lnet/minecraft/client/multiplayer/TransferState;)V", at = @At("HEAD"))
     *///?} else {
    @Inject(method = "startConnecting", at = @At("HEAD"))
    //?}
    private static void onStartConnecting(/* parameters */, CallbackInfo ci) {
        // Your logic
    }
}
```

**VALIDATION AFTER THIS STEP:**
```bash
# Compile all versions
./gradlew :1.21:compileJava
./gradlew :1.21.5:compileJava
./gradlew :1.21.8:compileJava
./gradlew :1.21.11:compileJava
# Expected: ALL SUCCESS
```

---

## **PHASE 4: FULL VALIDATION**

### **Test-Sequenz (100% Coverage):**
```bash
# Test 1: Älteste Version
./gradlew :1.21:build
# Erwartet: SUCCESS + afkutility-1.0.0-1.21.jar

# Test 2: Mid-Version
./gradlew :1.21.5:build
# Erwartet: SUCCESS + afkutility-1.0.0-1.21.5.jar

# Test 3: Problem-Version
./gradlew :1.21.8:build
# Erwartet: SUCCESS + afkutility-1.0.0-1.21.8.jar

# Test 4: Neueste Version
./gradlew :1.21.11:build
# Erwartet: SUCCESS + afkutility-1.0.0-1.21.11.jar

# Test 5: ALLE Versionen gleichzeitig
./gradlew chiseledBuild
# Erwartet: 12 .jar files in build/libs/
```

**Success criteria:**
- All 12 builds complete without errors
- Each .jar is between 50KB - 500KB (reasonable size)
- `build/libs/` contains exactly 12 files

---

## **PHASE 5: VERÖFFENTLICHUNG**

### **Final Build for Distribution:**
```bash
# Clean old builds
./gradlew clean

# Build all versions
./gradlew chiseledBuild

# Verify output
ls -lh build/libs/
```

**Expected output:**
```
build/libs/
├── afkutility-1.0.0-1.21.jar
├── afkutility-1.0.0-1.21.1.jar
├── afkutility-1.0.0-1.21.2.jar
├── afkutility-1.0.0-1.21.3.jar
├── afkutility-1.0.0-1.21.4.jar
├── afkutility-1.0.0-1.21.5.jar
├── afkutility-1.0.0-1.21.6.jar
├── afkutility-1.0.0-1.21.7.jar
├── afkutility-1.0.0-1.21.8.jar
├── afkutility-1.0.0-1.21.9.jar
├── afkutility-1.0.0-1.21.10.jar
└── afkutility-1.0.0-1.21.11.jar
```

---

## **SUCCESS CRITERIA**

This migration is COMPLETE when:
1. ✅ `./gradlew chiseledBuild` succeeds without errors
2. ✅ 12 .jar files exist in `build/libs/`
3. ✅ Each .jar loads in the corresponding Minecraft version without crashes
4. ✅ All mod features work identically across versions
5. ✅ No compiler warnings about deprecated APIs

---

## **EMERGENCY ROLLBACK**

If at ANY point the migration breaks:
```bash
# Full rollback
git reset --hard HEAD
git clean -fd

# Restore backups if needed
cp build.gradle.backup build.gradle
cp src/main/java/com/HelixCraft/afkutility/gui/AfkUtilityScreen.java.backup src/main/java/com/HelixCraft/afkutility/gui/AfkUtilityScreen.java
cp src/main/java/com/HelixCraft/afkutility/mixin/ConnectScreenMixin.java.backup src/main/java/com/HelixCraft/afkutility/mixin/ConnectScreenMixin.java
```

---

## **YOUR RESPONSE PROTOCOL**

After EACH phase, report:
1. ✅ or ❌ Status
2. Command output (if failed)
3. Next step to take
4. Request permission to proceed to next phase

**DO NOT** proceed to next phase without explicit confirmation.

---

**AUTHORIZATION TO BEGIN:** Execute Phase 1 now. Report results before proceeding.