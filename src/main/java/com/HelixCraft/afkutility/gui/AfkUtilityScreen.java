package com.HelixCraft.afkutility.gui;

import com.HelixCraft.afkutility.config.ConfigManager;
import com.HelixCraft.afkutility.config.ModConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class AfkUtilityScreen extends Screen {
        private final Screen parent;
        private Tab currentTab = Tab.ANTI_AFK;
        private final ModConfig.AntiAfk antiAfkConfig = ConfigManager.get().antiAfk;
        private final ModConfig.AutoEat autoEatConfig = ConfigManager.get().autoEat;
        private final ModConfig.AutoReconnect autoReconnectConfig = ConfigManager.get().autoReconnect;
        private final ModConfig.AutoLog autoLogConfig = ConfigManager.get().autoLog;

        private EditBox customMessageBox;

        public AfkUtilityScreen(Screen parent) {
                super(Component.literal("AFK Utility Config"));
                this.parent = parent;
        }

        @Override
        protected void init() {
                // Updated Sidebar Width: "A bit wider" -> 100 to 120 or 130
                int sidebarWidth = 130;

                // Tab Buttons (Sidebar)
                int tabY = 40;
                int btnHeight = 20;
                int btnGap = 5;
                int btnWidth = sidebarWidth - 20; // Padding

                this.addRenderableWidget(
                                Button.builder(Component.literal("Anti-AFK"), button -> switchTab(Tab.ANTI_AFK))
                                                .bounds(10, tabY, btnWidth, btnHeight)
                                                .build());
                tabY += btnHeight + btnGap;

                this.addRenderableWidget(
                                Button.builder(Component.literal("Auto-Eat"), button -> switchTab(Tab.AUTO_EAT))
                                                .bounds(10, tabY, btnWidth, btnHeight)
                                                .build());
                tabY += btnHeight + btnGap;

                this.addRenderableWidget(
                                Button.builder(Component.literal("Auto Reconnect"),
                                                button -> switchTab(Tab.AUTO_RECONNECT))
                                                .bounds(10, tabY, btnWidth, btnHeight)
                                                .build());
                tabY += btnHeight + btnGap;

                this.addRenderableWidget(
                                Button.builder(Component.literal("Auto Log"), button -> switchTab(Tab.AUTO_LOG))
                                                .bounds(10, tabY, btnWidth, btnHeight)
                                                .build());

                // Content Area init
                int contentX = sidebarWidth + 20;
                switch (currentTab) {
                        case ANTI_AFK:
                                initAntiAfk(contentX);
                                break;
                        case AUTO_EAT:
                                initAutoEat(contentX);
                                break;
                        case AUTO_RECONNECT:
                                initAutoReconnect(contentX);
                                break;
                        case AUTO_LOG:
                                initAutoLog(contentX);
                                break;
                }
        }

        private void switchTab(Tab tab) {
                if (customMessageBox != null) {
                        antiAfkConfig.customMessage = customMessageBox.getValue();
                }
                this.currentTab = tab;
                this.clearWidgets();
                this.init();
        }

        private void initAntiAfk(int xBase) {
                int y = 40;
                int configWidth = 150;
                int sliderWidth = 100;
                int gap = 5;

                // Jump + Interval
                this.addRenderableWidget(CycleButton.onOffBuilder(antiAfkConfig.jump)
                                .create(xBase, y, configWidth, 20, Component.literal("Jump"),
                                                (button, value) -> antiAfkConfig.jump = value));

                this.addRenderableWidget(
                                new IntSlider(xBase + configWidth + gap, y, sliderWidth, 20, "Interval: ", "s", 1, 60,
                                                antiAfkConfig.jumpInterval, val -> antiAfkConfig.jumpInterval = val));
                y += 24;

                // Swing + Interval
                this.addRenderableWidget(CycleButton.onOffBuilder(antiAfkConfig.swing)
                                .create(xBase, y, configWidth, 20, Component.literal("Swing Hand"),
                                                (button, value) -> antiAfkConfig.swing = value));

                this.addRenderableWidget(
                                new IntSlider(xBase + configWidth + gap, y, sliderWidth, 20, "Interval: ", "s", 1, 60,
                                                antiAfkConfig.swingInterval, val -> antiAfkConfig.swingInterval = val));
                y += 24;

                // Sneak + Interval
                this.addRenderableWidget(CycleButton.onOffBuilder(antiAfkConfig.sneak)
                                .create(xBase, y, configWidth, 20, Component.literal("Sneak"),
                                                (button, value) -> antiAfkConfig.sneak = value));

                this.addRenderableWidget(
                                new IntSlider(xBase + configWidth + gap, y, sliderWidth, 20, "Interval: ", "s", 1, 60,
                                                antiAfkConfig.sneakInterval, val -> antiAfkConfig.sneakInterval = val));
                y += 24;

                // Strafe
                this.addRenderableWidget(CycleButton.onOffBuilder(antiAfkConfig.strafe)
                                .create(xBase, y, configWidth, 20, Component.literal("Strafe"),
                                                (button, value) -> antiAfkConfig.strafe = value));
                y += 24;

                // Spin
                this.addRenderableWidget(CycleButton.onOffBuilder(antiAfkConfig.spin)
                                .create(xBase, y, configWidth, 20, Component.literal("Spin"),
                                                (button, value) -> antiAfkConfig.spin = value));

                this.addRenderableWidget(
                                new IntSlider(xBase + configWidth + gap, y, sliderWidth, 20, "Speed: ", "", 1, 30,
                                                antiAfkConfig.spinSpeed, val -> antiAfkConfig.spinSpeed = val));
                y += 24;

                this.addRenderableWidget(
                                CycleButton.<ModConfig.AntiAfk.SpinMode>builder(mode -> Component.literal(mode.name()))
                                                .withValues(ModConfig.AntiAfk.SpinMode.values())
                                                .withInitialValue(antiAfkConfig.spinMode)
                                                .create(xBase, y, configWidth, 20, Component.literal("Spin Mode"),
                                                                (button, value) -> antiAfkConfig.spinMode = value));
                y += 24;

                // Messages
                this.addRenderableWidget(CycleButton.onOffBuilder(antiAfkConfig.sendMessages)
                                .create(xBase, y, configWidth, 20, Component.literal("Send Chat Messages"),
                                                (button, value) -> antiAfkConfig.sendMessages = value));

                // Interval Slider (0.1 - 30.0 mins)
                this.addRenderableWidget(new FloatSlider(xBase + configWidth + gap, y, sliderWidth, 20, "Every: ",
                                "min",
                                0.1f, 30.0f,
                                (float) antiAfkConfig.messageInterval, val -> antiAfkConfig.messageInterval = val));
                y += 24;

                // Custom Message Field
                customMessageBox = new EditBox(this.font, xBase, y, configWidth + sliderWidth + gap, 20,
                                Component.literal("Custom Message"));
                customMessageBox.setValue(antiAfkConfig.customMessage);
                customMessageBox.setMaxLength(256);
                this.addRenderableWidget(customMessageBox);
                y += 24;
        }

        private void initAutoEat(int xBase) {
                int y = 50;
                int width = 200;

                this.addRenderableWidget(
                                Button.builder(Component.literal("Open Blacklist File (Coming Soon)"), button -> {
                                })
                                                .bounds(xBase, y, width, 20)
                                                .tooltip(net.minecraft.client.gui.components.Tooltip
                                                                .create(Component.literal(
                                                                                "Edit blacklist in config file manually for now")))
                                                .build());
        }

        private void initAutoReconnect(int xBase) {
                int y = 50;
                int width = 200;

                this.addRenderableWidget(CycleButton.onOffBuilder(autoReconnectConfig.enabled)
                                .create(xBase, y, width, 20, Component.literal("Enabled"),
                                                (button, value) -> autoReconnectConfig.enabled = value));
                y += 24;

                this.addRenderableWidget(
                                new IntSlider(xBase, y, width, 20, "Delay: ", "s", 1, 60, autoReconnectConfig.delay,
                                                val -> autoReconnectConfig.delay = val));
        }

        private void initAutoLog(int xBase) {
                int y = 50;
                int width = 200;

                this.addRenderableWidget(CycleButton.onOffBuilder(autoLogConfig.enabled)
                                .create(xBase, y, width, 20, Component.literal("Enabled"),
                                                (button, value) -> autoLogConfig.enabled = value));
                y += 24;

                this.addRenderableWidget(new IntSlider(xBase, y, width, 20, "Health < ", "", 0, 20,
                                autoLogConfig.healthThreshold, val -> autoLogConfig.healthThreshold = val));
                y += 24;

                this.addRenderableWidget(CycleButton.onOffBuilder(autoLogConfig.onDamage)
                                .create(xBase, y, width, 20, Component.literal("On Damage"),
                                                (button, value) -> autoLogConfig.onDamage = value));
                y += 24;

                this.addRenderableWidget(CycleButton.onOffBuilder(autoLogConfig.toggleAutoReconnect)
                                .create(xBase, y, width, 20, Component.literal("Disable Auto Reconnect"),
                                                (button, value) -> autoLogConfig.toggleAutoReconnect = value));
                y += 24;

                this.addRenderableWidget(CycleButton.onOffBuilder(autoLogConfig.toggleAutoLog)
                                .create(xBase, y, width, 20, Component.literal("Disable Auto Log"),
                                                (button, value) -> autoLogConfig.toggleAutoLog = value));
        }

        @Override
        public void onClose() {
                if (customMessageBox != null) {
                        antiAfkConfig.customMessage = customMessageBox.getValue();
                }
                ConfigManager.save();
                if (parent != null) {
                        if (this.minecraft != null)
                                this.minecraft.setScreen(parent);
                } else {
                        super.onClose();
                }
        }

        @Override
        public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
                this.renderBackground(context, mouseX, mouseY, delta);
                int sbWidth = 130; // Match sidebarWidth

                // Sidebar Background
                context.fill(0, 0, sbWidth, height, 0x80000000);
                // Content Background
                context.fill(sbWidth, 0, width, height, 0x60000000);

                super.render(context, mouseX, mouseY, delta);

                context.drawCenteredString(this.font, this.title, sbWidth + (width - sbWidth) / 2, 10, 0xFFFFFF);

                if (currentTab == Tab.AUTO_EAT) {
                        context.drawCenteredString(this.font, "Auto-Eat Active: Missing Hunger >= Food Value",
                                        sbWidth + (width - sbWidth) / 2, 80, 0xAAAAAA);
                }
        }

        private enum Tab {
                ANTI_AFK,
                AUTO_EAT,
                AUTO_RECONNECT,
                AUTO_LOG
        }

        private static class IntSlider extends AbstractSliderButton {
                private final int min;
                private final int max;
                private final Consumer<Integer> onChange;
                private final String prefix;
                private final String suffix;

                public IntSlider(int x, int y, int width, int height, String prefix, String suffix, int min, int max,
                                int currentValue, Consumer<Integer> onChange) {
                        super(x, y, width, height, Component.empty(), 0);
                        this.min = min;
                        this.max = max;
                        this.prefix = prefix;
                        this.suffix = suffix;
                        this.onChange = onChange;
                        this.value = (double) (currentValue - min) / (max - min);
                        this.updateMessage();
                }

                @Override
                protected void updateMessage() {
                        int val = (int) (min + (max - min) * this.value);
                        this.setMessage(Component.literal(prefix + val + suffix));
                }

                @Override
                protected void applyValue() {
                        int val = (int) (min + (max - min) * this.value);
                        onChange.accept(val);
                }
        }

        private static class FloatSlider extends AbstractSliderButton {
                private final float min;
                private final float max;
                private final Consumer<Float> onChange;
                private final String prefix;
                private final String suffix;

                public FloatSlider(int x, int y, int width, int height, String prefix, String suffix, float min,
                                float max,
                                float currentValue, Consumer<Float> onChange) {
                        super(x, y, width, height, Component.empty(), 0);
                        this.min = min;
                        this.max = max;
                        this.prefix = prefix;
                        this.suffix = suffix;
                        this.onChange = onChange;
                        // Clamp value
                        currentValue = Math.max(min, Math.min(max, currentValue));
                        this.value = (double) (currentValue - min) / (max - min);
                        this.updateMessage();
                }

                @Override
                protected void updateMessage() {
                        float val = (float) (min + (max - min) * this.value);
                        // Round to 1 decimal place
                        val = Math.round(val * 10) / 10.0f;
                        this.setMessage(Component.literal(prefix + val + suffix));
                }

                @Override
                protected void applyValue() {
                        float val = (float) (min + (max - min) * this.value);
                        val = Math.round(val * 10) / 10.0f;
                        onChange.accept(val);
                }
        }
}
