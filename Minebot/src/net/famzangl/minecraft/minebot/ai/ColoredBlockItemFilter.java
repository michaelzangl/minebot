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
package net.famzangl.minecraft.minebot.ai;

import net.famzangl.minecraft.minebot.ai.command.BlockWithData;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;

/**
 * Colored is ItemCloth
 * 
 * @author michael
 * 
 */
public class ColoredBlockItemFilter extends BlockItemFilter {
	public static final BlockSet COLORABLE_BLOCKS = new BlockSet(Blocks.wool,
			Blocks.stained_hardened_clay, Blocks.stained_glass,
			Blocks.stained_glass_pane, Blocks.carpet);

	// /**
	// * Right names for sheep wool and most blocks.
	// * <p>
	// * (15 - color) for dyes
	// */
	// public static final String[] COLORS = new String[] { "White", "Orange",
	// "Magenta", "LightBlue", "Yellow", "Lime", "Pink", "Gray",
	// "LightGray", "Cyan", "Purple", "Blue", "Brown", "Green", "Red",
	// "Black" };

	public ColoredBlockItemFilter(Block matched, String color) {
		this(matched, colorFromString(color));
	}

	public static EnumDyeColor colorFromString(String color) {
		EnumDyeColor res = colorFromStringNull(color);
		if (res == null) {
			throw new IllegalArgumentException("Unknown color: " + color);
		}
		return res;
	}

	public static EnumDyeColor colorFromStringNull(String color) {
		for (EnumDyeColor v : EnumDyeColor.values()) {
			if (v.getName().equalsIgnoreCase(color)) {
				return v;
			}
		}

		return null;
	}

	public ColoredBlockItemFilter(Block matched, EnumDyeColor color) {
		super(new BlockWithData(matched, color.getMetadata()));
		if (COLORABLE_BLOCKS.contains(matched)) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public String toString() {
		return "ColoredBlockItemFilter ["
				+ super.toString() + "]";
	}

}
