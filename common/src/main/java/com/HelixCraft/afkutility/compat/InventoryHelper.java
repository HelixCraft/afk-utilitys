package com.HelixCraft.afkutility.compat;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public interface InventoryHelper {
    int getSelectedSlot(Inventory inventory);

    void setSelectedSlot(Inventory inventory, int slot);

    ItemStack getItem(Inventory inventory, int slot);
}
