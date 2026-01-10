package com.HelixCraft.afkutility.compat;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import com.mojang.blaze3d.platform.InputConstants;
import com.HelixCraft.afkutility.config.ModConfig;

public interface ScreenHelper {
    Screen createAfkUtilityScreen(Screen parent);

    Screen createFoodSelectorScreen(Screen parent);

    KeyMapping createKeyMapping(String name, InputConstants.Type type, int keyCode, String category);

    CycleButton<ModConfig.AntiAfk.SpinMode> createSpinModeButton(int x, int y, int width, int height);
}
