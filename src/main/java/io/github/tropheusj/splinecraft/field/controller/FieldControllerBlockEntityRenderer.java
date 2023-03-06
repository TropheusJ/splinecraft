package io.github.tropheusj.splinecraft.field.controller;

import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import io.github.tropheusj.splinecraft.SplineCraft;
import io.github.tropheusj.splinecraft.SplineCraftClient;
import io.github.tropheusj.splinecraft.robot.Robot;
import io.github.tropheusj.splinecraft.robot.RobotModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class FieldControllerBlockEntityRenderer implements BlockEntityRenderer<FieldControllerBlockEntity> {
	public static final ResourceLocation TEXTURE = SplineCraft.id("textures/entity/robot.png");

	private final RobotModel robotModel;

	public FieldControllerBlockEntityRenderer(Context ctx) {
		this.robotModel = new RobotModel(ctx.bakeLayer(SplineCraftClient.ROBOT_MODEL_LAYER));
	}

	@Override
	public void render(FieldControllerBlockEntity be, float partialTick, PoseStack poseStack,
					   MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		Robot robot = be.getRobot();
		if (robot == null)
			return;
		// update the robot state every render
		// kinda gross, but most accurate
		robot.update();
		poseStack.pushPose();
		// align with controller
		poseStack.translate(0.5, 0, 0.5);
		poseStack.mulPose(Vector3f.YN.rotationDegrees(be.getFacing().toYRot()));
		// move to center of field
		poseStack.translate(-3.5, 0, 3.5);
		// make model sane
		poseStack.translate(0, 1.5, 0);
		poseStack.mulPose(Vector3f.XP.rotationDegrees(180));
		poseStack.mulPose(Vector3f.YP.rotationDegrees(90)); // yRot 0 is right
		// do the rendering
		RenderType renderType = robotModel.renderType(TEXTURE);
		VertexConsumer buffer = bufferSource.getBuffer(renderType);
		poseStack.translate(robot.getX(), 0, robot.getZ());
		poseStack.mulPose(Vector3f.YP.rotationDegrees(robot.getYRot()));
//		robotModel.rotateWheels(robot);
		robotModel.renderToBuffer(
				poseStack, buffer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1
		);
		poseStack.popPose();
	}

	@Override
	public boolean shouldRenderOffScreen(FieldControllerBlockEntity blockEntity) {
		return true;
	}
}
