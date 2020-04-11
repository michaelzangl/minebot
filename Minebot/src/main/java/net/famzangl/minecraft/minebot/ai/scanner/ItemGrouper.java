/*******************************************************************************
 * This file is part of Minebot.
 *
 * Minebot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Minebot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Minebot.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package net.famzangl.minecraft.minebot.ai.scanner;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

/**
 * Helper utility to group blocks.
 * TODO: Implement a strategy for this.
 * @author michael
 *
 */
public class ItemGrouper {
	public static class ItemGroup {
		private final Item fromItem;
		private final Item toItem;
		private final int size; // 2 or 3
		public ItemGroup(Item fromItem, Item toItem, int size) {
			super();
			this.fromItem = fromItem;
			this.toItem = toItem;
			this.size = size;
		}
		
	}
	
	public static ItemGroup[] ITEM_GROUPS = new ItemGroup[] {
		new ItemGroup(Items.WHEAT, Item.getItemFromBlock(Blocks.HAY_BLOCK), 3),
		new ItemGroup(Items.COAL, Item.getItemFromBlock(Blocks.COAL_BLOCK), 3),
		new ItemGroup(Items.REDSTONE, Item.getItemFromBlock(Blocks.REDSTONE_BLOCK), 3),
		new ItemGroup(Items.EMERALD, Item.getItemFromBlock(Blocks.EMERALD_BLOCK), 3),
		new ItemGroup(Items.DIAMOND, Item.getItemFromBlock(Blocks.DIAMOND_BLOCK), 3),
		new ItemGroup(Items.IRON_INGOT, Item.getItemFromBlock(Blocks.IRON_BLOCK), 3),
		new ItemGroup(Items.GOLD_INGOT, Item.getItemFromBlock(Blocks.GOLD_BLOCK), 3),
		new ItemGroup(Items.GOLD_NUGGET, Items.GOLD_INGOT, 3),
	//	new ItemGroup(Items.snowball, Item.getItemFromBlock(Blocks.snow), 2),
	};
}
