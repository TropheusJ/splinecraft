package io.github.tropheusj.splinecraft.robot.configurator;

import io.github.tropheusj.splinecraft.Content;
import io.github.tropheusj.splinecraft.robot.config.RobotConfig;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotConfiguratorBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {
	public static final Component TITLE = Component.translatable("splinecraft.robot.configurator.menu");

	public final RobotConfig robotConfig = new RobotConfig();

	public RobotConfiguratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
		super(type, pos, blockState);
	}

	public RobotConfiguratorBlockEntity(BlockPos pos, BlockState state) {
		this(Content.CONFIGURATOR_BLOCK_ENTITY, pos, state);
	}

	public void readRobotConfig(CompoundTag tag) {
		robotConfig.read(tag);
		setChanged();
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level != null)
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
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
	protected void saveAdditional(CompoundTag tag) {
		tag.put("RobotConfig", robotConfig.write());
	}

	@Override
	public void load(CompoundTag tag) {
		if (tag.contains("RobotConfig", Tag.TAG_COMPOUND))
			robotConfig.read(tag.getCompound("RobotConfig"));
	}

	// menu stuff

	@Override
	public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
		buf.writeBlockPos(worldPosition);
	}

	@Override
	@NotNull
	public Component getDisplayName() {
		return TITLE;
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
		return new RobotConfiguratorMenu(i, inventory, this);
	}
}
