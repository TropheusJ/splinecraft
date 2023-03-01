package io.github.tropheusj.splinecraft.packets.serverbound;

import io.github.tropheusj.splinecraft.SplineCraft;
import io.github.tropheusj.splinecraft.robot.config.RobotConfig;
import io.github.tropheusj.splinecraft.robot.configurator.RobotConfiguratorBlockEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class ServerboundApplyRobotConfigPacket {
	public static final ResourceLocation ID = SplineCraft.id("apply_robot_config");

	public static void send(RobotConfiguratorBlockEntity be, RobotConfig config) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeBlockPos(be.getBlockPos());
		buf.writeNbt(config.write());
		ClientPlayNetworking.send(ID, buf);
	}

	public static void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler,
							  FriendlyByteBuf buf, PacketSender responseSender) {
		BlockPos pos = buf.readBlockPos();
		CompoundTag configData = buf.readNbt();
		server.execute(() -> {
			if (player.level.getBlockEntity(pos) instanceof RobotConfiguratorBlockEntity configurator) {
				configurator.readRobotConfig(configData);
			}
		});
	}
}
