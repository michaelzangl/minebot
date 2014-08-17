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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		SlabFilter other = (SlabFilter) obj;
		if (type != other.type)
			return false;
		return true;
	}
	
	@Override
	public String getDescriptiveString() {
		return type.toString().toLowerCase() + " slabs";
	}
}
