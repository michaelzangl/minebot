package net.famzangl.minecraft.minebot.build;

import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.minecraft.item.ItemStack;

public class LogItemFilter extends BlockItemFilter {

	private final WoodType logType;

	public LogItemFilter(WoodType logType) {
		super(logType.block);
		this.logType = logType;
	}

	@Override
	public boolean matches(ItemStack itemStack) {
		return super.matches(itemStack)
				&& (itemStack.getItemDamage() & 3) == logType.lowerBits;
	}
}
