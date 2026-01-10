package com.HelixCraft.afkutility.compat;

import net.minecraft.client.multiplayer.ClientPacketListener;

public interface NetworkHelper {
    void sendRotationPacket(ClientPacketListener connection, float yRot, float xRot, boolean onGround);
}
