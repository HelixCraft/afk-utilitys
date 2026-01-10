package com.HelixCraft.afkutility.impl;

import com.HelixCraft.afkutility.compat.ScreenHelper;
import com.HelixCraft.afkutility.gui.AfkUtilityScreenImpl;
import com.HelixCraft.afkutility.gui.FoodSelectorScreenImpl;
import net.minecraft.client.gui.screens.Screen;

public class ScreenHelperImpl implements ScreenHelper {
    @Override
    public Screen createAfkUtilityScreen(Screen parent) {
        return new AfkUtilityScreenImpl(parent);
    }

    @Override
    public Screen createFoodSelectorScreen(Screen parent) {
        return new FoodSelectorScreenImpl(parent);
    }

    @Override
    public net.minecraft.client.KeyMapping createKeyMapping(String name,
            com.mojang.blaze3d.platform.InputConstants.Type type, int keyCode, String category) {
        return new net.minecraft.client.KeyMapping(name, type, keyCode, category);
    }

    @Override
    public net.minecraft.client.gui.components.CycleButton<com.HelixCraft.afkutility.config.ModConfig.AntiAfk.SpinMode> createSpinModeButton(
            int x, int y, int width, int height) {
        return net.minecraft.client.gui.components.CycleButton.<com.HelixCraft.afkutility.config.ModConfig.AntiAfk.SpinMode>builder(
                mode -> net.minecraft.network.chat.Component.literal(mode.name()))
                .withValues(com.HelixCraft.afkutility.config.ModConfig.AntiAfk.SpinMode.values())
                .withInitialValue(com.HelixCraft.afkutility.config.ConfigManager.get().antiAfk.spinMode)
                .create(x, y, width, height, net.minecraft.network.chat.Component.literal("Spin Mode"),
                        (button, value) -> com.HelixCraft.afkutility.config.ConfigManager
                                .get().antiAfk.spinMode = value);
    }
}
