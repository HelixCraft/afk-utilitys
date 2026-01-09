package com.HelixCraft.afkutility.features;

import com.HelixCraft.afkutility.config.ConfigManager;
import com.HelixCraft.afkutility.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class AutoLog {
    private static int lastHealth = -1;

    public static void tick(Minecraft client) {
        if (client.player == null || client.level == null || ConfigManager.get() == null)
            return;

        ModConfig.AutoLog config = ConfigManager.get().autoLog;
        if (!config.enabled)
            return;

        int currentHealth = (int) client.player.getHealth();
        // Check Health Threshold
        if (currentHealth < config.healthThreshold) {
            triggerDisconnect(client, "Health too low! (" + currentHealth + " < " + config.healthThreshold + ")");
            return;
        }

        if (config.onDamage) {
            if (client.player.hurtTime == 9) {
                triggerDisconnect(client, "Taking Damage!");
                return;
            }
        }
    }

    private static void triggerDisconnect(Minecraft client, String reason) {
        if (ConfigManager.get() == null)
            return;
        ModConfig.AutoLog config = ConfigManager.get().autoLog;

        if (config.toggleAutoReconnect) {
            ConfigManager.get().autoReconnect.enabled = false;
        }

        if (config.toggleAutoLog) {
            config.enabled = false;
        }
        ConfigManager.save();

        if (client.getConnection() != null) {
            // client.getConnection().getConnection() returns the
            // net.minecraft.network.Connection
            client.getConnection().getConnection().disconnect(Component.literal("[Auto Log] " + reason));
        }
    }
}
