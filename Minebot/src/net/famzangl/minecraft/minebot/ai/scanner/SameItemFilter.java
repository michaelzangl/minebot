package net.famzangl.minecraft.minebot.ai.scanner;

import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.minecraft.item.ItemStack;

/**
 * Filters for items that have the same type as the given item stack.
 * 
 * @author michael
 *
 */
public final class SameItemFilter implements ItemFilter {
	private final ItemStack displayed;

	public SameItemFilter(ItemStack displayed) {
		this.displayed = displayed;
	}

	@Override
	public boolean matches(ItemStack itemStack) {
		if (itemStack == null) {
			return false;
		} else if (itemStack.getItem() != displayed.getItem()) {
			return false;
		} else if (itemStack.getHasSubtypes()
				&& itemStack.getItemDamage() != displayed.getItemDamage()) {
			return false;
		} else if (!ItemStack.areItemStackTagsEqual(itemStack, displayed)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "SameItemFilter [displayed=" + displayed + "]";
	}

}