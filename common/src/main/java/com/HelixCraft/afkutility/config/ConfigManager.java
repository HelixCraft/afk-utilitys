package com.HelixCraft.afkutility.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {
    private static ModConfig config;
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("afkutility.json")
            .toFile();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                config = GSON.fromJson(reader, ModConfig.class);
                if (config == null) {
                    config = new ModConfig();
                }
            } catch (IOException e) {
                e.printStackTrace();
                config = new ModConfig();
            }
        } else {
            config = new ModConfig();
            save();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ModConfig get() {
        if (config == null) {
            load();
        }
        return config;
    }
}
