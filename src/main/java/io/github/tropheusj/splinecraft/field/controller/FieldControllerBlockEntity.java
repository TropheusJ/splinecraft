package io.github.tropheusj.splinecraft.field.controller;

import io.github.tropheusj.splinecraft.Content;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FieldControllerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, Container {
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
		if (level == null)
			return;

		BlockPos pos = worldPosition.relative(getFacing(), 3);
		level.setBlockAndUpdate(pos, Blocks.STONE.defaultBlockState());

		if (robot != null)
			robot.discard();
		this.robot = new RobotEntity(Content.ROBOT_ENTITY, level);
		robot.moveTo(pos.above(), 0, 0);
		level.addFreshEntity(robot);

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
