# AFK Utility Mod Documentation

An advanced utility mod for Minecraft Fabric 1.21.4 designed to manage AFK (Away From Keyboard) states effectively.

---

## 🛠 Features in Detail

### 1. Anti-AFK System
The Anti-AFK system prevents server isolation or kicks by simulating organic player activity.

*   **Auto-Jump**: Periodically forces the player to jump. This is randomized slightly to appear more natural to anti-cheat systems.
*   **Auto-Swing**: Periodically triggers a left-click (attack) action. Useful for automated farming or maintaining activity that requires block/entity interaction.
*   **Auto-Sneak**: Alternates between sneaking and standing. You can configure the **Sneak Time** (how long you stay crouched) and the interval between sneaks.
*   **Strafe Mode**: Moves the player left and right automatically to prevent coordinate-based AFK detection.
*   **Spin Mode (Two Types)**:
    *   **Client Mode**: Rotates your actual camera view. You will see your screen spinning.
    *   **Server Mode**: Sends rotation packets to the server while keeping your local camera completely static. Other players see you spinning, but your screen remains steady.
*   **Chat Messages**: Sends periodic messages to the chat.
    *   **Interval Slider**: Range from 0.1 minutes (6 seconds) to 30 minutes.
    *   **Custom**: Send a specific "I am not AFK" message.

---

### 2. Auto Reconnect
Never lose your spot on a server during a restart or a temporary connection puff.

*   **Retention**: The mod automatically remembers the last server address and server data you were connected to.
*   **Smart Countdown**: Upon being disconnected, a countdown timer appears on the disconnect screen.
*   **Toggleable**: You can quickly enable or disable Auto Reconnect directly from the disconnect screen via a dedicated button.
*   **Delay**: Configurable delay (in seconds) to wait before the reconnection attempt.

---

### 3. Auto Log (Safety Disconnect)
Protects your character from death while you are away.

*   **HP Threshold**: Automatically disconnects you from the server if your health drops below a certain value (e.g., 2 hearts).
*   **On Damage**: Can be configured to disconnect immediately upon taking *any* damage, regardless of current health.
*   **Safety Lock**: Optionally disables "Auto Reconnect" if an Auto Log is triggered. This prevents the mod from putting you back into a dangerous situation repeatedly.

---

### 4. Auto Eat
Keeps your hunger bar full and health regenerating.

*   **Intelligent Timing**: Only eats when the hunger points provided by the food in your hand won't go to waste (strictly calculating hunger vs. food value).
*   **Blacklist Support**: Allows you to blacklist specific items (like Rotten Flesh or Spider Eyes) so you don't accidentally poison yourself while AFK.

---

## ⚙️ Configuration & UI

The mod features a custom sidebar-based configuration screen, designed for ease of use:

*   **Sidebar Access**: Navigate through the four main categories (Anti-AFK, Auto-Eat, Reconnect, Auto-Log).
*   **Dynamic Sliders**: Intuitive sliders for all intervals, including high-precision floats for message intervals.
*   **Persistence**: All settings are saved automatically to `config/afkutility.json` in your .minecraft folder.

---

## ⌨️ Default Keybinds
*   **Open Menu**: `K` (Default)
*   *Can be rebound in the standard Minecraft Controls menu.*
