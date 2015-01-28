package net.famzangl.minecraft.minebot.ai.strategy;

import java.util.Arrays;

import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.minecraft.item.ItemStack;

/**
 * An inverted {@link ItemFilter}.
 * 
 * @author michael
 *
 */
public class NoneOfFilter implements ItemFilter {

	private final ItemFilter[] filters;

	public NoneOfFilter(ItemFilter... filters) {
		this.filters = filters;
	}

	@Override
	public boolean matches(ItemStack itemStack) {
		for (ItemFilter filter : filters) {
			if (filter.matches(itemStack)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "NoneOfFilter [filters=" + Arrays.toString(filters) + "]";
	}
}
