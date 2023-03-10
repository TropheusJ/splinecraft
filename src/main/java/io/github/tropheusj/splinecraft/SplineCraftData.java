package io.github.tropheusj.splinecraft;

import io.github.tropheusj.splinecraft.field.floor.FieldFloorModelGen;
import io.github.tropheusj.splinecraft.field.wall.FieldWallModelGen;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class SplineCraftData implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator gen) {
		gen.addProvider(FieldFloorModelGen::new);
		gen.addProvider(FieldWallModelGen::new);
	}
}
