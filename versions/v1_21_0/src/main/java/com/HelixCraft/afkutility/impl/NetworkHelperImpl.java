package com.HelixCraft.afkutility.impl;

import com.HelixCraft.afkutility.compat.NetworkHelper;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

import java.lang.reflect.Constructor;

public class NetworkHelperImpl implements NetworkHelper {

    @Override
    public void sendRotationPacket(ClientPacketListener connection, float yRot, float xRot, boolean onGround) {
        try {
            // Try 4-arg constructor first (1.21.2+)
            try {
                Constructor<ServerboundMovePlayerPacket.Rot> ctor = ServerboundMovePlayerPacket.Rot.class
                        .getConstructor(float.class, float.class, boolean.class, boolean.class);
                connection.send(ctor.newInstance(yRot, xRot, onGround, false));
                return;
            } catch (NoSuchMethodException ignored) {
            }

            // Fallback to 3-arg constructor (1.21 / 1.21.1)
            try {
                Constructor<ServerboundMovePlayerPacket.Rot> ctor = ServerboundMovePlayerPacket.Rot.class
                        .getConstructor(float.class, float.class, boolean.class);
                connection.send(ctor.newInstance(yRot, xRot, onGround));
                return;
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Could not find suitable ServerboundMovePlayerPacket.Rot constructor", e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to send rotation packet via reflection", e);
        }
    }
}
