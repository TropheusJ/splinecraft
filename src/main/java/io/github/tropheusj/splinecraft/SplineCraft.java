package io.github.tropheusj.splinecraft;

import io.github.tropheusj.splinecraft.packets.serverbound.ServerboundPackets;
import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SplineCraft implements ModInitializer {
	public static final String ID = "splinecraft";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize() {
		Content.init();
		ServerboundPackets.init();
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(ID, path);
	}
}
