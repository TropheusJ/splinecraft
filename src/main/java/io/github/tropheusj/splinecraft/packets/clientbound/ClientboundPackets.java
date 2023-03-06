package io.github.tropheusj.splinecraft.packets.clientbound;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientboundPackets {
	public static void init() {
		ClientPlayNetworking.registerGlobalReceiver(ClientboundPlaybackPacket.ID, ClientboundPlaybackPacket::handle);
	}
}
