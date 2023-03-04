package io.github.tropheusj.splinecraft.robot.entity;

import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.blaze3d.vertex.VertexConsumer;

import com.mojang.math.Vector3f;

import io.github.tropheusj.splinecraft.SplineCraft;
import io.github.tropheusj.splinecraft.SplineCraftClient;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;

public class RobotEntityRenderer extends EntityRenderer<RobotEntity> {
	public static final ResourceLocation TEXTURE = SplineCraft.id("textures/entity/robot.png");

	private final RobotModel model;

	public RobotEntityRenderer(Context context) {
		super(context);
		this.model = new RobotModel(context.bakeLayer(SplineCraftClient.ROBOT_MODEL_LAYER));
	}

	@Override
	public void render(RobotEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
		poseStack.pushPose();
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
		ResourceLocation texture = getTextureLocation(entity);
		RenderType renderType = model.renderType(texture);
		VertexConsumer buffer = bufferSource.getBuffer(renderType);
		model.setupAnim(entity, 0, 0, entity.tickCount + partialTick, 0, 0);
		poseStack.translate(0, 1.5, 0);
		poseStack.mulPose(Vector3f.XP.rotationDegrees(180));
		poseStack.mulPose(Vector3f.YP.rotationDegrees(entityYaw));
		model.renderToBuffer(poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
		poseStack.popPose();
	}

	@Override
	@NotNull
	public ResourceLocation getTextureLocation(RobotEntity entity) {
		return TEXTURE;
	}
}
