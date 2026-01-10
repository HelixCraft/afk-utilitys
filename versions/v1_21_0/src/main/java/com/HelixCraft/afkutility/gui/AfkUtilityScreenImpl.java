package com.HelixCraft.afkutility.gui;

import com.HelixCraft.afkutility.gui.AbstractAfkUtilityScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class AfkUtilityScreenImpl extends AbstractAfkUtilityScreen {

    public AfkUtilityScreenImpl(Screen parent) {
        super(parent);
    }

    @Override
    protected ObjectSelectionList<?> createBlacklistList(int width, int height, int y, int itemHeight) {
        return new BlacklistList(this.minecraft, width, height, y, itemHeight);
    }

    private class BlacklistList extends ObjectSelectionList<BlacklistList.Entry> {
        public BlacklistList(net.minecraft.client.Minecraft minecraft, int width, int height, int y, int itemHeight) {
            super(minecraft, width, height, y, itemHeight);
            for (String item : autoEatConfig.blacklist) {
                this.addEntry(new Entry(item));
            }
        }

        @Override
        public int getRowWidth() {
            return this.width - 20;
        }

        private class Entry extends ObjectSelectionList.Entry<Entry> {
            private final String itemName;
            private final Button removeButton;

            public Entry(String itemName) {
                this.itemName = itemName;
                this.removeButton = Button.builder(Component.literal("X"), button -> {
                    autoEatConfig.blacklist.remove(itemName);
                    AfkUtilityScreenImpl.this.clearWidgets();
                    AfkUtilityScreenImpl.this.init();
                }).bounds(0, 0, 20, 20).build();
            }

            @Override
            public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight,
                    int mouseX, int mouseY, boolean hovered, float tickDelta) {
                graphics.drawString(AfkUtilityScreenImpl.this.font, itemName, x + 5, y + 5, 0xFFFFFF);
                removeButton.setX(x + entryWidth - 25);
                removeButton.setY(y);
                removeButton.render(graphics, mouseX, mouseY, tickDelta);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (removeButton.mouseClicked(mouseX, mouseY, button))
                    return true;
                return super.mouseClicked(mouseX, mouseY, button);
            }

            @Override
            public Component getNarration() {
                return Component.literal(itemName);
            }
        }
    }
}
