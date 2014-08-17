package net.famzangl.minecraft.minebot.ai.enchanting;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.AIStrategyFactory;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.minecraft.client.gui.GuiEnchantment;

/**
 * Strategy: kill mobs, until we have enough levels.
 * 
 * @author michael
 * 
 */

public class EnchantStrategy implements AIStrategy, AIStrategyFactory {


	private final int level;

	public EnchantStrategy() {
		this(30);
	}
	
	public EnchantStrategy(int level) {
		this.level = level;
	}

	@Override
	public AIStrategy produceStrategy(AIHelper helper) {
		return this;
	}

	@Override
	public void searchTasks(AIHelper helper) {
		if (hasLevelsToEnchant(helper)) {
			if (enchantmentTableOpened(helper)) {
				helper.addTask(new PutItemInTableTask());
				helper.addTask(new SelectEnchantmentTask());
			} else {
				helper.addTask(new FaceBlockOfTypeTask());
				helper.addTask(new ClickOnEnchantmentTable());
			}
		} else if (enchantmentTableOpened(helper)) {
			helper.addTask(new TakeEnchantedItemTask());
			helper.addTask(new CloseScreenTask());
		} else {
			helper.addTask(new FaceAnyMobTask());
			helper.addTask(new KillAnyMobTask());
		}
	}

	private boolean hasLevelsToEnchant(AIHelper helper) {
		return helper.getMinecraft().thePlayer.experienceLevel >= level;
	}

	private boolean enchantmentTableOpened(AIHelper helper) {
		return helper.getMinecraft().currentScreen instanceof GuiEnchantment;
	}

	@Override
	public String getDescription() {
		return "Enchanting for level " + level;
	}

	@Override
	public AITask getOverrideTask(AIHelper helper) {
		return null;
	}
}
