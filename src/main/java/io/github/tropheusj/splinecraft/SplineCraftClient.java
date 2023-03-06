package io.github.tropheusj.splinecraft;

import io.github.tropheusj.splinecraft.field.controller.FieldControllerBlockEntityRenderer;
import io.github.tropheusj.splinecraft.field.controller.FieldControllerScreen;
import io.github.tropheusj.splinecraft.packets.clientbound.ClientboundPackets;
import io.github.tropheusj.splinecraft.robot.RobotModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class SplineCraftClient implements ClientModInitializer {
	public static final ModelLayerLocation ROBOT_MODEL_LAYER = new ModelLayerLocation(SplineCraft.id("robot"), "main");

	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(Content.FIELD_WALL, RenderType.cutout());
		MenuScreens.register(Content.CONTROLLER_MENU, FieldControllerScreen::new);
		EntityModelLayerRegistry.registerModelLayer(ROBOT_MODEL_LAYER, RobotModel::createBodyLayer);
		BlockEntityRenderers.register(Content.CONTROLLER_BLOCK_ENTITY, FieldControllerBlockEntityRenderer::new);
		ClientboundPackets.init();
	}
}
