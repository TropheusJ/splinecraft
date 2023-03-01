package io.github.tropheusj.splinecraft;

import io.github.tropheusj.splinecraft.field.controller.FieldControllerScreen;
import io.github.tropheusj.splinecraft.packets.clientbound.ClientboundPackets;
import io.github.tropheusj.splinecraft.robot.configurator.RobotConfiguratorScreen;
import io.github.tropheusj.splinecraft.robot.entity.RobotEntityRenderer;
import io.github.tropheusj.splinecraft.robot.entity.RobotModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class SplineCraftClient implements ClientModInitializer {
	public static final ModelLayerLocation ROBOT_MODEL_LAYER = new ModelLayerLocation(SplineCraft.id("robot"), "main");

	@Override
	public void onInitializeClient() {
		MenuScreens.register(Content.CONTROLLER_MENU, FieldControllerScreen::new);
		MenuScreens.register(Content.CONFIGURATOR_MENU, RobotConfiguratorScreen::new);
		EntityModelLayerRegistry.registerModelLayer(ROBOT_MODEL_LAYER, RobotModel::createBodyLayer);
		EntityRendererRegistry.register(Content.ROBOT_ENTITY, RobotEntityRenderer::new);
		ClientboundPackets.init();
	}
}
