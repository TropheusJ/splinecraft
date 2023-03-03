package io.github.tropheusj.splinecraft.field.floor;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import com.google.gson.JsonElement;

import com.google.gson.JsonParser;

import io.github.tropheusj.splinecraft.Content;
import io.github.tropheusj.splinecraft.SplineCraft;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.resources.ResourceLocation;

public class FieldFloorModelGen extends FabricModelProvider {
	public static final String U1_PLACEHOLDER = "$U1";
	public static final String U2_PLACEHOLDER = "$U2";
	public static final String V1_PLACEHOLDER = "$V1";
	public static final String V2_PLACEHOLDER = "$V2";
	public static final String TEMPLATE = """
			{
			  "parent": "block/cube",
			  "texture_size": [96, 96],
			  "textures": {
			    "top": "splinecraft:block/field_floor",
			    "side": "splinecraft:block/tile"
			  },
			  "elements": [
			    {
			      "from": [ 0, 0, 0 ],
			      "to": [ 16, 16, 16 ],
			      "faces": {
			        "down":  { "texture": "#side", "cullface": "down" },
			        "up":    { "texture": "#top",  "cullface": "up", "uv": [$U1, $V1, $U2, $V2] },
			        "north": { "texture": "#side", "cullface": "north"	},
			        "south": { "texture": "#side", "cullface": "south"	},
			        "west":  { "texture": "#side", "cullface": "west"	},
			        "east":  { "texture": "#side", "cullface": "east"	}
			      }
			    }
			  ]
			}
			""";

	public FieldFloorModelGen(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators models) {
		Table<Integer, Integer, ResourceLocation> generatedModels = HashBasedTable.create(6, 6);
		for (int row = 0; row < 6; row++) {
			for (int column = 0; column < 6; column++) {
				String u1 = String.valueOf(column * 16 / 6f);
				String v1 = String.valueOf(row * 16 / 6f);
				String u2 = String.valueOf(((column * 16) + 16) / 6f);
				String v2 = String.valueOf(((row * 16) + 16) / 6f);

				String filled = TEMPLATE
						.replace(U1_PLACEHOLDER, u1)
						.replace(V1_PLACEHOLDER, v1)
						.replace(U2_PLACEHOLDER, u2)
						.replace(V2_PLACEHOLDER, v2);

				ResourceLocation id = SplineCraft.id("block/field_floor_column_" + column + "_row_" + row);
				JsonElement json = JsonParser.parseString(filled);
				models.modelOutput.accept(id, () -> json);
				generatedModels.put(row, column, id);
			}
		}

		models.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Content.FIELD_FLOOR)
				.with(BlockModelGenerators.createHorizontalFacingDispatch())
				.with(PropertyDispatch.properties(FieldFloorBlock.ROW, FieldFloorBlock.COLUMN)
						.generate((row, column) -> Variant.variant()
								.with(VariantProperties.MODEL, generatedModels.get(row, column))
						)
				)
		);
	}

	@Override
	public void generateItemModels(ItemModelGenerators models) {
	}
}
