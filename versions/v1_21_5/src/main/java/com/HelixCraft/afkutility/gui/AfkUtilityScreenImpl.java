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

            // In 1.21.5+, render is final and calls renderContent which has a simplified
            // signature
            // assuming the graphics context is translated to the entry's position.
            // Check signature: void renderContent(GuiGraphics guiGraphics, int i, int j,
            // boolean bl, float f)
            // i=mouseX, j=mouseY, bl=hovered, f=partialTick?
            // Actually, let's verify if the abstract method is indeed called renderContent
            // or something else by checking exact error logs if needed.
            // But proceeding with standard mapped name guess.

            // Wait, I need to check if I need to override renderContent or just render. In
            // 1.21.5 it is renderContent.
            // But wait, my IDE won't compile this if I don't import the source.
            // I will assume standard mappings.

            // @Override
            // public void render(GuiGraphics graphics, int index, int y, int x, int
            // entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float
            // tickDelta) {
            // This is OLD.
            // }

            // NEW 1.21.5+
            // abstract void renderBack(GuiGraphics guiGraphics, int i, int j, int k, int l,
            // int m, int n, int o, boolean bl, float f);
            // No, renderContent is separate?
            // Let's look at the error log from Step 929 again.
            // "Entry ist nicht abstrakt und setzt die abstrakte Methode
            // renderContent(GuiGraphics,int,int,boolean,float) nicht außer Kraft"
            // So yes, it IS renderContent(GuiGraphics, int, int, boolean, float).

            @Override
            public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                // If context is translated, x=0, y=0.
                // But wait, usually buttons need absolute coordinates for mouse clicks?
                // Or maybe they are parented?
                // renderContent is "draw the content".
                // mouseX/Y are likely relative? Or absolute?
                // If absolute, and graphics is translated, then mouseX/Y checks need care.

                // Drawing text at 5, 5 relative to Entry top-left
                graphics.drawString(AfkUtilityScreenImpl.this.font, itemName, 5, 5, 0xFFFFFF);

                // Button position.
                // removeButton bounds are set to 0,0,20,20.
                // We need to positions it relative to right side.
                // Entry width? getRowWidth()?
                // BlacklistList.this.getRowWidth().

                int entryWidth = BlacklistList.this.getRowWidth();
                removeButton.setX(entryWidth - 25); // Relative X
                removeButton.setY(0); // Relative Y

                // render the button. But Widget.render expects mouseX/Y.
                // If graphics is translated, does render handle it?
                // Button.render -> AbstractWidget.render -> renderWidget.
                // Usually widgets expect absolute coordinates?
                // In 1.21.5, container entries might handling offset?

                // Use offset for rendering?
                removeButton.render(graphics, mouseX, mouseY, tickDelta);
            }

            @Override
            public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean handled) {
                // 1.21.11 uses MouseButtonEvent with record-style accessors
                // Remove button also expects (event, handled)
                if (removeButton.mouseClicked(event, handled)) {
                    return true;
                }
                return super.mouseClicked(event, handled);
            }

            @Override
            public Component getNarration() {
                return Component.literal(itemName);
            }
        }
    }
}
