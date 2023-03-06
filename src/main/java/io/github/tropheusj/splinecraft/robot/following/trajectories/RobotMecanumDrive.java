package io.github.tropheusj.splinecraft.robot.following.trajectories;

import com.acmerobotics.roadrunner.drive.MecanumDrive;

import com.acmerobotics.roadrunner.geometry.Pose2d;

import com.acmerobotics.roadrunner.trajectory.Trajectory;

import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;

import com.acmerobotics.roadrunner.trajectory.constraints.AngularVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.MecanumVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.MinVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.ProfileAccelerationConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryAccelerationConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryVelocityConstraint;

import io.github.tropheusj.splinecraft.robot.Robot;
import io.github.tropheusj.splinecraft.robot.config.RobotConfig;

import io.github.tropheusj.splinecraft.robot.following.trajectories.trajectorysequence.TrajectorySequence;
import io.github.tropheusj.splinecraft.robot.following.trajectories.trajectorysequence.TrajectorySequenceBuilder;
import io.github.tropheusj.splinecraft.robot.following.trajectories.trajectorysequence.TrajectorySequenceRunner;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RobotMecanumDrive extends MecanumDrive {
	// reasonable(?) defaults, shouldn't actually matter really
	public static final double KV = 1.0 / (1 * 2 * Math.PI * 2 / 60.0);
	public static final double KA = 0;
	public static final double KS = 0;
	public static final double LATERAL_MULTIPLIER = 1;

	private final Robot robot;
	private final TrajectorySequenceRunner runner;

	private final TrajectoryVelocityConstraint velConstraint;
	private final TrajectoryAccelerationConstraint accelConstraint;

	public RobotMecanumDrive(Robot robot) {
		super(KV, KA, KS, robot.config.trackWidth.value, LATERAL_MULTIPLIER);
		this.robot = robot;
		setLocalizer(new RobotLocalizer(robot));
		this.runner = new TrajectorySequenceRunner(new DummyTrajectoryFollower());

		RobotConfig config = robot.config;
		this.accelConstraint = new ProfileAccelerationConstraint(config.maxAccel.value);
		this.velConstraint = new MinVelocityConstraint(List.of(
				new AngularVelocityConstraint(config.maxAngVel.value),
				new MecanumVelocityConstraint(config.maxVel.value, config.trackWidth.value)
		));
	}

	public void update() {
		Pose2d pose = runner.update(getPoseEstimate());
		if (pose != null)
			setPoseEstimate(pose);
	}

	public boolean isBusy() {
		return runner.isBusy();
	}

	public TrajectoryBuilder trajectoryBuilder(Pose2d startPose) {
		return new TrajectoryBuilder(startPose, velConstraint, accelConstraint);
	}

	public TrajectoryBuilder trajectoryBuilder(Pose2d startPose, boolean reversed) {
		return new TrajectoryBuilder(startPose, reversed, velConstraint, accelConstraint);
	}

	public TrajectoryBuilder trajectoryBuilder(Pose2d startPose, double startHeading) {
		return new TrajectoryBuilder(startPose, startHeading, velConstraint, accelConstraint);
	}

	public TrajectorySequenceBuilder trajectorySequenceBuilder(Pose2d startPose) {
		return new TrajectorySequenceBuilder(
				startPose,
				velConstraint, accelConstraint,
				robot.config.maxAngVel.value,
				robot.config.maxAngAccel.value
		);
	}


	public void turn(double angle) {
		runner.followTrajectorySequenceAsync(
				trajectorySequenceBuilder(getPoseEstimate())
						.turn(angle)
						.build()
		);
	}

	public void followTrajectory(Trajectory trajectory) {
		runner.followTrajectorySequenceAsync(
				trajectorySequenceBuilder(trajectory.start())
						.addTrajectory(trajectory)
						.build()
		);
	}

	public void followTrajectorySequence(TrajectorySequence trajectorySequence) {
		runner.followTrajectorySequenceAsync(trajectorySequence);
	}

	@Override
	protected double getRawExternalHeading() {
		return Math.toRadians(robot.getYRot());
	}

	@NotNull
	@Override
	public List<Double> getWheelPositions() {
		throw new UnsupportedOperationException("getWheelPositions should not be called, only used in default localizer");
	}

	@Override
	public void setMotorPowers(double frontLeft, double backLeft, double backRight, double frontRight) {
		throw new UnsupportedOperationException("setMotorPowers should not be called, only called by SampleMecanumDrive");
	}
}
