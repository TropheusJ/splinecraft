package io.github.tropheusj.splinecraft.robot.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

public class RobotEntity extends Entity {
	public RobotEntity(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	protected void defineSynchedData() {
	}

	@Override
	protected float getEyeHeight(Pose pose, EntityDimensions dimensions) {
		return 0.5f;
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
	}

	@Override
	@NotNull
	public Packet<?> getAddEntityPacket() {
		return new ClientboundAddEntityPacket(this);
	}
}
