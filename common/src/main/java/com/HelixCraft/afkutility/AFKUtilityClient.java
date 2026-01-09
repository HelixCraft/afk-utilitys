package com.HelixCraft.afkutility;

import com.HelixCraft.afkutility.config.ConfigManager;
import com.HelixCraft.afkutility.features.AntiAfk;
import com.HelixCraft.afkutility.features.AutoEat;
import com.HelixCraft.afkutility.features.AutoLog;
import com.HelixCraft.afkutility.gui.AfkUtilityScreen;
import net.fabricmc.api.ClientModInitializer;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class AFKUtilityClient implements ClientModInitializer {
    private static KeyMapping openConfigKey;

    @Override
    public void onInitializeClient() {
        ConfigManager.load();

        openConfigKey = new KeyMapping(
                "key.afkutility.open_config",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "category.afkutility.general");
        KeyMappingRegistry.register(openConfigKey);

        ClientTickEvent.CLIENT_POST.register(client -> {
            if (openConfigKey.consumeClick()) { // wasPressed -> consumeClick
                client.setScreen(new AfkUtilityScreen(client.screen)); // currentScreen -> screen
            }

            AntiAfk.tick(client);
            AutoEat.tick(client);
            AutoLog.tick(client);
        });
    }
}
