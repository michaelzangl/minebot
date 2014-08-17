package net.famzangl.minecraft.minebot.ai.command;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.minecraft.client.Minecraft;

public interface IAIControllable {

	Minecraft getMinecraft();

	AIHelper getAiHelper();

	void requestUseStrategy(AIStrategy strategy);

}
