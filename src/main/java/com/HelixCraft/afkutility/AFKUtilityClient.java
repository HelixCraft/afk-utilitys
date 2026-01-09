package com.HelixCraft.afkutility;

import com.HelixCraft.afkutility.config.ConfigManager;
import com.HelixCraft.afkutility.features.AntiAfk;
import com.HelixCraft.afkutility.features.AutoEat;
import com.HelixCraft.afkutility.features.AutoLog;
import com.HelixCraft.afkutility.gui.AfkUtilityScreen;
import net.fabricmc.api.ClientModInitializer;
import org.lwjgl.glfw.GLFW;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.resources.Identifier;
// import com.mojang.blaze3d.platform.InputConstants; // Already imported

public class AFKUtilityClient implements ClientModInitializer {
    private static KeyMapping openConfigKey;

    @Override
    public void onInitializeClient() {
        ConfigManager.load();

        openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.afkutility.open_config",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                new KeyMapping.Category(Identifier.parse("afkutility:general"))));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openConfigKey.consumeClick()) { // wasPressed -> consumeClick
                client.setScreen(new AfkUtilityScreen(client.screen)); // currentScreen -> screen
            }

            AntiAfk.tick(client);
            AutoEat.tick(client);
            AutoLog.tick(client);
        });
    }
}