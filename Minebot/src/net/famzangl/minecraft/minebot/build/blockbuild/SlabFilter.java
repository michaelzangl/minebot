package net.famzangl.minecraft.minebot.build.blockbuild;

import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class SlabFilter extends BlockItemFilter {
	private final SlabType type;

	public SlabFilter(SlabType type) {
		super(type.slabBlock);
		this.type = type;
	}

	@Override
	protected boolean matchesItem(ItemStack itemStack, ItemBlock item) {
		return super.matchesItem(itemStack, item)
				&& (itemStack.getItemDamage() & 7) == type.meta;
	}
}
