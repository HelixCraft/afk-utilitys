package com.HelixCraft.afkutility.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Inventory.class)
public interface InventoryAccessor {
    @Accessor("items")
    NonNullList<ItemStack> getItems();

    @Accessor("selected")
    int getSelected();

    @Accessor("selected")
    void setSelected(int selected);
}
