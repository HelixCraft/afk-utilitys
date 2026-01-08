package com.HelixCraft.afkutility.features;

import com.HelixCraft.afkutility.config.ConfigManager;
import com.HelixCraft.afkutility.config.ModConfig;
import net.minecraft.client.player.LocalPlayer;

public class RotationSpoofer {
    private static float originalYaw;
    private static float originalPitch;
    private static boolean isSpoofing = false;

    public static void spoof(LocalPlayer player) {
        ModConfig.AntiAfk config = ConfigManager.get().antiAfk;
        if (config.spin && config.spinMode == ModConfig.AntiAfk.SpinMode.Server) {
            originalYaw = player.getYRot();
            originalPitch = player.getXRot();

            float targetYaw = AntiAfk.getLastYaw();
            float targetPitch = (float) config.pitch;

            player.setYRot(targetYaw);
            player.setXRot(targetPitch);
            isSpoofing = true;
        }
    }

    public static void restore(LocalPlayer player) {
        if (isSpoofing) {
            player.setYRot(originalYaw);
            player.setXRot(originalPitch);
            isSpoofing = false;
        }
    }
}
