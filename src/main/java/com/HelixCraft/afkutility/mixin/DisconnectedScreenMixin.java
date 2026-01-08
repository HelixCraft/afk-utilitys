package com.HelixCraft.afkutility.mixin;

import com.HelixCraft.afkutility.config.ConfigManager;
import com.HelixCraft.afkutility.features.AutoReconnect;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DisconnectedScreen.class)
public abstract class DisconnectedScreenMixin extends Screen {

    @Shadow
    @Final
    private LinearLayout layout;
    @Unique
    private Button reconnectBtn;
    @Unique
    private double time;

    protected DisconnectedScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/LinearLayout;arrangeElements()V", shift = At.Shift.BEFORE))
    private void addButtons(CallbackInfo ci) {
        if (AutoReconnect.lastServerData != null) {
            this.time = ConfigManager.get().autoReconnect.delay * 20;

            // Reconnect Button
            reconnectBtn = Button.builder(Component.literal(getText()), button -> tryConnecting())
                    .build();
            layout.addChild(reconnectBtn);

            // Toggle Button
            layout.addChild(
                    Button.builder(Component.literal("Toggle Auto Reconnect"), button -> {
                        ConfigManager.get().autoReconnect.enabled = !ConfigManager.get().autoReconnect.enabled;
                        ConfigManager.save();
                        if (ConfigManager.get().autoReconnect.enabled) {
                            this.time = ConfigManager.get().autoReconnect.delay * 20;
                        }
                        updateButtonText();
                    }).build());
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (AutoReconnect.lastServerData != null && ConfigManager.get().autoReconnect.enabled) {
            if (time <= 0) {
                tryConnecting();
            } else {
                time--;
                updateButtonText();
            }
        }
    }

    @Unique
    private void updateButtonText() {
        if (reconnectBtn != null) {
            reconnectBtn.setMessage(Component.literal(getText()));
        }
    }

    @Unique
    private String getText() {
        String base = "Reconnect";
        if (ConfigManager.get().autoReconnect.enabled) {
            base += String.format(" (%.1f)", time / 20.0);
        }
        return base;
    }

    @Unique
    private void tryConnecting() {
        ServerData serverData = AutoReconnect.lastServerData;
        ServerAddress serverAddress = AutoReconnect.lastServerAddress;
        if (serverAddress == null && serverData != null)
            serverAddress = ServerAddress.parseString(serverData.ip);

        if (serverData != null) {
            ConnectScreen.startConnecting(new TitleScreen(), this.minecraft, serverAddress, serverData, false, null);
        }
    }
}
