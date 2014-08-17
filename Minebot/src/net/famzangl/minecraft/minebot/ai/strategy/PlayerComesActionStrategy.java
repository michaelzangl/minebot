package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerComesActionStrategy extends CloseEntityActionStrategy {

	@Override
	protected boolean matches(AIHelper helper, Entity player) {
		return player instanceof EntityPlayer && player != helper.getMinecraft().thePlayer;
	}

	@Override
	protected String getSettingPrefix() {
		return "on_player_comes_";
	}

}
