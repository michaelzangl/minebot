package net.famzangl.minecraft.minebot.ai;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * Colored is ItemCloth
 * 
 * @author michael
 * 
 */
public class ColoredBlockItemFilter extends BlockItemFilter {
	public static final Block[] COLORABLE_BLOCKS = new Block[] { Blocks.wool,
			Blocks.stained_hardened_clay, Blocks.stained_glass,
			Blocks.stained_glass_pane, Blocks.carpet };
	private final int colorMeta;

	/**
	 * Right names for sheep wool and most blocks.
	 * <p>
	 * (15 - color) for dyes
	 */
	public static final String[] COLORS = new String[] { "White", "Orange",
			"Magenta", "LightBlue", "Yellow", "Lime", "Pink", "Gray",
			"LightGray", "Cyan", "Purple", "Blue", "Brown", "Green", "Red",
			"Black" };

	public ColoredBlockItemFilter(Block matched, String color) {
		this(matched, colorFromString(color));
	}

	private static int colorFromString(String color) {
		final String safeColor = color.replaceAll("[-_]", "");
		for (int i = 0; i < COLORS.length; i++) {
			if (COLORS[i].equalsIgnoreCase(safeColor)) {
				return i;
			}
		}
		System.out.println("Did not understand color: " + color);
		// TODO: warn?
		return 0;
	}

	public ColoredBlockItemFilter(Block matched, int color) {
		super(matched);
		colorMeta = color;
		if (!AIHelper.blockIsOneOf(matched, COLORABLE_BLOCKS)) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	protected boolean matchesItem(ItemStack itemStack, ItemBlock item) {
		return super.matchesItem(itemStack, item)
				&& itemStack.getItemDamage() == colorMeta;
	}

	@Override
	public String toString() {
		return "ColoredBlockItemFilter [colorMeta=" + colorMeta + ", "
				+ super.toString() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + colorMeta;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ColoredBlockItemFilter other = (ColoredBlockItemFilter) obj;
		if (colorMeta != other.colorMeta) {
			return false;
		}
		return true;
	}

	@Override
	public String getDescriptiveString() {
		return COLORS[colorMeta] + " " + super.getDescriptiveString();
	}

}
