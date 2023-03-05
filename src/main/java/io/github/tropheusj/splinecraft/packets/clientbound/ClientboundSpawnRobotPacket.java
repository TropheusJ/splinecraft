package io.github.tropheusj.splinecraft.packets.clientbound;

import io.github.tropheusj.splinecraft.SplineCraft;
import io.github.tropheusj.splinecraft.field.Field;
import io.github.tropheusj.splinecraft.field.controller.FieldControllerBlockEntity;
import io.github.tropheusj.splinecraft.robot.config.RobotConfig;
import io.github.tropheusj.splinecraft.robot.configurator.RobotConfiguratorBlockEntity;
import io.github.tropheusj.splinecraft.robot.entity.RobotEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceLocation;

public class ClientboundSpawnRobotPacket {
	public static final ResourceLocation ID = SplineCraft.id("apply_robot_config");

	public static Packet<?> create(RobotEntity robot) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		new ClientboundAddEntityPacket(robot).write(buf);
		buf.writeBlockPos(robot.getField().controller.getBlockPos());
		return ServerPlayNetworking.createS2CPacket(ID, buf);
	}

	@Environment(EnvType.CLIENT)
	public static void handle(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
		ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket(buf);
		BlockPos controllerPos = buf.readBlockPos();
		client.execute(() -> {
			packet.handle(handler);
			int entityId = packet.getId();
			ClientLevel level = client.level;

			if (level.getBlockEntity(controllerPos) instanceof FieldControllerBlockEntity controller
			&& level.getEntity(entityId) instanceof RobotEntity robot) {
				Field field = controller.field;
				robot.setField(field);
				field.robot = robot;
			}
		});
	}
}
