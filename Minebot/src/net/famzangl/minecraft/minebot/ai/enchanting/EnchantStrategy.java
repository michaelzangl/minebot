package net.famzangl.minecraft.minebot.ai.enchanting;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.strategy.TaskStrategy;
import net.minecraft.client.gui.GuiEnchantment;

/**
 * Strategy: kill mobs, until we have enough levels.
 * 
 * @author michael
 * 
 */

public class EnchantStrategy extends TaskStrategy {

	private final int level;

	public EnchantStrategy() {
		this(30);
	}

	public EnchantStrategy(int level) {
		this.level = level;
	}

	@Override
	public void searchTasks(AIHelper helper) {
		if (hasLevelsToEnchant(helper)) {
			if (enchantmentTableOpened(helper)) {
				addTask(new PutItemInTableTask());
				addTask(new SelectEnchantmentTask());
			} else {
				addTask(new FaceBlockOfTypeTask());
				addTask(new ClickOnEnchantmentTable());
			}
		} else if (enchantmentTableOpened(helper)) {
			addTask(new TakeEnchantedItemTask());
			addTask(new CloseScreenTask());
		} else {
			addTask(new FaceAnyMobTask());
			addTask(new KillAnyMobTask());
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
}
