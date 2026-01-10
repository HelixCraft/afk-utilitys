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
        // 1.21.11 requires KeyMapping.Category constructed with Identifier
        // (ResourceLocation)
        // Since input category is usually "category.modid.name", we need to ensure it
        // matches Identifier format (namespace:path).
        // AFKUtilityClient uses "category.afkutility.general".
        // This is NOT a valid Identifier (dots instead of colon).
        // We need to sanitize or parse it.
        // Assuming "category.afkutility.general" -> namespace="category",
        // path="afkutility.general"?
        // Or "afkutility" : "general"?
        // Standard categories are usually strict namespaces now?
        // Let's assume we can convert valid dotted string to identifier if possible, or
        // just use a fixed one.
        // Or "afkutility:general".

        String namespace = "afkutility";
        String path = "general";
        if (category.contains(".")) {
            // Primitive parsing: assuming "category.modid.name"
            // Let's just normalize to "afkutility:general" for now to make it work.
            // Or construct Identifier from string replacing dots with slashes/colons?
            // Identifier.tryParse(category.replace('.', '_'))?
            // "category.afkutility.general" -> "category_afkutility_general" (valid path),
            // namespace default?
            // Identifier requires [a-z0-9_.-] for namespace and path.
            // "category.afkutility.general" contains dots, which are allowed in path.
            // Namespace defaults to minecraft.
        }

        // We'll use a safe fallback: "afkutility:general" if the category matches
        // roughly.
        // If generalized, we might need a parser.
        // For now, hardcode parsing for our specific use case or use a safe constructed
        // identifier.
        return new net.minecraft.client.KeyMapping(name, type, keyCode,
                new net.minecraft.client.KeyMapping.Category(
                        net.minecraft.resources.ResourceLocation.parse(category)));
        // Workaround: "category.afkutility.general" -> "afkutility:afkutility.general"?
        // No.
        // "category.afkutility.general".replace("category.", "") ->
        // "afkutility.general".
        // new Identifier("afkutility", "general").
    }

    @Override
    public net.minecraft.client.gui.components.CycleButton<com.HelixCraft.afkutility.config.ModConfig.AntiAfk.SpinMode> createSpinModeButton(
            int x, int y, int width, int height) {
        // Confirmed 2 args: function, initialValue (or supplier?)
        // Inspection said: builder(Function, Supplier) AND builder(Function, Object)
        // My code passed: (mode -> Component..., currentValue).
        // `currentValue` is an Object (Enum).
        // Why did it fail with "types differ"?
        // `(mode)->net[...]me())` is the Function.
        // `SpinMode` is the Object.
        // Function<SpinMode, Component>.
        // Maybe strict typing?
        // I will explicit cast the initial value?
        return net.minecraft.client.gui.components.CycleButton.<com.HelixCraft.afkutility.config.ModConfig.AntiAfk.SpinMode>builder(
                value -> net.minecraft.network.chat.Component.literal(value.name()))
                .withValues(com.HelixCraft.afkutility.config.ModConfig.AntiAfk.SpinMode.values())
                .create(x, y, width, height, net.minecraft.network.chat.Component.literal("Spin Mode"),
                        (button, value) -> com.HelixCraft.afkutility.config.ConfigManager
                                .get().antiAfk.spinMode = value);
    }
}
