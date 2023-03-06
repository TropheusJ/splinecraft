package io.github.tropheusj.splinecraft.packets.serverbound;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ServerboundPackets {
	public static void init() {
		ServerPlayNetworking.registerGlobalReceiver(ServerboundResetFieldPacket.ID, ServerboundResetFieldPacket::handle);
		ServerPlayNetworking.registerGlobalReceiver(ServerboundApplyRobotConfigPacket.ID, ServerboundApplyRobotConfigPacket::handle);
		ServerPlayNetworking.registerGlobalReceiver(ServerboundPlaybackPacket.ID, ServerboundPlaybackPacket::handle);
	}
}
