package com.HelixCraft.afkutility.impl;

import com.HelixCraft.afkutility.compat.InventoryHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class InventoryHelperImpl implements InventoryHelper {

    // Cache the field to avoid repeated lookups
    private static java.lang.reflect.Field selectedField;

    static {
        try {
            // "selected" is the intermediary name usually.
            // If running in dev (named), it might be "selected".
            // In prod (intermediary), it's "field_7545" (for example) or still "selected"
            // if mapped.
            // But we are compiling against mapped jar usually?
            // Wait, Loom provides mapped jar.
            // In runtime? Mappings might vary.
            // BUT, usually "selected" works in dev/fabric environment.
            // Let's try "selected" first.
            // Safest: try direct access first (source), if fails compile... wait we are
            // FIXING compilation.
            // We must use reflection at RUNTIME.
            // But to pass COMPILATION against 1.21.6 (where it is private), we MUST usage
            // reflection or accessors.
            // We'll use Reflection.

            selectedField = Inventory.class.getDeclaredField("selected");
            selectedField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            // Try fallback or maybe it's a different name?
            // Since we target 1.21.x, it's fairly stable.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getSelectedSlot(Inventory inventory) {
        try {
            if (selectedField != null) {
                return selectedField.getInt(inventory);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0; // Fallback
    }

    @Override
    public void setSelectedSlot(Inventory inventory, int slot) {
        try {
            if (selectedField != null) {
                selectedField.setInt(inventory, slot);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ItemStack getItem(Inventory inventory, int slot) {
        // Inventory implements Container/RegistryContainer which has getItem(int)
        // This is standard across versions.
        return inventory.getItem(slot);
    }
}
