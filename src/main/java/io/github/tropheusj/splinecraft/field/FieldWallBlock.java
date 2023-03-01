package io.github.tropheusj.splinecraft.field;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;

public class FieldWallBlock extends StairBlock {
	public FieldWallBlock(Properties properties) {
		super(Blocks.IRON_BARS.defaultBlockState(), properties);
	}
}
