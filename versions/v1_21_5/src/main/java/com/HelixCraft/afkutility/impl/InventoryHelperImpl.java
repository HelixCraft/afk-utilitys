package com.HelixCraft.afkutility.impl;

import com.HelixCraft.afkutility.compat.InventoryHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class InventoryHelperImpl implements InventoryHelper {
    @Override
    public int getSelectedSlot(Inventory inventory) {
        return inventory.getSelectedSlot();
    }

    @Override
    public void setSelectedSlot(Inventory inventory, int slot) {
        inventory.setSelectedSlot(slot);
    }

    @Override
    public ItemStack getItem(Inventory inventory, int slot) {
        return inventory.getItem(slot);
    }
}
