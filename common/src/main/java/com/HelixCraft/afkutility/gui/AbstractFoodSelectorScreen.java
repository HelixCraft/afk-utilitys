package com.HelixCraft.afkutility.gui;

import com.HelixCraft.afkutility.config.ConfigManager;
import com.HelixCraft.afkutility.config.ModConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractFoodSelectorScreen extends Screen {
    protected final Screen parent;
    protected final ModConfig.AutoEat autoEatConfig = ConfigManager.get().autoEat;
    protected net.minecraft.client.gui.components.ObjectSelectionList<?> foodList;
    private EditBox searchBox;
    protected final List<Item> edibleItems;

    public AbstractFoodSelectorScreen(Screen parent) {
        super(Component.literal("Select Food to Blacklist"));
        this.parent = parent;
        this.edibleItems = BuiltInRegistries.ITEM.stream()
                .filter(item -> item.components().has(DataComponents.FOOD))
                .collect(Collectors.toList());
    }

    protected abstract net.minecraft.client.gui.components.ObjectSelectionList<?> createFoodList(int width, int height,
            int y, int itemHeight);

    protected abstract void refreshList(String filter);

    @Override
    protected void init() {
        searchBox = new EditBox(this.font, this.width / 2 - 100, 20, 200, 20, Component.literal("Search"));
        searchBox.setHint(Component.literal("Search..."));
        searchBox.setResponder(this::updateFilter);
        this.addRenderableWidget(searchBox);

        foodList = createFoodList(this.width, this.height - 80, 50, 24);
        this.addRenderableWidget(foodList);

        this.addRenderableWidget(Button.builder(Component.literal("Cancel"), button -> this.minecraft.setScreen(parent))
                .bounds(this.width / 2 - 50, this.height - 25, 100, 20)
                .build());

        updateFilter(searchBox.getValue());
    }

    private void updateFilter(String filter) {
        refreshList(filter);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(graphics, mouseX, mouseY, delta);
        super.render(graphics, mouseX, mouseY, delta);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 8, 0xFFFFFF);
    }
}
