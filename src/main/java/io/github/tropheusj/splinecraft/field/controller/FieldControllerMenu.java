package io.github.tropheusj.splinecraft.field.controller;

import io.github.tropheusj.splinecraft.Content;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class FieldControllerMenu extends AbstractContainerMenu {
	private final Inventory playerInv;
	protected final FieldControllerBlockEntity be;

	protected FieldControllerMenu(int containerId, Inventory playerInv, FieldControllerBlockEntity be) {
		super(Content.CONTROLLER_MENU, containerId);
		this.playerInv = playerInv;
		this.be = be;
		addPlayerSlots();
		// controller slot
		addSlot(new Slot(be, 0, 150, 50));
	}

	public static FieldControllerMenu createOnClient(int syncId, Inventory inventory, FriendlyByteBuf buf) {
		BlockPos pos = buf.readBlockPos();
		BlockEntity be = inventory.player.level.getBlockEntity(pos);
		if (!(be instanceof FieldControllerBlockEntity controller))
			throw new IllegalStateException("no FieldControllerBlockEntity at " + pos);
		return new FieldControllerMenu(syncId, inventory, controller);
	}

	protected void addPlayerSlots() {
		// main inv
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(playerInv, j + i * 9 + 9, 112 + j * 18, 144 + i * 18));
			}
		}
		// hotbar
		for (int i = 0; i < 9; ++i) {
			this.addSlot(new Slot(playerInv, i, 112 + i * 18, 144 + 58));
		}
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}
}
