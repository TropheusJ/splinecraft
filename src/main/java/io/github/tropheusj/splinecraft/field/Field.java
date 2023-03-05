package io.github.tropheusj.splinecraft.field;

import io.github.tropheusj.splinecraft.field.controller.FieldControllerBlockEntity;
import io.github.tropheusj.splinecraft.robot.configurator.RobotConfiguratorBlockEntity;
import io.github.tropheusj.splinecraft.robot.entity.RobotEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public final class Field {

	public FieldControllerBlockEntity controller;
	public RobotConfiguratorBlockEntity configurator;
	public RobotEntity robot;

	public final Direction forward, back, left, right;

	public final BlockPos corner;
	public final Vec3 origin;

	public Field(FieldControllerBlockEntity controller) {
		this.controller = controller;

		this.forward = controller.getFacing();
		this.back = forward.getOpposite();
		this.left = forward.getCounterClockWise();
		this.right = forward.getClockWise();

		this.corner = controller.getBlockPos()
				.relative(forward)
				.relative(left, 3);
		this.origin = Vec3.atBottomCenterOf(controller.getBlockPos())
				.relative(forward, 3.5)
				.relative(left, 0.5);
	}

	public BlockPos toAbsolute(BlockPos pos) {
		return corner.offset(pos);
	}

	public BlockPos toLocal(BlockPos pos) {
		return pos.subtract(corner);
	}

	public Vec3 toInches(Vec3 blocks) {
		Vec3 relative = origin.subtract(blocks);
		// each block is 1 tile, 24 inches
		return relative.multiply(24, 24, 24);
	}

	public Vec3 toBlocks(Vec3 inches) {
		// undo toInches
		Vec3 blocks = inches.multiply(1 / 24f, 1 / 24f, 1 / 24f);
		return origin.add(blocks);
	}
}
