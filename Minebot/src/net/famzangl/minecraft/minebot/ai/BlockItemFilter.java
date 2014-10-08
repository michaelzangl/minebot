package net.famzangl.minecraft.minebot.ai;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class BlockItemFilter implements HumanReadableItemFilter {

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(matched);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final BlockItemFilter other = (BlockItemFilter) obj;
		if (!Arrays.equals(matched, other.matched)) {
			return false;
		}
		return true;
	}

	@Override
	public String getDescription() {
		final StringBuilder str = new StringBuilder();
		for (final Block m : matched) {
			if (str.length() > 0) {
				str.append(", ");
			}
			str.append(m.getLocalizedName());
		}
		return str.toString();
	}

}
