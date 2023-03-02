package io.github.tropheusj.splinecraft.field.floor;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class FieldFloorBlock extends Block {
	// one field block is 1 tile, 6x6 grid
	public static final IntegerProperty ROW = IntegerProperty.create("row", 0, 5);
	public static final IntegerProperty COLUMN = IntegerProperty.create("column", 0, 5);
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	public FieldFloorBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(ROW, COLUMN, FACING));
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		Direction newFacing = rotation.rotate(state.getValue(FACING));
		return state.setValue(FACING, newFacing);
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		Direction newFacing = mirror.mirror(state.getValue(FACING));
		return state.setValue(FACING, newFacing);
	}
}
