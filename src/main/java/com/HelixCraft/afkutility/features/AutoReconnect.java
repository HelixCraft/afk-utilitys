package com.HelixCraft.afkutility.features;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;

public class AutoReconnect {
    public static ServerData lastServerData;
    public static ServerAddress lastServerAddress;

    public static void setLastServer(ServerData serverData, ServerAddress serverAddress) {
        lastServerData = serverData;
        lastServerAddress = serverAddress;
    }
}
