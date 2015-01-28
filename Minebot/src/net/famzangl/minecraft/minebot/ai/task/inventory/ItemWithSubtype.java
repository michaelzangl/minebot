package net.famzangl.minecraft.minebot.ai.task.inventory;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * A simple (itemid, damagevalue) touple.
 * 
 * TODO: Use this more in the bot.
 * 
 * @author michael
 *
 */
public class ItemWithSubtype {

	private final int itemId;
	private final int itemDamage;
	private final boolean hasSubtype;

	public ItemWithSubtype(ItemStack stack) {
		this(Item.getIdFromItem(stack.getItem()), stack.getItemDamage());
	}

	public ItemWithSubtype(int itemId, int itemDamage) {
		this.itemId = itemId;
		this.itemDamage = itemDamage;
		this.hasSubtype = Item.getItemById(itemId).getHasSubtypes();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (hasSubtype ? 1231 : 1237);
		if (hasSubtype) {
			result = prime * result + itemDamage;
		}
		result = prime * result + itemId;
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
		ItemWithSubtype other = (ItemWithSubtype) obj;
		if (hasSubtype != other.hasSubtype)
			return false;
		if (itemId != other.itemId)
			return false;
		if (hasSubtype && itemDamage != other.itemDamage)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return hasSubtype ? itemId + ":" + itemDamage : itemId + "";
	}

	public ItemWithSubtype withSubtype(int subtype) {
		return new ItemWithSubtype(itemId, subtype);
	}
}
