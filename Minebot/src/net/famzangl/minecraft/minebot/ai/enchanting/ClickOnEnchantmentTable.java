package net.famzangl.minecraft.minebot.ai.enchanting;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.task.UseItemOnBlockTask;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.init.Blocks;

public class ClickOnEnchantmentTable extends UseItemOnBlockTask {

	public ClickOnEnchantmentTable() {
		super(Blocks.enchanting_table);
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return ((h.getMinecraft().currentScreen) instanceof GuiEnchantment);
	}

}
