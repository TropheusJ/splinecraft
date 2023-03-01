package io.github.tropheusj.splinecraft.field.controller;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import io.github.tropheusj.splinecraft.SplineCraft;
import io.github.tropheusj.splinecraft.packets.serverbound.ServerboundResetFieldPacket;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FieldControllerScreen extends AbstractContainerScreen<FieldControllerMenu> {
	public static final ResourceLocation TEXTURE = SplineCraft.id("textures/gui/controller.png");
	public static final Component INIT = Component.translatable("splinecraft.field.controller.initialize");
	public static final Component RESET = Component.translatable("splinecraft.field.controller.reset");
	public static final int IMAGE_WIDTH = 384;
	public static final int IMAGE_HEIGHT = 256;
	public static final int SIDE_PANEL_WIDTH = 104;
	public static final int BUTTON_SIZE = 20;

	private Button initButton, playButton, pauseButton;
	private boolean initialized;

	public FieldControllerScreen(FieldControllerMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
	}

	@Override
	protected void init() {
		this.imageWidth = IMAGE_WIDTH;
		this.imageHeight = IMAGE_HEIGHT;
		super.init();
		this.inventoryLabelX = 112;
		this.inventoryLabelY = 132;
		this.titleLabelX = 112;
		this.titleLabelY = 50;
		Component initText = initialized() ? RESET : INIT;
		initButton = new Button(150, 50, 20, 20, initText, this::initialize);
		addRenderableWidget(initButton);
		initialized = initialized();
		if (initialized) {
			// playback
			playButton = new ImageButton(170, 70, BUTTON_SIZE, BUTTON_SIZE, 0, IMAGE_HEIGHT - (BUTTON_SIZE * 2),
					BUTTON_SIZE, TEXTURE, IMAGE_WIDTH, IMAGE_HEIGHT, this::play);
			pauseButton = new ImageButton(170, 70, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE, IMAGE_HEIGHT - (BUTTON_SIZE * 2),
					BUTTON_SIZE, TEXTURE, IMAGE_WIDTH, IMAGE_HEIGHT, this::pause);
			addRenderableWidget(playButton);
			// todo add slider
		}
	}

	@Override
	protected void containerTick() {
		super.containerTick();
		if (!initialized && initialized()) {
			// re-init when block entity initializes
			removeWidget(initButton);
			init();
		}
	}

	public boolean initialized() {
		return menu.be.initialized;
	}

	protected void initialize(Button b) {
		ServerboundResetFieldPacket.send(menu.be);
	}

	protected void play(Button b) {
		removeWidget(playButton);
		addRenderableWidget(pauseButton);
	}

	protected void pause(Button b) {
		removeWidget(pauseButton);
		addRenderableWidget(playButton);
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, partialTick);
		this.renderTooltip(poseStack, mouseX, mouseY);
	}

	@Override
	protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		int offset = initialized() ? 0 : SIDE_PANEL_WIDTH;
		int width = imageWidth - (offset * 2);
		poseStack.pushPose();
		poseStack.translate(0, 10, 0); // a little too tall for default resolution
		//noinspection SuspiciousNameCombination // mapped W/H names are flipped
		blit(poseStack, leftPos, topPos, 0, offset, 0, width, imageHeight - 40, imageWidth, imageHeight);
		poseStack.popPose();
	}
}
