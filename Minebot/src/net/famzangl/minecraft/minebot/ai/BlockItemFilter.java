package net.famzangl.minecraft.minebot.ai;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class BlockItemFilter implements HumanReadableItemFilter {

	private final BlockWhitelist matched;

	public BlockItemFilter(Block... matched) {
		this.matched = new BlockWhitelist(matched);
	}

	public BlockItemFilter(BlockWhitelist matched) {
		this.matched = matched;
	}

	@Override
	public boolean matches(ItemStack itemStack) {
		return itemStack != null && itemStack.getItem() != null
				&& itemStack.getItem() instanceof ItemBlock
				&& matchesItem(itemStack, (ItemBlock) itemStack.getItem());
	}

	protected boolean matchesItem(ItemStack itemStack, ItemBlock item) {
		return matched.contains(item.field_150939_a);
	}

	@Override
	public String toString() {
		return "BlockItemFilter [matched=" + matched + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((matched == null) ? 0 : matched.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BlockItemFilter other = (BlockItemFilter) obj;
		if (matched == null) {
			if (other.matched != null)
				return false;
		} else if (!matched.equals(other.matched))
			return false;
		return true;
	}

	@Override
	public String getDescription() {
		final StringBuilder str = new StringBuilder();
		matched.getBlockString(str);
		return str.toString();
	}

}
