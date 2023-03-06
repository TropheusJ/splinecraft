package io.github.tropheusj.splinecraft.robot;

import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class RobotModel extends Model {
	public static final double WHEEL_CIRCUMFERENCE = Math.PI * ((2 / 12f) * 18) / 24; // ((wheelPixels / robotPixels) * robotInches) / inchesPerBlock

	private final ModelPart body;
	private final Wheel frontLeft;
	private final Wheel frontRight;
	private final Wheel backLeft;
	private final Wheel backRight;
	private final Wheel[] wheels;

	public RobotModel(ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.body = root.getChild("body");
		this.frontLeft = new Wheel("FL", true, true, root.getChild("front_left_wheel"));
		this.frontRight = new Wheel("FR", true, false, root.getChild("front_right_wheel"));
		this.backLeft = new Wheel("BL", false, true, root.getChild("back_left_wheel"));
		this.backRight = new Wheel("BL", false, false, root.getChild("back_right_wheel"));
		this.wheels = new Wheel[] { frontLeft, backLeft, backRight, frontRight };
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
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight,
							   int packedOverlay, float red, float green, float blue, float alpha) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		for (Wheel wheel : wheels) {
			wheel.part.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		}
	}

	public void rotateWheels(Robot robot) {
		Vec3 robotPos = new Vec3(robot.getX(), 0, robot.getZ());
		Vec3 lastRobotPos = new Vec3(robot.getLastX(), 0, robot.getLastZ());
		double trackWidth = robot.config.trackWidth.value;
		float yRot = (float) Math.toRadians(robot.getYRot());
		Vec3 rotVec = new Vec3(Mth.cos(yRot), 0, Mth.sin(yRot));

		for (Wheel wheel : wheels) {
			Vec3 rawOffset = wheel.getOffsetFromCenter(trackWidth);
			Vec3 lastOffset = rawOffset.yRot(robot.getLastYRot());
			Vec3 lastWheelPos = lastRobotPos.add(lastOffset);
			Vec3 offset = rawOffset.yRot(yRot);
			Vec3 wheelPos = robotPos.add(offset);
			Vec3 displacement = wheelPos.subtract(lastWheelPos);
			rotateWheel(wheel, displacement, rotVec);
		}
	}

	public void rotateWheel(Wheel wheel, Vec3 displacement, Vec3 rotVec) {
		double dot = displacement.dot(rotVec);
		double distance = displacement.length() * dot;
		double revs = distance / WHEEL_CIRCUMFERENCE;
		double rotRad = revs * 2 * Math.PI;

		// ??? but it works
		wheel.part.xRot += revs / 4;
//		if (distance > 0.001)
//			System.out.printf("d=[%s], dX=[%s], dZ=[%s], rad=[%s]; %s\n", distance, displacement.x, displacement.z,rotRad,  wheel);
	}

	public record Wheel(String key, boolean front, boolean left, ModelPart part) {
		public Vec3 getOffsetFromCenter(double trackWidth) {
			double xOffset = (trackWidth / 2) / 24; // to blocks
			return new Vec3(
					left ? -xOffset : xOffset,
					0,
					front ? 0.25 : -0.25
			);
		}
	}
}
