package io.github.tropheusj.splinecraft.robot.config;

import io.github.tropheusj.splinecraft.robot.config.RobotConfig.RobotConfigEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.LinkedHashMap;
import java.util.Map;

public class ClientRobotConfig {
	public static final int Y_INTERVAL = 23;

	public final RobotConfig config;
	public final Map<RobotConfigEntry, EditBox> editBoxes = new LinkedHashMap<>();

	private final int x;
	private int y;

	public ClientRobotConfig(RobotConfig config, int firstX, int firstY, int boxWidth, Runnable onChanged) {
		this.config = new RobotConfig();
		this.config.read(config.write());
		this.x = firstX;
		this.y = firstY;
		Font font = Minecraft.getInstance().font;
		this.config.entries.values().forEach(entry -> {
			MutableComponent title = Component.translatable("splinecraft.robot.config." + entry.name);
			EditBox box = new EditBox(font, x, y, boxWidth, 20, title);
			box.setValue(String.valueOf(entry.value));
			box.setFilter(ClientRobotConfig::isDouble);
			box.setMaxLength(32);
			box.setResponder(s -> {
				entry.value = Double.parseDouble(s);
				onChanged.run();
			});
			editBoxes.put(entry, box);
			y += Y_INTERVAL;
		});
	}

	public void tick() {
		editBoxes.values().forEach(EditBox::tick);
	}

	// https://stackoverflow.com/questions/3133770/how-to-find-out-if-the-value-contained-in-a-string-is-double-or-not
	private static final String doubleRegex = "[\\x00-\\x20]*[+-]?((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)([eE][+-]?(\\p{Digit}+))?)|(\\.(\\p{Digit}+)([eE][+-]?(\\p{Digit}+))?)|(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))[pP][+-]?(\\p{Digit}+)))[fFdD]?)[\\x00-\\x20]*";

	private static boolean isDouble(String s) {
		return s.matches(doubleRegex);
	}
}
