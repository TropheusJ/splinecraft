package io.github.tropheusj.splinecraft.robot.following.trajectories;

import com.acmerobotics.roadrunner.drive.DriveSignal;
import com.acmerobotics.roadrunner.followers.TrajectoryFollower;
import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DummyTrajectoryFollower extends TrajectoryFollower {
	@NotNull
	@Override
	public Pose2d getLastError() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void setLastError(@NotNull Pose2d pose2d) {
		throw new UnsupportedOperationException();
	}

	@NotNull
	@Override
	protected DriveSignal internalUpdate(@NotNull Pose2d pose2d, @Nullable Pose2d pose2d1) {
		return null; // it's fiiiiiiiiine
	}
}
