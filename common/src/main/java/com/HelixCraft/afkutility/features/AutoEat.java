package com.HelixCraft.afkutility.features;

import com.HelixCraft.afkutility.config.ConfigManager;
import com.HelixCraft.afkutility.config.ModConfig;
import com.HelixCraft.afkutility.compat.VersionCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class AutoEat {
    private static boolean eating = false;
    private static int slot = -1;
    private static int prevSlot = -1;

    public static void tick(Minecraft client) {
        if (client.player == null || client.level == null || ConfigManager.get() == null)
            return;

        ModConfig.AutoEat config = ConfigManager.get().autoEat;

        if (!config.enabled) {
            if (eating)
                stopEating(client);
            return;
        }

        if (eating) {
            // Stop eating if we shouldn't eat anymore
            if (!shouldEat(client, config)) {
                stopEating(client);
                return;
            }

            // Check if current item is still food
            if (VersionCompat.INVENTORY.getItem(client.player.getInventory(), slot != -1 && slot != 40 ? slot : 0)
                    .get(DataComponents.FOOD) == null && slot != 40) {
                stopEating(client);
                return;
            }

            // Continue eating
            if (!client.player.isUsingItem()) {
                client.options.keyUse.setDown(true);
                if (client.gameMode != null) {
                    client.gameMode.useItem(client.player,
                            slot == 40 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
                }
            }
            return;
        }

        if (shouldEat(client, config)) {
            startEating(client, config);
        }
    }

    private static boolean shouldEat(Minecraft client, ModConfig.AutoEat config) {
        if (client.player.getFoodData().getFoodLevel() >= 20)
            return false;

        slot = findSlot(client, config);
        return slot != -1;
    }

    private static void startEating(Minecraft client, ModConfig.AutoEat config) {
        prevSlot = VersionCompat.INVENTORY.getSelectedSlot(client.player.getInventory());
        changeSlot(client, slot);
        client.options.keyUse.setDown(true);
        if (client.gameMode != null) {
            client.gameMode.useItem(client.player, slot == 40 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        }
        eating = true;
    }

    private static void stopEating(Minecraft client) {
        if (prevSlot != -1 && slot != 40) {
            VersionCompat.INVENTORY.setSelectedSlot(client.player.getInventory(), prevSlot);
        }
        client.options.keyUse.setDown(false);
        eating = false;
        prevSlot = -1;
        slot = -1;
    }

    private static void changeSlot(Minecraft client, int newSlot) {
        if (newSlot == 40)
            return;
        VersionCompat.INVENTORY.setSelectedSlot(client.player.getInventory(), newSlot);
        slot = newSlot;
    }

    private static int findSlot(Minecraft client, ModConfig.AutoEat config) {
        int bestSlot = -1;
        int bestHunger = -1;

        int missingHunger = 20 - client.player.getFoodData().getFoodLevel();

        for (int i = 0; i < 9; i++) {
            ItemStack stack = VersionCompat.INVENTORY.getItem(client.player.getInventory(), i);
            Item item = stack.getItem();
            FoodProperties food = stack.get(DataComponents.FOOD);

            if (food != null) {
                if (isBlacklisted(item, config))
                    continue;

                int hunger = food.nutrition();
                // Strict check: Only eat if missing hunger is exactly or more than the food
                // provides
                // "es ist erst dan, wenn genau so viel hunger nicht da ist, wie das jeweilge
                // essen auffüllen würde"
                if (missingHunger >= hunger) {
                    if (hunger > bestHunger) {
                        bestSlot = i;
                        bestHunger = hunger;
                    }
                }
            }
        }

        ItemStack offhandStack = client.player.getOffhandItem();
        Item offhandItem = offhandStack.getItem();
        FoodProperties offhandFood = offhandStack.get(DataComponents.FOOD);

        if (offhandFood != null) {
            if (!isBlacklisted(offhandItem, config)) {
                int hunger = offhandFood.nutrition();
                if (missingHunger >= hunger) {
                    if (hunger > bestHunger) {
                        return 40;
                    }
                }
            }
        }

        return bestSlot;
    }

    private static boolean isBlacklisted(Item item, ModConfig.AutoEat config) {
        String id = BuiltInRegistries.ITEM.getKey(item).toString();
        return config.blacklist.contains(id);
    }
}
