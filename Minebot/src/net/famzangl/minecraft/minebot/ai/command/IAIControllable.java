package net.famzangl.minecraft.minebot.ai.command;

import net.famzangl.minecraft.minebot.ai.AIController;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.minecraft.client.Minecraft;

/**
 * This is something that could be controlled by the bot.
 * 
 * @see AIController
 * @author michael
 *
 */
public interface IAIControllable {

	/**
	 * Get the current minecraft instance.
	 * 
	 * @return
	 */
	Minecraft getMinecraft();

	AIHelper getAiHelper();

	/**
	 * Request to pass control to that strategy.
	 * 
	 * @param strategy
	 *            The new strategy.
	 */
	void requestUseStrategy(AIStrategy strategy);

}
