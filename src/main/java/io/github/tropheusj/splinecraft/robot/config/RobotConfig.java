package io.github.tropheusj.splinecraft.robot.config;

import net.minecraft.nbt.CompoundTag;

import java.util.LinkedHashMap;
import java.util.Map;

public class RobotConfig {
	public final Map<String, RobotConfigEntry> entries = new LinkedHashMap<>();
	public final RobotConfigEntry maxVel = entry("max_vel", 30);
	public final RobotConfigEntry maxAccel = entry("max_accel", 30);
	public final RobotConfigEntry maxAngVel = entry("max_ang_vel", 60);
	public final RobotConfigEntry maxAngAccel = entry("max_ang_accel", 60);
	public final RobotConfigEntry trackWidth = entry("track_width", 15);
	public final RobotConfigEntry width = entry("width", 18);
	public final RobotConfigEntry height = entry("height", 18);

	private RobotConfigEntry entry(String name, double initial) {
		RobotConfigEntry entry = new RobotConfigEntry(name, initial, initial);
		entries.put(name, entry);
		return entry;
	}

	public CompoundTag write() {
		CompoundTag tag = new CompoundTag();
		entries.forEach((name, entry) -> tag.putDouble(name, entry.value));
		return tag;
	}

	public void read(CompoundTag tag) {
		entries.forEach((name, entry) -> entry.value = tag.getDouble(name));
	}

	public static final class RobotConfigEntry {
		public final String name;
		public final double initial;
		public double value;

		public RobotConfigEntry(String name, double value, double initial) {
			this.name = name;
			this.value = value;
			this.initial = initial;
		}
	}
}
