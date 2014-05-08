package net.famzangl.minecraft.minebot.ai;

import net.minecraft.item.ItemStack;

/**
 * An {@link ItemStack} filter that can match item stacks.
 * 
 * @author michael
 * 
 */
public interface ItemFilter {
	/**
	 * Checks if this filter matches the item.
	 * 
	 * @param itemStack
	 *            The item stack. It might be <code>null</code> for an empty
	 *            stack
	 * @return If the stack matches this filter.
	 */
	boolean matches(ItemStack itemStack);
}