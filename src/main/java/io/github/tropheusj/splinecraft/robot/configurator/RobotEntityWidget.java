package io.github.tropheusj.splinecraft.robot.configurator;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import io.github.tropheusj.splinecraft.Content;
import io.github.tropheusj.splinecraft.robot.entity.RobotEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

public class RobotEntityWidget implements Widget, GuiEventListener, NarratableEntry {

	private final RobotEntity robot;
	private int age;

	public final int x, y;
	public final float scale;

	public RobotEntityWidget(int x, int y, float scale) {
		this.x = x;
		this.y = y;
		this.scale = scale;
		this.robot = new RobotEntity(Content.ROBOT_ENTITY, Minecraft.getInstance().level);
	}

	public void tick() {
		age++;
	}

	/**
	 * Based on {@link InventoryScreen#renderEntityInInventory(int, int, int, float, float, LivingEntity)}
	 */
	@Override
	public void render(PoseStack unused, int mouseX, int mouseY, float partialTick) {
		PoseStack poseStack = RenderSystem.getModelViewStack();
		poseStack.pushPose();
		poseStack.translate(x, y, 1050.0);
		poseStack.scale(1.0F, 1.0F, -1.0F);
		RenderSystem.applyModelViewMatrix();
		PoseStack poseStack2 = new PoseStack();
		poseStack2.translate(0.0, 0.0, 1000.0);
		poseStack2.scale((float)scale, (float)scale, (float)scale);
		Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
		Quaternion quaternion2 = Vector3f.XP.rotationDegrees(-20);
		quaternion.mul(quaternion2);
		poseStack2.mulPose(quaternion);
		poseStack2.mulPose(Vector3f.YP.rotationDegrees((age + partialTick) * 3.5f));
		Lighting.setupForEntityInInventory();
		EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
		quaternion2.conj();
		entityRenderDispatcher.overrideCameraOrientation(quaternion2);
		entityRenderDispatcher.setRenderShadow(false);
		MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
		RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(
				robot, 0.0, 0.0, 0.0, 0.0F, 1.0F, poseStack2, bufferSource, 15728880
		));
		bufferSource.endBatch();
		entityRenderDispatcher.setRenderShadow(true);
		poseStack.popPose();
		RenderSystem.applyModelViewMatrix();
		Lighting.setupFor3DItems();
	}

	@Override
	@NotNull
	public NarrationPriority narrationPriority() {
		return NarrationPriority.NONE;
	}

	@Override
	public void updateNarration(NarrationElementOutput narrationElementOutput) {
	}
}
