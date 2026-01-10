package com.HelixCraft.afkutility.gui;

import com.HelixCraft.afkutility.gui.AbstractFoodSelectorScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

public class FoodSelectorScreenImpl extends AbstractFoodSelectorScreen {

    public FoodSelectorScreenImpl(Screen parent) {
        super(parent);
    }

    @Override
    protected ObjectSelectionList<?> createFoodList(int width, int height, int y, int itemHeight) {
        return new FoodList(this.minecraft, width, height, y, itemHeight);
    }

    @Override
    protected void refreshList(String filter) {
        if (foodList instanceof FoodList) {
            ((FoodList) foodList).refreshEntries(filter);
        }
    }

    private class FoodList extends ObjectSelectionList<FoodList.Entry> {
        public FoodList(net.minecraft.client.Minecraft minecraft, int width, int height, int y, int itemHeight) {
            super(minecraft, width, height, y, itemHeight);
        }

        public void refreshEntries(String filter) {
            this.clearEntries();
            String lowerFilter = filter.toLowerCase();
            for (Item item : edibleItems) {
                String id = BuiltInRegistries.ITEM.getKey(item).toString();
                String name = item.getName().getString().toLowerCase();
                if (id.contains(lowerFilter) || name.contains(lowerFilter)) {
                    this.addEntry(new Entry(item));
                }
            }
        }

        @Override
        public int getRowWidth() {
            return Math.min(400, this.width - 50);
        }

        protected int getScrollbarPosition() {
            return this.getX() + this.width / 2 + getRowWidth() / 2 + 10;
        }

        private class Entry extends ObjectSelectionList.Entry<Entry> {
            private final Item item;
            private final String itemId;
            private final Button addButton;

            public Entry(Item item) {
                this.item = item;
                this.itemId = BuiltInRegistries.ITEM.getKey(item).toString();
                boolean alreadyIn = autoEatConfig.blacklist.contains(itemId);
                this.addButton = Button.builder(Component.literal(alreadyIn ? "Added" : "Add"), button -> {
                    if (!autoEatConfig.blacklist.contains(itemId)) {
                        autoEatConfig.blacklist.add(itemId);
                        FoodSelectorScreenImpl.this.minecraft.setScreen(FoodSelectorScreenImpl.this.parent); // How to
                                                                                                             // get
                                                                                                             // parent?
                                                                                                             // parent
                                                                                                             // is
                                                                                                             // private
                                                                                                             // in
                                                                                                             // AbstractFoodSelectorScreen.
                        // I need parent to be protected in AbstractFoodSelectorScreen.
                    }
                }).bounds(0, 0, 50, 20).build();
                if (alreadyIn)
                    this.addButton.active = false;
            }

            @Override
            public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight,
                    int mouseX, int mouseY, boolean hovered, float tickDelta) {
                graphics.drawString(FoodSelectorScreenImpl.this.font, item.getName(), x + 5, y + 2, 0xFFFFFF);
                graphics.drawString(FoodSelectorScreenImpl.this.font, itemId, x + 5, y + 12, 0x888888);

                addButton.setX(x + entryWidth - 55);
                addButton.setY(y + 2);
                addButton.render(graphics, mouseX, mouseY, tickDelta);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (addButton.mouseClicked(mouseX, mouseY, button))
                    return true;
                return super.mouseClicked(mouseX, mouseY, button);
            }

            @Override
            public Component getNarration() {
                return item.getName();
            }
        }
    }
}
