package net.famzangl.minecraft.minebot.ai.tools.rate;

import net.famzangl.minecraft.minebot.ai.path.world.BlockFloatMap;
import net.minecraft.item.ItemStack;

public abstract class Rater {

	protected final String name;

	protected final BlockFloatMap values;

	public Rater(String name, BlockFloatMap values) {
		super();
		this.name = name;
		this.values = values;
	}

	public final float ratePowed(ItemStack item, int forBlockAndMeta) {
		return (float) Math.pow(rate(item, forBlockAndMeta), getPow(item, forBlockAndMeta));
	}

	protected double getPow(ItemStack item, int forBlockAndMeta) {
		return 1;
	}

	public float rate(ItemStack item, int forBlockAndMeta) {
		if (isAppleciable(item, forBlockAndMeta)) {
			return forBlockAndMeta < 0 ? values.getDefaultValue() : values.get(forBlockAndMeta);
		} else {
			return 1;
		}
	}

	protected boolean isAppleciable(ItemStack item, int forBlockAndMeta) {
		return true;
	}

	public String getName() {
		return name;
	}
	
	public BlockFloatMap getValues() {
		return values;
	}

	@Override
	public String toString() {
		return "Rater [name=" + name + ", values=" + values + "]";
	}

	protected static String createName(Rater[] raters, String sep) {
		StringBuilder sb = new StringBuilder();
		for (Rater rater : raters) {
			if (sb.length() > 0) {
				sb.append(sep);
			}
			sb.append(rater.getName());
		}
		return sb.toString();
	}
}