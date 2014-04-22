package net.famzangl.minecraft.minebot.build;

import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class WoodItemFilter extends BlockItemFilter {

	private WoodType woodType;

	public WoodItemFilter(String woodType) {
		super(Blocks.planks);
		this.woodType = WoodType.valueOf(woodType.toUpperCase());
	}

	@Override
	public boolean matches(ItemStack itemStack) {
		return super.matches(itemStack)
				&& itemStack.getItemDamage() == woodType.ordinal();
	}
}
