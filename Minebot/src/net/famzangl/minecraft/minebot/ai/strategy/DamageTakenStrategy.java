package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;

public class DamageTakenStrategy extends ValueActionStrategy {

	@Override
	protected double getValue(AIHelper helper) {
		return helper.getMinecraft().thePlayer.getHealth();
	}

	@Override
	protected String getSettingPrefix() {
		return "on_damage_";
	}

	@Override
	public String getDescription(AIHelper helper) {
		return "Check if health went down.";
	}
}
