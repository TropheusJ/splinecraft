package io.github.tropheusj.splinecraft.field.wall;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.Table;

import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;

public class FieldWallBlock extends Block {
	public static final VoxelShape SOUTH_PLANE = Block.box(0, 0, 15, 16, 16, 16);
	public static final VoxelShape NORTH_PLANE = Block.box(0, 0, 0, 16, 16, 1);
	public static final VoxelShape EAST_PLANE = Block.box(15, 0, 0, 16, 16, 16);
	public static final VoxelShape WEST_PLANE = Block.box(0, 0, 0, 1, 16, 16);

	public static final Map<Shape, VoxelShape> SHAPES = Map.of(
			Shape.NE, Shapes.or(NORTH_PLANE, EAST_PLANE),
			Shape.E, EAST_PLANE,
			Shape.SE, Shapes.or(SOUTH_PLANE, EAST_PLANE),
			Shape.S, SOUTH_PLANE,
			Shape.SW, Shapes.or(SOUTH_PLANE, WEST_PLANE),
			Shape.W, WEST_PLANE,
			Shape.NW, Shapes.or(NORTH_PLANE, WEST_PLANE),
			Shape.N, NORTH_PLANE
	);

	public static final EnumProperty<Shape> SHAPE = EnumProperty.create("shape", Shape.class);

	public FieldWallBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(SHAPE));
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		Shape shape = state.getValue(SHAPE);
		Shape newShape = switch (rotation) {
			case NONE -> shape;
			case CLOCKWISE_90 -> shape.clockwise();
			case CLOCKWISE_180 -> shape.clockwise().clockwise();
			case COUNTERCLOCKWISE_90 -> shape.clockwise().clockwise().clockwise();
		};
		return state.setValue(SHAPE, newShape);
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		Shape shape = state.getValue(SHAPE);
		Shape newShape = switch (mirror) {
			case NONE -> shape;
			case FRONT_BACK -> switch (shape) {
				case E -> Shape.W;
				case W -> Shape.E;
				default -> shape;
			};
			case LEFT_RIGHT -> switch (shape) {
				case N -> Shape.S;
				case S -> Shape.N;
				default -> shape;
			};
		};
		return state.setValue(SHAPE, newShape);
	}

	@Override
	@NotNull
	@SuppressWarnings("deprecation")
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPES.get(state.getValue(SHAPE));
	}

	public enum Shape implements StringRepresentable {
		NE, E, SE, S, SW, W, NW, N;

		@SuppressWarnings("UnstableApiUsage") // (x, z) -> shape
		private static final Table<Integer, Integer, Shape> rowAndColumnLookup = ArrayTable.create(
				IntStream.range(0, 6).boxed().toList(), IntStream.range(0, 6).boxed().toList()
		);

		static {
			for (int i = 1; i < 5; i++) {
				// left wall
				rowAndColumnLookup.put(0, i, W);
				// right wall
				rowAndColumnLookup.put(5, i, E);
				// far wall
				rowAndColumnLookup.put(i, 0, N);
				// near wall
				rowAndColumnLookup.put(i, 5,  S);
			}
			// corners
			rowAndColumnLookup.put(0, 0, NW);
			rowAndColumnLookup.put(5, 0, NE);
			rowAndColumnLookup.put(0, 5, SW);
			rowAndColumnLookup.put(5, 5, SE);
		}

		public static Shape fromRowAndColumn(int x, int z) {
		    return rowAndColumnLookup.get(x, z);
		}

		// maintains corner/wall, ex. NW -> NE
		public Shape clockwise() {
			return values()[wraparound(ordinal() + 2)];
		}

		public static int wraparound(int ordinal) {
			return ordinal % values().length;
		}

		@Override
		@NotNull
		public String getSerializedName() {
			return name().toLowerCase(Locale.ROOT);
		}
	}
}
