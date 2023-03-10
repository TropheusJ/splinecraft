package io.github.tropheusj.splinecraft;

import io.github.tropheusj.splinecraft.field.controller.FieldControllerBlock;
import io.github.tropheusj.splinecraft.field.controller.FieldControllerBlockEntity;
import io.github.tropheusj.splinecraft.field.floor.FieldFloorBlock;
import io.github.tropheusj.splinecraft.field.wall.FieldWallBlock;
import io.github.tropheusj.splinecraft.field.controller.FieldControllerMenu;
import io.github.tropheusj.splinecraft.field.controller.RemoteControllerItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

import static io.github.tropheusj.splinecraft.SplineCraft.id;

public class Content {
	public static final BlockAndItem FIELD_CONTROLLER = blockAndItem("field_controller", new FieldControllerBlock(Properties.copy(Blocks.IRON_BLOCK)));

	public static final Block FIELD_WALL = block("field_wall", new FieldWallBlock(Properties.copy(Blocks.IRON_BARS)));
	public static final Block FIELD_FLOOR = block("field_floor", new FieldFloorBlock(Properties.copy(Blocks.BLACK_WOOL)));

	public static final Item REMOTE_CONTROLLER = Registry.register(
			Registry.ITEM, id("remote_controller"),
			new RemoteControllerItem(new FabricItemSettings().stacksTo(1))
	);

	public static final BlockEntityType<FieldControllerBlockEntity> CONTROLLER_BLOCK_ENTITY = Registry.register(
			Registry.BLOCK_ENTITY_TYPE, id("field_controller"),
			FabricBlockEntityTypeBuilder.create(FieldControllerBlockEntity::new, FIELD_CONTROLLER.block).build()
	);

	public static final MenuType<FieldControllerMenu> CONTROLLER_MENU = Registry.register(
			Registry.MENU, id("field_controller"),
			new ExtendedScreenHandlerType<>(FieldControllerMenu::createOnClient)
	);

	private static BlockAndItem blockAndItem(String name, Block block) {
		block = block(name, block);
		BlockItem item = Registry.register(Registry.ITEM, SplineCraft.id(name), new BlockItem(block, new FabricItemSettings()));
		return new BlockAndItem(block, item);
	}

	private static Block block(String name, Block block) {
		ResourceLocation id = id(name);
		return Registry.register(Registry.BLOCK, id, block);
	}

	public static void init() {
	}

	public record BlockAndItem(Block block, BlockItem item) {
	}
}
