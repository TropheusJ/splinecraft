package io.github.tropheusj.splinecraft.field.controller;

import io.github.tropheusj.splinecraft.Content;
import io.github.tropheusj.splinecraft.SplineCraft;
import io.github.tropheusj.splinecraft.robot.configurator.RobotConfiguratorBlockEntity;
import io.github.tropheusj.splinecraft.robot.entity.RobotEntity;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FieldControllerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, Container {
	public static final ResourceLocation FIELD_STRUCTURE = SplineCraft.id("field");
	public static final Component TITLE = Component.translatable("splinecraft.field.controller.menu");

	private final ItemStack remote;
	private RobotEntity robot;
	protected boolean initialized;

	public FieldControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
		super(type, pos, blockState);
		this.remote = new ItemStack(Content.REMOTE_CONTROLLER);
		CompoundTag tag = new CompoundTag();
		tag.putIntArray("Controller", List.of(pos.getX(), pos.getY(), pos.getZ()));
		remote.setTag(tag);
	}

	public FieldControllerBlockEntity(BlockPos pos, BlockState state) {
		this(Content.CONTROLLER_BLOCK_ENTITY, pos, state);
	}

	public Direction getFacing() {
		return getBlockState().getValue(FieldControllerBlock.FACING);
	}

	public void resetField() {
		if (!(level instanceof ServerLevel serverLevel))
			return;

		// place a fresh copy of the field
		Direction facing = getFacing();
		Direction left = facing.getCounterClockWise();
		BlockPos fieldFloorCorner = worldPosition.below()
				.relative(facing, 6)
				.relative(left, 3);

		StructureTemplate field = serverLevel.getStructureManager().get(FIELD_STRUCTURE)
				.orElseThrow(() -> new IllegalStateException("Field structure not found"));
		Rotation rotation = switch (facing) {
			case EAST -> Rotation.CLOCKWISE_90;
			case SOUTH -> Rotation.CLOCKWISE_180;
			case WEST -> Rotation.COUNTERCLOCKWISE_90;
			default -> Rotation.NONE;
		};
		StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(rotation);
		field.placeInWorld(serverLevel, fieldFloorCorner, fieldFloorCorner, settings, level.random, Block.UPDATE_ALL);

		BlockPos robotStart = worldPosition.relative(facing).relative(left, 2);

		// set up the robot entity
		if (robot != null)
			robot.discard();
		this.robot = new RobotEntity(Content.ROBOT_ENTITY, level);
		robot.moveTo(robotStart, facing.toYRot(), 0);
		level.addFreshEntity(robot);

		// add robot configurator
		BlockPos configPos = worldPosition.relative(left);
		level.setBlockAndUpdate(configPos, Content.ROBOT_CONFIGURATOR.block().defaultBlockState());
		if (level.getBlockEntity(configPos) instanceof RobotConfiguratorBlockEntity config) {
			config.setRobot(robot);
		}

		// ready!
		initialized = true;
		setChanged();
	}

	// sync

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	@NotNull
	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		saveAdditional(tag);
		return tag;
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level != null)
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
	}

	// saving

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putBoolean("Initialized", initialized);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.initialized = tag.getBoolean("Initialized");
	}

	// menu factory

	@Override
	public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
		buf.writeBlockPos(worldPosition);
	}

	@Override
	public Component getDisplayName() {
		return TITLE;
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
		return new FieldControllerMenu(i, inventory, this);
	}

	// container

	@Override
	public int getContainerSize() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public ItemStack getItem(int slot) {
		return remote.copy();
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		return remote.copy();
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		return remote.copy();
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public void clearContent() {
	}
}
