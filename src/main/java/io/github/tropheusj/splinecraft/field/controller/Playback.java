package io.github.tropheusj.splinecraft.field.controller;

import io.github.tropheusj.splinecraft.robot.entity.RobotEntity;

public record Playback(FieldControllerBlockEntity be, RobotEntity robot) {
	public void tick() {
		if (isInvalid())
			return;

	}

	public boolean isInvalid() {
		return be.isRemoved() || robot.isRemoved();
	}
}
