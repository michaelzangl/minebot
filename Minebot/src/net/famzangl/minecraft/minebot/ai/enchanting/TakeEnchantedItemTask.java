package net.famzangl.minecraft.minebot.ai.enchanting;

import net.famzangl.minecraft.minebot.ai.task.inventory.TakeResultItem;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.item.ItemStack;

public class TakeEnchantedItemTask extends TakeResultItem {

	public TakeEnchantedItemTask() {
		super(GuiEnchantment.class, 0);
	}

	@Override
	protected boolean shouldTakeStack(ItemStack stack) {
		return stack.isItemEnchanted();
	}
}
