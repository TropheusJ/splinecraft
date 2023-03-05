package io.github.tropheusj.splinecraft.robot.entity;

import com.acmerobotics.roadrunner.geometry.Pose2d;

import com.acmerobotics.roadrunner.geometry.Vector2d;

import io.github.tropheusj.splinecraft.Content;
import io.github.tropheusj.splinecraft.field.Field;
import io.github.tropheusj.splinecraft.packets.clientbound.ClientboundSpawnRobotPacket;
import io.github.tropheusj.splinecraft.robot.config.RobotConfig;
import io.github.tropheusj.splinecraft.robot.following.trajectories.RobotEntityMecanumDrive;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;

public class RobotEntity extends Entity {
	public RobotConfig config;

	private Field field;
	private RobotEntityMecanumDrive drive;

	public RobotEntity(EntityType<?> type, Level level) {
		this(level, null, new RobotConfig());
	}

	public RobotEntity(Level level, Field field, RobotConfig config) {
		super(Content.ROBOT_ENTITY, level);
		this.config = config;
		setField(field);
	}

	public void setField(Field field) {
		this.field = field;
		drive = field == null ? null : new RobotEntityMecanumDrive(this);
	}

	public Field getField() {
		return field;
	}

	public static RobotEntity spawn(Level level, Field field, RobotConfig config, Vec3 pos, float yRot) {
		RobotEntity robot = new RobotEntity(level, field, config);
		robot.moveTo(pos.x, pos.y, pos.z, yRot, 0);
		level.addFreshEntity(robot);
		return robot;
	}

	@Override
	public void tick() {
		super.tick();
		if (drive == null || level.isClientSide)
			return;
		if (tickCount == 40) {
			drive.followTrajectorySequence(
					drive.trajectorySequenceBuilder(new Pose2d(0, 0, Math.PI / 2))
//							.forward(48)
//							.strafeRight(48)
//							.back(48)
//							.strafeLeft(48)
//							.waitSeconds(2)
							.forward(48)
							.turn(-Math.PI / 2)
							.forward(48)
							.turn(-Math.PI / 2)
							.forward(48)
							.turn(-Math.PI / 2)
							.forward(48)
							.turn(-Math.PI / 2)
							.build()
			);
		}
		Pose2d current = currentPose();
		drive.update();
		Pose2d newPose = currentPose();
		Pose2d delta = newPose.minus(current);
		if (Math.abs(delta.getHeading()) > 1e-6)
			System.out.println("delta: " + delta);
	}

	public Pose2d currentPose() {
		Vec3 inches = field.toInches(position());
		return new Pose2d(inches.x, inches.z, Math.toRadians(getYRot()));
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
		return ClientboundSpawnRobotPacket.create(this);
	}
}
