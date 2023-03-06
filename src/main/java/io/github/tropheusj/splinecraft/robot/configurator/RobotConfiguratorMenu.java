package io.github.tropheusj.splinecraft.robot.configurator;

import io.github.tropheusj.splinecraft.Content;
import io.github.tropheusj.splinecraft.field.controller.FieldControllerBlockEntity;
import io.github.tropheusj.splinecraft.field.controller.FieldControllerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.NotNull;

public class RobotConfiguratorMenu extends AbstractContainerMenu {
	protected final RobotConfiguratorBlockEntity be;

	protected RobotConfiguratorMenu(int containerId, Inventory playerInv, RobotConfiguratorBlockEntity be) {
		super(null, containerId);
		this.be = be;
	}

	public static RobotConfiguratorMenu createOnClient(int syncId, Inventory inventory, FriendlyByteBuf buf) {
		BlockPos pos = buf.readBlockPos();
		BlockEntity be = inventory.player.level.getBlockEntity(pos);
		if (!(be instanceof RobotConfiguratorBlockEntity configurator))
			throw new IllegalStateException("no RobotConfiguratorBlockEntity at " + pos);
		return new RobotConfiguratorMenu(syncId, inventory, configurator);
	}

	@Override
	@NotNull
	public ItemStack quickMoveStack(Player player, int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}
}
