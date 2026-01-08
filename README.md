# AFK Utilitys

![Environment](https://img.shields.io/badge/Environment-Client-purple)
[![Java 21](https://img.shields.io/badge/Language-Java%2021-orange)](https://www.oracle.com/java/technologies/downloads/#java21)
[![Modrinth](https://img.shields.io/badge/Modrinth-AFK--Utility-00AF5C?logo=modrinth)](https://modrinth.com/mod/afk-utilitys)

<p align="left">
  <a href="https://modrinth.com/mod/afk-utilitys">
    <img src="https://github.com/user-attachments/assets/6bc92930-84f9-4eb1-ae1d-8f79775b87c6" width="200" alt="Download on Modrinth">
  </a>
</p>

An advanced utility mod for Minecraft Fabric that manages AFK (Away From Keyboard) states with intelligent automation and safety features.

> **Settings Keybind: `K`**

## Use Cases

- **Server AFK Protection**: Prevent kicks from AFK timeout systems
- **Safety Management**: Auto-disconnect when health drops or taking damage
- **Automatic Reconnection**: Never lose your spot during server restarts
- **Hunger Management**: Keep your hunger bar full while away
- **Farm Automation**: Maintain activity for passive mob farms and auto-clickers

<div align="center">
  <img src="https://github.com/user-attachments/assets/02730b70-11d9-47bf-9b59-a8cf227b1755" width="1000" />
</div>

## Features

### Anti-AFK System

Simulates natural player activity to prevent server kicks:

- **Auto-Jump**: Randomized jumping intervals for natural appearance
- **Auto-Swing**: Periodic hand swinging without block interaction
- **Auto-Sneak**: Configurable crouch timing and intervals
- **Strafe Mode**: Automatic left-right movement to avoid coordinate-based detection
- **Spin Mode**:
  - **Client Mode**: Rotates your camera view visually
  - **Server Mode**: Sends rotation packets while keeping your screen static
- **Chat Messages**: Send periodic messages with custom text
  - Interval range: 0.1 to 30 minutes
  - Custom message support

### Auto Reconnect

Smart reconnection system for uninterrupted gameplay:

- **Server Retention**: Automatically remembers last connected server
- **Countdown Timer**: Visual countdown on disconnect screen
- **Quick Toggle**: Enable/disable directly from disconnect screen
- **Configurable Delay**: Set custom wait time before reconnection attempt

### Auto Log (Safety Disconnect)

Protect your character while away:

- **HP Threshold**: Disconnect when health drops below configured value
- **Instant Damage Mode**: Disconnect immediately upon taking any damage
- **Safety Lock**: Optional Auto Reconnect disable after Auto Log trigger
- **Death Prevention**: Keeps you safe in dangerous situations

### Auto Eat

Intelligent hunger management:

- **Smart Timing**: Only eats when hunger restoration won't be wasted
- **Blacklist Support**: Exclude specific items (e.g., Rotten Flesh, Spider Eyes)
- **Efficient Consumption**: Calculates optimal eating times based on food value

## Installation

### Requirements

- Minecraft 1.21.4
- Fabric Loader 0.16.0+

### Download

Download the mod from Modrinth and place the `.jar` file into your `.minecraft/mods` folder.

[**Download on Modrinth**](https://modrinth.com/mod/afk-utilitys)

<a href="https://modrinth.com/mod/afk-utilitys">
  <img src="https://github.com/user-attachments/assets/6bc92930-84f9-4eb1-ae1d-8f79775b87c6" width="200" alt="Download on Modrinth">
</a>

## Configuration

The mod features a sidebar-based configuration UI with four main categories:

1. **Anti-AFK**: Configure all anti-kick features and intervals
2. **Auto-Eat**: Set hunger thresholds and manage food blacklist
3. **Reconnect**: Adjust reconnection delay and toggle auto-reconnect
4. **Auto-Log**: Configure health thresholds and safety settings

### Configuration File

Settings are saved to:

```
.minecraft/config/afkutility.json
```

Example configuration:

```json
{
  "antiAfk": {
    "autoJump": true,
    "autoSwing": true,
    "autoSneak": false,
    "sneakTime": 2.0,
    "strafeMode": false,
    "spinMode": "OFF",
    "chatMessages": false,
    "messageInterval": 5.0,
    "customMessage": "I am not AFK"
  },
  "autoReconnect": {
    "enabled": true,
    "delay": 5
  },
  "autoLog": {
    "enabled": true,
    "healthThreshold": 4.0,
    "onDamage": false,
    "disableReconnect": true
  },
  "autoEat": {
    "enabled": true,
    "blacklist": ["minecraft:rotten_flesh", "minecraft:spider_eye"]
  }
}
```

## For Developers

### Building

```bash
./gradlew build
```

Output: `build/libs/afk-utility-1.0.0.jar`

### Project Structure

```
src/client/java/dev/afk/utility/
├── AFKUtilityClient.java          # Client entrypoint
├── config/
│   └── ModConfig.java             # Configuration management
├── features/
│   ├── AntiAFK.java               # Anti-AFK implementations
│   ├── AutoReconnect.java         # Reconnection handler
│   ├── AutoLog.java               # Safety disconnect
│   └── AutoEat.java               # Hunger management
├── mixin/client/
│   ├── DisconnectScreenMixin.java # UI integration
│   └── ClientPlayerMixin.java     # Player event hooks
└── screen/
    └── ConfigScreen.java          # Sidebar configuration UI
```

## License

MIT

## Credits

- Fabric API Team
- Minecraft Modding Community