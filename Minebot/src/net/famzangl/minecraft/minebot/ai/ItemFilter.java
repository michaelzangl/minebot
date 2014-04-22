package net.famzangl.minecraft.minebot.ai;

import net.minecraft.item.ItemStack;

public interface ItemFilter {
	/**
	 * Should handle <code>null</code>
	 * 
	 * @param itemStack
	 * @return
	 */
	boolean matches(ItemStack itemStack);
}