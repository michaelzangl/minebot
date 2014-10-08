package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;

public class CreeperComesActionStrategy extends CloseEntityActionStrategy {

	@Override
	protected boolean matches(AIHelper helper, Entity player) {
		return player instanceof EntityCreeper;
	}

	@Override
	protected String getSettingPrefix() {
		return "on_creeper_comes_";
	}
	
	@Override
	public String getDescription(AIHelper helper) {
		return "Watch out for creepers.";
	}
}
