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
		new ItemGroup(Items.wheat, Item.getItemFromBlock(Blocks.hay_block), 3),
		new ItemGroup(Items.coal, Item.getItemFromBlock(Blocks.coal_block), 3),
		new ItemGroup(Items.redstone, Item.getItemFromBlock(Blocks.redstone_block), 3),
		new ItemGroup(Items.emerald, Item.getItemFromBlock(Blocks.emerald_block), 3),
		new ItemGroup(Items.diamond, Item.getItemFromBlock(Blocks.diamond_block), 3),
		new ItemGroup(Items.iron_ingot, Item.getItemFromBlock(Blocks.iron_block), 3),
		new ItemGroup(Items.gold_ingot, Item.getItemFromBlock(Blocks.gold_block), 3),
		new ItemGroup(Items.gold_nugget, Items.gold_ingot, 3),
	//	new ItemGroup(Items.snowball, Item.getItemFromBlock(Blocks.snow), 2),
	};
}
