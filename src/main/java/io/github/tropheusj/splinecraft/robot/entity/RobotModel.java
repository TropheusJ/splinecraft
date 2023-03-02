package io.github.tropheusj.splinecraft.robot.entity;

import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class RobotModel extends EntityModel<RobotEntity> {
	private final ModelPart body;
	private final ModelPart front_left_wheel;
	private final ModelPart front_right_wheel;
	private final ModelPart back_left_wheel;
	private final ModelPart back_right_wheel;
	private final ModelPart[] wheels;

	public RobotModel(ModelPart root) {
		this.body = root.getChild("body");
		this.front_left_wheel = root.getChild("front_left_wheel");
		this.front_right_wheel = root.getChild("front_right_wheel");
		this.back_left_wheel = root.getChild("back_left_wheel");
		this.back_right_wheel = root.getChild("back_right_wheel");
		this.wheels = new ModelPart[] { front_left_wheel, front_right_wheel, back_left_wheel, back_right_wheel };
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -12.0F, -6.0F, 12.0F, 12.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(38, 5).addBox(-1.0F, -15.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition front_left_wheel = partdefinition.addOrReplaceChild("front_left_wheel", CubeListBuilder.create().texOffs(4, 6).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(6.5F, 23.0F, -4.0F));

		PartDefinition front_right_wheel = partdefinition.addOrReplaceChild("front_right_wheel", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.5F, 23.0F, -4.0F));

		PartDefinition back_left_wheel = partdefinition.addOrReplaceChild("back_left_wheel", CubeListBuilder.create().texOffs(4, 2).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(6.5F, 23.0F, 4.0F));

		PartDefinition back_right_wheel = partdefinition.addOrReplaceChild("back_right_wheel", CubeListBuilder.create().texOffs(0, 4).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.5F, 23.0F, 4.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(RobotEntity entity, float limbSwing, float limbSwingAmount,
						  float ageInTicks, float netHeadYaw, float headPitch) {
		for (ModelPart wheel : wheels) {
			wheel.xRot = ageInTicks;
		}
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight,
							   int packedOverlay, float red, float green, float blue, float alpha) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		front_left_wheel.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		front_right_wheel.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		back_left_wheel.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		back_right_wheel.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
