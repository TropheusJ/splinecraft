package io.github.tropheusj.splinecraft.robot.configurator;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import io.github.tropheusj.splinecraft.SplineCraft;
import io.github.tropheusj.splinecraft.packets.serverbound.ServerboundApplyRobotConfigPacket;
import io.github.tropheusj.splinecraft.robot.config.ClientRobotConfig;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class RobotConfiguratorScreen extends AbstractContainerScreen<RobotConfiguratorMenu> {
	public static final ResourceLocation TEXTURE = SplineCraft.id("textures/gui/configurator.png");
	public static final Component APPLY_CONFIG = Component.translatable("splinecraft.robot.config.apply");
	public static final int IMAGE_WIDTH = 314;
	public static final int IMAGE_HEIGHT = 216;

	private ClientRobotConfig config;
	private Button applyConfigButton;
	private RobotEntityWidget robot;

	public RobotConfiguratorScreen(RobotConfiguratorMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
	}

	@Override
	protected void init() {
		this.imageWidth = IMAGE_WIDTH;
		this.imageHeight = IMAGE_HEIGHT;
		this.inventoryLabelX = 999999; // begone
		super.init();
		applyConfigButton = new Button(leftPos + 35, topPos + 185, 100, 20, APPLY_CONFIG, this::applyConfig);
		applyConfigButton.active = false;
		addRenderableWidget(applyConfigButton);
		config = new ClientRobotConfig(menu.be.robotConfig, leftPos + 76, topPos + 20, 90, () -> applyConfigButton.active = true);
		config.editBoxes.values().forEach(this::addRenderableWidget);

		robot = new RobotEntityWidget(leftPos + 240, topPos + 135, 60);
		addRenderableWidget(robot);
	}

	@Override
	protected void containerTick() {
		super.containerTick();
		if (robot != null)
			robot.tick();
	}

	private void applyConfig(Button b) {
		ServerboundApplyRobotConfigPacket.send(menu.be, config.config);
		applyConfigButton.active = false;
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, partialTick);
		this.renderTooltip(poseStack, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
		super.renderLabels(poseStack, mouseX, mouseY);
		int i = 0;
		for (EditBox box : config.editBoxes.values()) {
			font.draw(poseStack, box.getMessage(), 8, 25 + (i * 23), 4210752);
			i++;
		}
	}

	@Override
	protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		poseStack.pushPose();
		//noinspection SuspiciousNameCombination // mapped W/H names are flipped
		blit(poseStack, leftPos, topPos, 0, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
		poseStack.popPose();
	}
}
