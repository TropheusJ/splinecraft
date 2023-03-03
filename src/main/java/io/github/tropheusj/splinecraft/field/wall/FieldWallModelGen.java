package io.github.tropheusj.splinecraft.field.wall;

import io.github.tropheusj.splinecraft.Content;
import io.github.tropheusj.splinecraft.SplineCraft;
import io.github.tropheusj.splinecraft.field.wall.FieldWallBlock.Shape;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.Condition;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.blockstates.VariantProperties.Rotation;
import net.minecraft.resources.ResourceLocation;

public class FieldWallModelGen extends FabricModelProvider {
	public FieldWallModelGen(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators models) {
		ResourceLocation model = SplineCraft.id("block/field_wall");
		models.blockStateOutput.accept(MultiPartGenerator.multiPart(Content.FIELD_WALL)
				.with(
						Condition.condition().term(FieldWallBlock.SHAPE, Shape.NW, Shape.N, Shape.NE),
						Variant.variant().with(VariantProperties.MODEL, model)
				)
				.with(
						Condition.condition().term(FieldWallBlock.SHAPE, Shape.NE, Shape.E, Shape.SE),
						Variant.variant().with(VariantProperties.MODEL, model).with(VariantProperties.Y_ROT, Rotation.R90)
				)
				.with(
						Condition.condition().term(FieldWallBlock.SHAPE, Shape.SE, Shape.S, Shape.SW),
						Variant.variant().with(VariantProperties.MODEL, model).with(VariantProperties.Y_ROT, Rotation.R180)
				)
				.with(
						Condition.condition().term(FieldWallBlock.SHAPE, Shape.SW, Shape.W, Shape.NW),
						Variant.variant().with(VariantProperties.MODEL, model).with(VariantProperties.Y_ROT, Rotation.R270)
				)
		);
	}

	@Override
	public void generateItemModels(ItemModelGenerators itemModelGenerator) {
	}
}
