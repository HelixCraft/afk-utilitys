package com.HelixCraft.afkutility.compat;

public class VersionCompat {
    public static InventoryHelper INVENTORY;

    public static ScreenHelper SCREEN_HELPER;
    public static NetworkHelper NETWORK;

    public static void init(InventoryHelper inventoryHelper, ScreenHelper screenHelper) {
        INVENTORY = inventoryHelper;
        SCREEN_HELPER = screenHelper;
    }
}
