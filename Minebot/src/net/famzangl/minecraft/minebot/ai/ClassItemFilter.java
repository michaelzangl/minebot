package net.famzangl.minecraft.minebot.ai;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ClassItemFilter implements ItemFilter {
	private final Class<? extends Item> itemClass;

	public ClassItemFilter(Class<? extends Item> itemClass) {
		this.itemClass = itemClass;
	}

	@Override
	public boolean matches(ItemStack itemStack) {
		return itemStack != null && itemStack.getItem() != null
				&& itemClass.isInstance(itemStack.getItem());
	}
}
