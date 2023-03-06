package io.github.tropheusj.splinecraft.robot;

import com.acmerobotics.roadrunner.geometry.Pose2d;

import com.acmerobotics.roadrunner.geometry.Vector2d;

import io.github.tropheusj.splinecraft.field.Field;
import io.github.tropheusj.splinecraft.robot.config.RobotConfig;
import io.github.tropheusj.splinecraft.robot.following.trajectories.RobotMecanumDrive;
import io.github.tropheusj.splinecraft.robot.following.trajectories.trajectorysequence.TrajectorySequence;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

public class Robot {
	public final Field field;
	public final RobotConfig config;

	// field-relative, but in blocks
	private double x, z;
	private double lastX, lastZ;
	// degrees
	private float yRot;
	private float lastYRot;

	private RobotMecanumDrive drive;

	public Robot(Field field) {
		this.field = field;
		this.config = new RobotConfig();
	}

	public void start(Function<RobotMecanumDrive, TrajectorySequence> sequenceFactory) {
		drive = new RobotMecanumDrive(this);
		drive.followTrajectorySequence(
				drive.trajectorySequenceBuilder(new Pose2d(0, 0, 0))
//						.forward(48)
//						.strafeRight(48)
//						.back(48)
//						.strafeLeft(48)
//						.waitSeconds(1)
//						.forward(48)
//						.turn(-Math.PI / 2)
//						.forward(48)
//						.turn(-Math.PI / 2)
//						.forward(48)
//						.turn(-Math.PI / 2)
//						.forward(48)
//						.turn(-Math.PI / 2)
//						.waitSeconds(1)
						.splineTo(new Vector2d(-48, 48), Math.PI)
						.build()
		);
	}

	public boolean isRunning() {
		return drive.isBusy();
	}

	public void stop() {
		drive = null;
	}

	public void update() {
		this.lastX = this.x;
		this.lastZ = this.z;
		this.lastYRot = this.yRot;
		if (drive != null)
			drive.update();
	}

	public Pose2d currentPose() {
		Vec3 inches = field.toInches(new Vec3(x, 0, z));
		return new Pose2d(inches.x, inches.z, yRot);
	}

	public void setPos(double x, double z, float yRot) {
		this.x = x;
		this.z = z;
		this.yRot = yRot;
	}

	public double getX() {
		return x;
	}

	public double getLastX() {
		return lastX;
	}

	public double getZ() {
		return z;
	}

	public double getLastZ() {
		return lastZ;
	}

	public float getYRot() {
		return yRot;
	}

	public float getLastYRot() {
		return lastYRot;
	}
}
