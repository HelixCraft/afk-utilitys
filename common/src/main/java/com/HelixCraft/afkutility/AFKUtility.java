package com.HelixCraft.afkutility;

import com.HelixCraft.afkutility.compat.InventoryHelper;
import com.HelixCraft.afkutility.compat.NetworkHelper;
import com.HelixCraft.afkutility.compat.ScreenHelper;
import com.HelixCraft.afkutility.compat.VersionCompat;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AFKUtility implements ModInitializer {
	public static final String MOD_ID = "afkutility";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// Initialize Version Compatibility Layer
		try {
			Class<?> inventoryHelperClass = Class.forName("com.HelixCraft.afkutility.impl.InventoryHelperImpl");
			VersionCompat.INVENTORY = (InventoryHelper) inventoryHelperClass.getDeclaredConstructor().newInstance();

			Class<?> screenHelperClass = Class.forName("com.HelixCraft.afkutility.impl.ScreenHelperImpl");
			VersionCompat.SCREEN_HELPER = (ScreenHelper) screenHelperClass.getDeclaredConstructor().newInstance();

			Class<?> networkHelperClass = Class.forName("com.HelixCraft.afkutility.impl.NetworkHelperImpl");
			VersionCompat.NETWORK = (NetworkHelper) networkHelperClass.getDeclaredConstructor().newInstance();

			LOGGER.info("Initialized Version Compatibility Layer with: " + inventoryHelperClass.getName() + ", "
					+ screenHelperClass.getName() + " and " + networkHelperClass.getName());
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize version compatibility layer", e);
		}

		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
	}
}