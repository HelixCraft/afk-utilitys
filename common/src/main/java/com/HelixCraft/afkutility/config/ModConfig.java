package com.HelixCraft.afkutility.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModConfig {
    private static final File FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "afkutility.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public AntiAfk antiAfk = new AntiAfk();
    public AutoEat autoEat = new AutoEat();
    public AutoReconnect autoReconnect = new AutoReconnect();
    public AutoLog autoLog = new AutoLog();

    public static class AntiAfk {
        public boolean enabled = false;
        // Actions
        public boolean jump = false;
        public boolean swing = false;
        public boolean sneak = false;
        public boolean strafe = false;
        public boolean spin = false; // New Spin Action

        // Intervals (Seconds)
        public int jumpInterval = 5;
        public int swingInterval = 10;
        public int sneakInterval = 15;

        // Settings
        public int sneakTime = 20; // How long to hold sneak (ticks)
        public int spinSpeed = 5; // Degrees per tick
        public SpinMode spinMode = SpinMode.Client; // Client (Normal), Server (Packet spoof only)
        public double pitch = 0.0; // Pitch during spin

        // Messages
        public boolean sendMessages = false;
        public double messageInterval = 1.0; // Minutes (0.1 - 30.0)
        public String customMessage = "I am not AFK!";

        public enum SpinMode {
            Client,
            Server
        }
    }

    public static class AutoEat {
        public boolean enabled = false;
        public List<String> blacklist = new ArrayList<>(List.of(
                "minecraft:pufferfish", // Gift, Übelkeit und Hunger-Effekt
                "minecraft:rotten_flesh", // 80% Chance auf Hunger-Effekt
                "minecraft:spider_eye", // Verursacht Gift-Schaden
                "minecraft:poisonous_potato", // Verursacht Gift-Schaden
                "minecraft:raw_chicken", // 30% Chance auf Hunger-Effekt
                "minecraft:enchanted_golden_apple", // Zu wertvoll (für Kämpfe aufsparen)
                "minecraft:golden_apple", // Zu wertvoll (Notfälle/Villager)
                "minecraft:chorus_fruit", // Zufällige Teleportation
                "minecraft:suspicious_stew", // Unberechenbare negative Effekte
                "minecraft:honey_bottle" // Zur Gift-Heilung aufsparen
        ));
    }

    public static class AutoReconnect {
        public boolean enabled = false;
        public int delay = 5; // Seconds
    }

    public static class AutoLog {
        public boolean enabled = false;
        public int healthThreshold = 4; // 2 hearts
        public boolean onDamage = false;
        public boolean toggleAutoReconnect = true; // Disable Auto Reconnect if Auto Log triggered
        public boolean toggleAutoLog = true; // Disable Auto Log after trigger (prevent loop if logging back in unsafe)
    }
}
