package io.github.tropheusj.splinecraft.field.floor;

import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.client.resources.model.BakedModel;

public class FieldFloorModel extends ForwardingBakedModel {
	public FieldFloorModel(BakedModel wrapped) {
		this.wrapped = wrapped;
	}
}
