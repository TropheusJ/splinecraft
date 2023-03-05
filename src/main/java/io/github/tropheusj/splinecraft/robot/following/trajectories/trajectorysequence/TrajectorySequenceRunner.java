package io.github.tropheusj.splinecraft.robot.following.trajectories.trajectorysequence;

import com.acmerobotics.roadrunner.control.PIDCoefficients;
import com.acmerobotics.roadrunner.control.PIDFController;
import com.acmerobotics.roadrunner.drive.DriveSignal;
import com.acmerobotics.roadrunner.followers.TrajectoryFollower;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.profile.MotionState;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryMarker;
import com.acmerobotics.roadrunner.util.NanoClock;

import io.github.tropheusj.splinecraft.robot.following.trajectories.trajectorysequence.sequencesegment.SequenceSegment;
import io.github.tropheusj.splinecraft.robot.following.trajectories.trajectorysequence.sequencesegment.TrajectorySegment;
import io.github.tropheusj.splinecraft.robot.following.trajectories.trajectorysequence.sequencesegment.TurnSegment;
import io.github.tropheusj.splinecraft.robot.following.trajectories.trajectorysequence.sequencesegment.WaitSegment;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrajectorySequenceRunner {
    private final TrajectoryFollower follower;

    private final NanoClock clock;

    private TrajectorySequence currentTrajectorySequence;
    private double currentSegmentStartTime;
    private int currentSegmentIndex;
    private int lastSegmentIndex;

    List<TrajectoryMarker> remainingMarkers = new ArrayList<>();

    public TrajectorySequenceRunner(TrajectoryFollower follower) {
        this.follower = follower;

        clock = NanoClock.system();
    }

    public void followTrajectorySequenceAsync(TrajectorySequence trajectorySequence) {
        currentTrajectorySequence = trajectorySequence;
        currentSegmentStartTime = clock.seconds();
        currentSegmentIndex = 0;
        lastSegmentIndex = -1;
    }

    public @Nullable Pose2d update(Pose2d poseEstimate) {
        Pose2d targetPose = null;

        SequenceSegment currentSegment = null;

        if (currentTrajectorySequence != null) {
            if (currentSegmentIndex >= currentTrajectorySequence.size()) {
                for (TrajectoryMarker marker : remainingMarkers) {
                    marker.getCallback().onMarkerReached();
                }

                remainingMarkers.clear();

                currentTrajectorySequence = null;
            }

            if (currentTrajectorySequence == null)
                return null;

            double now = clock.seconds();
            boolean isNewTransition = currentSegmentIndex != lastSegmentIndex;

            currentSegment = currentTrajectorySequence.get(currentSegmentIndex);

            if (isNewTransition) {
                currentSegmentStartTime = now;
                lastSegmentIndex = currentSegmentIndex;

                for (TrajectoryMarker marker : remainingMarkers) {
                    marker.getCallback().onMarkerReached();
                }

                remainingMarkers.clear();

                remainingMarkers.addAll(currentSegment.getMarkers());
                Collections.sort(remainingMarkers, (t1, t2) -> Double.compare(t1.getTime(), t2.getTime()));
            }

            double deltaTime = now - currentSegmentStartTime;

            if (currentSegment instanceof TrajectorySegment) {
                Trajectory currentTrajectory = ((TrajectorySegment) currentSegment).getTrajectory();

                if (isNewTransition)
                    follower.followTrajectory(currentTrajectory);

                if (!follower.isFollowing()) {
                    currentSegmentIndex++;
                } else {
                    follower.update(poseEstimate, null);
                }

                targetPose = currentTrajectory.get(deltaTime);
            } else if (currentSegment instanceof TurnSegment) {
                MotionState targetState = ((TurnSegment) currentSegment).getMotionProfile().get(deltaTime);

                Pose2d startPose = currentSegment.getStartPose();
                targetPose = startPose.copy(startPose.getX(), startPose.getY(), targetState.getX());

                if (deltaTime >= currentSegment.getDuration()) {
                    currentSegmentIndex++;
                }
            } else if (currentSegment instanceof WaitSegment) {
                targetPose = currentSegment.getStartPose();

                if (deltaTime >= currentSegment.getDuration()) {
                    currentSegmentIndex++;
                }
            }

            while (remainingMarkers.size() > 0 && deltaTime > remainingMarkers.get(0).getTime()) {
                remainingMarkers.get(0).getCallback().onMarkerReached();
                remainingMarkers.remove(0);
            }
        }

        return targetPose;
    }

    public boolean isBusy() {
        return currentTrajectorySequence != null;
    }
}
