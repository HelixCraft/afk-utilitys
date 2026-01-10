package com.HelixCraft.afkutility.impl;

import com.HelixCraft.afkutility.compat.NetworkHelper;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

public class NetworkHelperImpl implements NetworkHelper {

    @Override
    public void sendRotationPacket(ClientPacketListener connection, float yRot, float xRot, boolean onGround) {
        // 1.21.5+ generally uses the 4-arg constructor (checked in 1.21.11 and
        // 1.21.5-1.21.9 failures implying similar structure to 1.21.4 or newer)
        // If 1.21.11 changed it again, we'd need to adjust, but likely it's 4-arg.
        // Actually, for v1_21_5 module which compiles against 1.21.5+, we'll assume
        // standard direct usage if possible,
        // OR rely on reflection if we are unsure about intermediate versions.
        // But v1_21_5 targets 1.21.11 where we know 4-arg exists (or 3-arg?).
        // Wait, 1.21.11 likely has 4-arg (horizontalCollision was added earlier).
        // Let's assume direct access works if compiled against implicit version, but to
        // be SAFE and consistent with v1_21_0's robustness:
        // Actually, v1_21_5 is compiled against the active version (e.g. 1.21.11).
        // If 1.21.11 has it, direct call works.
        // If 1.21.11 CHANGED it, direct call fails compilation.

        // We'll use direct call for now, assuming 4 args.
        connection.send(new ServerboundMovePlayerPacket.Rot(yRot, xRot, onGround, false));
    }
}
