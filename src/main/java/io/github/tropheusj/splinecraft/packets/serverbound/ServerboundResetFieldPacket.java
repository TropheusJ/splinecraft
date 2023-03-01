package io.github.tropheusj.splinecraft.packets.serverbound;

import io.github.tropheusj.splinecraft.SplineCraft;
import io.github.tropheusj.splinecraft.field.controller.FieldControllerBlockEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class ServerboundResetFieldPacket {
	public static final ResourceLocation ID = SplineCraft.id("reset_field");

	public static void send(FieldControllerBlockEntity be) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeBlockPos(be.getBlockPos());
		ClientPlayNetworking.send(ID, buf);
	}

	public static void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler,
							  FriendlyByteBuf buf, PacketSender responseSender) {
		BlockPos pos = buf.readBlockPos();
		server.execute(() -> {
			if (player.level.getBlockEntity(pos) instanceof FieldControllerBlockEntity controller) {
				controller.resetField();
			} else {
				SplineCraft.LOGGER.error("No controller at " + pos);
			}
		});
	}
}
