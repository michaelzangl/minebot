package net.famzangl.minecraft.minebot.settings;

public class SaferuleSettings {
	@ClampedFloat(min = 10, max = 180)
	private float maxPitchChangeDegrees = 30;
	@ClampedFloat(min = 10, max = 180)
	private float maxYawChangeDegrees = 40;
	
	private boolean allowTopOfWorldHit = false;

	public float getMaxPitchChangeDegrees() {
		return maxPitchChangeDegrees;
	}

	public float getMaxYawChangeDegrees() {
		return maxYawChangeDegrees;
	}

	public boolean isAllowTopOfWorldHit() {
		return allowTopOfWorldHit;
	}
}
