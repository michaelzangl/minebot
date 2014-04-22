package net.famzangl.minecraft.minebot.build;

import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.minecraft.item.ItemStack;

public class LogItemFilter extends BlockItemFilter {

	private WoodType logType;

	public LogItemFilter(String logType) {
		super(WoodType.valueOf(logType.toUpperCase()).block);
		this.logType = WoodType.valueOf(logType.toUpperCase());
	}

	@Override
	public boolean matches(ItemStack itemStack) {
		return super.matches(itemStack)
				&& (itemStack.getItemDamage() & 3) == logType.lowerBits;
	}
}
