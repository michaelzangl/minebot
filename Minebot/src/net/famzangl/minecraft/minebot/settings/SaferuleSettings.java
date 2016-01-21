package net.famzangl.minecraft.minebot.settings;

import java.util.regex.Pattern;

public class SaferuleSettings {
	@ClampedFloat(min = 10, max = 180)
	private float maxPitchChangeDegrees = 30;
	@ClampedFloat(min = 10, max = 180)
	private float maxYawChangeDegrees = 40;
	
	private boolean allowTopOfWorldHit = false;
	
	private int placeTorchLightLevel = 3;
	
	private int minimumPlayerDistance = 10;
	
	private String ignoredPlayerRegex = "";

	public float getMaxPitchChangeDegrees() {
		return maxPitchChangeDegrees;
	}

	public float getMaxYawChangeDegrees() {
		return maxYawChangeDegrees;
	}

	public boolean isAllowTopOfWorldHit() {
		return allowTopOfWorldHit;
	}
	
	public int getPlaceTorchLightLevel() {
		return placeTorchLightLevel;
	}
	
	public int getMinimumPlayerDistance() {
		return minimumPlayerDistance;
	}
	
	public Pattern getIgnoredPlayerRegex() {
		if (ignoredPlayerRegex == null || ignoredPlayerRegex.isEmpty()) {
			return Pattern.compile("$a");
		} else {
			return Pattern.compile(ignoredPlayerRegex);
		}
	}
}
