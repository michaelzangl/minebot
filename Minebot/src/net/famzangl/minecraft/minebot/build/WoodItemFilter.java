package net.famzangl.minecraft.minebot.build;

import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class WoodItemFilter extends BlockItemFilter {

	private final WoodType woodType;

	public WoodItemFilter(WoodType woodType) {
		super(Blocks.planks);
		this.woodType = woodType;
	}

	@Override
	public boolean matches(ItemStack itemStack) {
		return super.matches(itemStack)
				&& itemStack.getItemDamage() == woodType.ordinal();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((woodType == null) ? 0 : woodType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		WoodItemFilter other = (WoodItemFilter) obj;
		if (woodType != other.woodType)
			return false;
		return true;
	}

}
