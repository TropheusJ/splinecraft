package io.github.tropheusj.splinecraft.robot.following.trajectories;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.localization.Localizer;

import io.github.tropheusj.splinecraft.robot.Robot;

import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotLocalizer implements Localizer {
	private final Robot robot;

	private Pose2d estimate;

	public RobotLocalizer(Robot robot) {
		this.robot = robot;
		update();
	}

	@NotNull
	@Override
	public Pose2d getPoseEstimate() {
		return estimate;
	}

	@Override
	public void setPoseEstimate(@NotNull Pose2d estimate) {
		this.estimate = estimate;
		Vec3 pos = new Vec3(estimate.getY(), 0, -estimate.getX());
		Vec3 blocks = robot.field.toBlocks(pos);

		float yRot = -(float) Math.toDegrees(estimate.getHeading());

		robot.setPos(blocks.x, blocks.z, yRot);
	}

	@Nullable
	@Override
	public Pose2d getPoseVelocity() {
		return null;
	}

	@Override
	public void update() {
		this.estimate = robot.currentPose();
	}
}
