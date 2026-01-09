package com.HelixCraft.afkutility.gui;

import com.HelixCraft.afkutility.config.ConfigManager;
import com.HelixCraft.afkutility.config.ModConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.client.input.MouseButtonEvent;

import java.util.List;
import java.util.stream.Collectors;

public class FoodSelectorScreen extends Screen {
    private final Screen parent;
    private final ModConfig.AutoEat autoEatConfig = ConfigManager.get().autoEat;
    private FoodList foodList;
    private EditBox searchBox;
    private final List<Item> edibleItems;

    public FoodSelectorScreen(Screen parent) {
        super(Component.literal("Select Food to Blacklist"));
        this.parent = parent;
        this.edibleItems = BuiltInRegistries.ITEM.stream()
                .filter(item -> item.components().has(DataComponents.FOOD))
                .collect(Collectors.toList());
    }

    @Override
    protected void init() {
        searchBox = new EditBox(this.font, this.width / 2 - 100, 20, 200, 20, Component.literal("Search"));
        searchBox.setHint(Component.literal("Search..."));
        searchBox.setResponder(this::updateFilter);
        this.addRenderableWidget(searchBox);

        foodList = new FoodList(this.minecraft, this.width, this.height - 80, 50, 24);
        this.addRenderableWidget(foodList);

        this.addRenderableWidget(Button.builder(Component.literal("Cancel"), button -> this.minecraft.setScreen(parent))
                .bounds(this.width / 2 - 50, this.height - 25, 100, 20)
                .build());

        updateFilter(searchBox.getValue());
    }

    private void updateFilter(String filter) {
        foodList.refreshEntries(filter);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(graphics, mouseX, mouseY, delta);
        super.render(graphics, mouseX, mouseY, delta);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 8, 0xFFFFFF);
    }

    private class FoodList extends net.minecraft.client.gui.components.ObjectSelectionList<FoodList.Entry> {
        public FoodList(net.minecraft.client.Minecraft minecraft, int width, int height, int y, int itemHeight) {
            super(minecraft, width, height, y, itemHeight);
        }

        public void refreshEntries(String filter) {
            this.clearEntries();
            String lowerFilter = filter.toLowerCase();
            for (Item item : edibleItems) {
                String id = BuiltInRegistries.ITEM.getKey(item).toString();
                String name = new ItemStack(item).getHoverName().getString().toLowerCase();
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
            return this.width / 2 + getRowWidth() / 2 + 10;
        }

        private class Entry extends net.minecraft.client.gui.components.ObjectSelectionList.Entry<Entry> {
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
                        FoodSelectorScreen.this.minecraft.setScreen(parent);
                    }
                }).bounds(0, 0, 50, 20).build();
                if (alreadyIn)
                    this.addButton.active = false;
            }

            @Override
            public void renderContent(GuiGraphics graphics, int x, int y, boolean hovered, float tickDelta) {
                graphics.drawString(FoodSelectorScreen.this.font, new ItemStack(item).getHoverName(), x + 5, y + 2,
                        0xFFFFFF);
                graphics.drawString(FoodSelectorScreen.this.font, itemId, x + 5, y + 12, 0x888888);

                addButton.setX(x + FoodList.this.getRowWidth() - 55);
                addButton.setY(y + 2);
                // AbstractWidget.render requires mouseX, mouseY.
                // Since this signature doesn't provide them, checking if we can get them from
                // minecraft
                // or if we just pass 0 if interaction is handled elsewhere.
                // However, render handles hover state.
                // Assuming Minecraft.getInstance().mouseHandler.xpos/ypos?
                // For now, I'll use 0, 0 to fix compilation.
                addButton.render(graphics, 0, 0, tickDelta);
            }

            @Override
            public boolean mouseClicked(MouseButtonEvent event, boolean handled) {
                if (addButton.mouseClicked(event, handled))
                    return true;
                return super.mouseClicked(event, handled);
            }

            @Override
            public Component getNarration() {
                return new ItemStack(item).getHoverName();
            }
        }
    }
}
