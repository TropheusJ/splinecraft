package io.github.tropheusj.splinecraft.packets.clientbound;

import io.github.tropheusj.splinecraft.SplineCraft;
import io.github.tropheusj.splinecraft.field.controller.FieldControllerBlockEntity;
import io.github.tropheusj.splinecraft.robot.PlaybackStatus;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ClientboundPlaybackPacket {
	public static final ResourceLocation ID = SplineCraft.id("playback");

	public static void send(FieldControllerBlockEntity be, PlaybackStatus status) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeBlockPos(be.getBlockPos());
		buf.writeEnum(status);
		PlayerLookup.tracking(be).forEach(player -> ServerPlayNetworking.send(player, ID, buf));
	}

	@Environment(EnvType.CLIENT)
	public static void handle(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
		BlockPos controllerPos = buf.readBlockPos();
		PlaybackStatus status = buf.readEnum(PlaybackStatus.class);
		client.execute(() -> {
			ClientLevel level = client.level;
			if (level.getBlockEntity(controllerPos) instanceof FieldControllerBlockEntity controller) {
				controller.setRobotStatus(status);
			}
		});
	}
}
