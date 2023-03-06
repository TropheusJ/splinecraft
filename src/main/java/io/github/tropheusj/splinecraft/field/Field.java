package io.github.tropheusj.splinecraft.field;

import io.github.tropheusj.splinecraft.robot.Robot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class Field {

	public final BlockPos corner;
	public final Vec3 origin;
	public final Robot robot;

	public Field(BlockPos corner, Direction forward, Direction right) {
		this.corner = corner;
		this.origin = Vec3.atBottomCenterOf(corner)
				.relative(forward, 3.5)
				.relative(right, 3.5);
		this.robot = new Robot(this);
	}

	public BlockPos toAbsolute(BlockPos pos) {
		return corner.offset(pos);
	}

	public BlockPos toLocal(BlockPos pos) {
		return pos.subtract(corner);
	}

	public Vec3 toInches(Vec3 blocks) {
		// each block is 1 tile, 24 inches
		return blocks.multiply(24, 24, 24);
	}

	public Vec3 toBlocks(Vec3 inches) {
		return inches.multiply(1 / 24f, 1 / 24f, 1 / 24f);
	}
}
