package io.github.tropheusj.splinecraft.robot.following.trajectories;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.localization.Localizer;

import io.github.tropheusj.splinecraft.robot.entity.RobotEntity;

import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotEntityLocalizer implements Localizer {
	private final RobotEntity robot;

	private Pose2d estimate;

	public RobotEntityLocalizer(RobotEntity robot) {
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
		Vec3 pos = new Vec3(-estimate.getX(), 0, estimate.getY());
		Vec3 blocks = robot.getField().toBlocks(pos);

		float yRot = (float) Math.toDegrees(estimate.getHeading()) - 90;

		robot.moveTo(blocks.x, robot.getY(), blocks.z, -yRot, 0);
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
