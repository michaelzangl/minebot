package net.famzangl.minecraft.minebot.ai;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class BlockItemFilter implements ItemFilter {

	private final Block[] matched;

	public BlockItemFilter(Block... matched) {
		this.matched = matched;
	}

	@Override
	public boolean matches(ItemStack itemStack) {
		return itemStack != null && itemStack.getItem() != null
				&& itemStack.getItem() instanceof ItemBlock
				&& matchesItem(itemStack, (ItemBlock) itemStack.getItem());
	}

	protected boolean matchesItem(ItemStack itemStack, ItemBlock item) {
		return AIHelper.blockIsOneOf(item.field_150939_a, matched);
	}

	@Override
	public String toString() {
		return "BlockItemFilter [matched=" + Arrays.toString(matched) + "]";
	}
}
