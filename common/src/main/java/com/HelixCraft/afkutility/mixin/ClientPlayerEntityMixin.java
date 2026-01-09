package com.HelixCraft.afkutility.mixin;

import com.HelixCraft.afkutility.features.RotationSpoofer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class ClientPlayerEntityMixin {

    @Inject(method = "sendPosition", at = @At("HEAD"))
    private void onSendPositionHead(CallbackInfo ci) {
        RotationSpoofer.spoof((LocalPlayer) (Object) this);
    }

    @Inject(method = "sendPosition", at = @At("TAIL"))
    private void onSendPositionTail(CallbackInfo ci) {
        RotationSpoofer.restore((LocalPlayer) (Object) this);
    }
}
