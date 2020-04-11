package net.famzangl.minecraft.minebot.ai.enchanting;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.strategy.TintStrategy;
import net.famzangl.minecraft.minebot.ai.task.inventory.PutItemInContainerTask;
import net.minecraft.client.gui.screen.EnchantmentScreen;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;

/**
 * Put the lapis into the enchantment table.
 * 
 * @author michael
 *
 */
public class PutLapisInTableTask extends PutItemInContainerTask {

	private static final int TABLE_INV_OFFSET = 2;

	@Override
	protected int getStackToPut(AIHelper aiHelper) {
		final EnchantmentScreen screen = (EnchantmentScreen) aiHelper.getMinecraft().currentScreen;
		for (int i = TABLE_INV_OFFSET; i < 9 * 4 + TABLE_INV_OFFSET; i++) {
			Slot slot = screen.getContainer().getSlot(i);
			if (slot == null || !slot.canTakeStack(aiHelper.getMinecraft().player)) {
				continue;
			}
			final ItemStack stack = slot.getStack();
			if (new TintStrategy.DyeItemFilter(DyeColor.BLUE)
					.matches(stack)) {
				return i;
			}
		}
		return -1;
	}
}
