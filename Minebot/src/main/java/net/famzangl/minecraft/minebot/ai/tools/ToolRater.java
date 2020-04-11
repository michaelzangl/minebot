package net.famzangl.minecraft.minebot.ai.tools;

import com.google.gson.Gson;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.path.world.BlockFloatMap;
import net.famzangl.minecraft.minebot.ai.tools.rate.AndRater;
import net.famzangl.minecraft.minebot.ai.tools.rate.EnchantmentRater;
import net.famzangl.minecraft.minebot.ai.tools.rate.FilterRater;
import net.famzangl.minecraft.minebot.ai.tools.rate.MatchesRater;
import net.famzangl.minecraft.minebot.ai.tools.rate.MultiplyEnchantmentRater;
import net.famzangl.minecraft.minebot.ai.tools.rate.NotRater;
import net.famzangl.minecraft.minebot.ai.tools.rate.OrRater;
import net.famzangl.minecraft.minebot.ai.tools.rate.Rater;
import net.famzangl.minecraft.minebot.settings.MinebotSettings;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShearsItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class rates tools.
 * 
 * @author Michael Zangl
 *
 */
public class ToolRater {

	private static class IsEnchanted implements ItemFilter {
		@Override
		public boolean matches(ItemStack itemStack) {
			return itemStack != null && itemStack.isEnchanted();
		}

		@Override
		public String toString() {
			return "IsEnchanted []";
		}
	}

	private static class TakesDamage implements ItemFilter {
		@Override
		public boolean matches(ItemStack itemStack) {
			return itemStack != null && itemStack.isDamageable();
		}

		@Override
		public String toString() {
			return "TakesDamage []";
		}
	}

	private static class Depleted implements ItemFilter {
		@Override
		public boolean matches(ItemStack itemStack) {
			return itemStack != null
					&& itemStack.isDamageable()
					&& (itemStack.getMaxDamage() - itemStack.getDamage()) <= 1;
		}

		@Override
		public String toString() {
			return "Depleted []";
		}
	}

	private static class Hand implements ItemFilter {
		@Override
		public boolean matches(ItemStack itemStack) {
			return itemStack == null;
		}

		@Override
		public String toString() {
			return "Hand []";
		}

	}

	private static class ItemMaterial implements ItemFilter {
		private ItemTier toolMaterial;

		public ItemMaterial(ItemTier toolMaterial) {
			super();
			Objects.requireNonNull(toolMaterial, "toolMaterial");
			this.toolMaterial = toolMaterial;
		}

		@Override
		public boolean matches(ItemStack itemStack) {
			if (itemStack == null) {
				return false;
			} else if (itemStack.getItem() instanceof ToolItem) {
				ToolItem itemTool = (ToolItem) itemStack.getItem();
				return toolMaterial.equals(itemTool.getTier());
			} else if (itemStack.getItem() instanceof HoeItem) {
				HoeItem itemTool = (HoeItem) itemStack.getItem();
				return toolMaterial.equals(itemTool.getTier());
			} else {
				return false;
			}
		}

		@Override
		public String toString() {
			return "ItemMaterial [toolMaterial=" + toolMaterial + "]";
		}

	}

	public enum ToolType implements ItemFilter {
		AXE("axe", AxeItem.class),
		BOW("bow", BowItem.class),
		FISHING_ROD("fishingrod", FishingRodItem.class),
		HOE("hoe", HoeItem.class),
		PICKAXE("pickaxe", PickaxeItem.class),
		SHEARS("shears", ShearsItem.class),
		SPADE("shovel", ShovelItem.class),
		SWORD("sword", SwordItem.class);

		private final Class<? extends Item> itemClass;
		private final String name;

		ToolType(String name, Class<? extends Item> itemClass) {
			this.name = name;
			this.itemClass = itemClass;
		}

		@Override
		public boolean matches(ItemStack itemStack) {
			return itemStack != null && itemStack.getItem() != Items.AIR
					&& itemClass.isInstance(itemStack.getItem());
		}

		public String getName() {
			return name;
		}
	}

	private static final Hashtable<String, Enchantment> ENCHANTMENTS = new Hashtable<String, Enchantment>();

	static {
		ENCHANTMENTS.put("efficiency", Enchantments.EFFICIENCY);
		ENCHANTMENTS.put("fortune", Enchantments.FORTUNE);
		ENCHANTMENTS.put("unbreaking", Enchantments.UNBREAKING);
		ENCHANTMENTS.put("silk_touch", Enchantments.SILK_TOUCH);
	}

	private static final Hashtable<String, ItemFilter> FILTERS = new Hashtable<String, ItemFilter>();

	static {
		FILTERS.put("is_enchanted", new IsEnchanted());
		FILTERS.put("takes_damage", new TakesDamage());
		FILTERS.put("depleted", new Depleted());
		FILTERS.put("hand", new Hand());
		FILTERS.put("wood", new ItemMaterial(ItemTier.WOOD));
		FILTERS.put("stone", new ItemMaterial(ItemTier.STONE));
		FILTERS.put("gold", new ItemMaterial(ItemTier.GOLD));
		FILTERS.put("iron", new ItemMaterial(ItemTier.IRON));
		FILTERS.put("diamond", new ItemMaterial(ItemTier.DIAMOND));
		for (ToolType tt : ToolType.values()) {
			FILTERS.put(tt.getName(), tt);
		}
	}

	public ToolRater() {
	}

	public ToolRater(ToolType requiredToolType) {
		BlockFloatMap allBad = new BlockFloatMap();
		allBad.setDefault(0);
		raters.add(new NotRater(allBad, new FilterRater(requiredToolType,
				requiredToolType.getName(), allBad)));
	}

	private static Rater getCompoundRater(String name, BlockFloatMap values) {
		// todo: advanced boolean parsing, parentheses, ...
		String[] orPartStrings = name.split("\\s*\\|\\s*");
		Rater[] orParts = new Rater[orPartStrings.length];
		for (int i = 0; i < orParts.length; i++) {
			orParts[i] = getCompoundAndRater(values, orPartStrings[i]);
		}
		return new OrRater(values, orParts);
	}

	private static AndRater getCompoundAndRater(BlockFloatMap values,
			String string) {
		String[] parts = string.split("\\s*\\&\\s*");
		Rater[] raters = new Rater[parts.length];
		for (int j = 0; j < raters.length; j++) {
			raters[j] = createRaterWithNot(parts[j], values);
		}
		AndRater andRater = new AndRater(values, raters);
		return andRater;
	}

	private static Rater createRaterWithNot(String string, BlockFloatMap values) {
		Matcher match = Pattern.compile("(\\!?)\\s*(\\w+)").matcher(string);
		if (!match.matches()) {
			throw new IllegalArgumentException("Could not match filter: "
					+ string);
		}

		Rater rater = createRater(match.group(2), values);
		if (!match.group(1).isEmpty()) {
			rater = new NotRater(values, rater);
		}
		return rater;
	}

	private static Rater createRater(String name, BlockFloatMap values) {
		if ("matches".equals(name)) {
			return new MatchesRater(name, values);
		}

		Enchantment enchantmentId = ENCHANTMENTS.get(name);
		if (enchantmentId != null) {
			return new MultiplyEnchantmentRater(enchantmentId, name, values);
		}

		if (name.endsWith("_present")) {
			enchantmentId = ENCHANTMENTS.get(name.replace("_present", ""));
			if (enchantmentId != null) {
				return new EnchantmentRater(enchantmentId, name, values);
			}
		}

		ItemFilter filter = FILTERS.get(name);
		if (filter != null) {
			return new FilterRater(filter, name, values);
		}

		throw new IllegalArgumentException("Could not find rater: " + name);
	}

	private final ArrayList<Rater> raters = new ArrayList<Rater>();

	public void addRater(Rater rater) {
		raters.add(rater);
	}

	public void addRater(String name, BlockFloatMap values) {
		addRater(getCompoundRater(name, values));
	}

	public List<Rater> getRaters() {
		return Collections.unmodifiableList(raters);
	}

	public float rateTool(ItemStack stack, int forBlockAndMeta) {
		float f = 1;
		for (Rater rater : raters) {
			f *= rater.rate(stack, forBlockAndMeta);
		}
		return f;
	}

	@Override
	public String toString() {
		return "ToolRater [raters=" + raters + "]";
	}

	public static ToolRater createToolRaterFromJson(String json) {
		return createToolRaterByJson(new StringReader(json));
	}
	
	public static ToolRater createDefaultRater() {
		InputStream res = ToolRater.class.getResourceAsStream("tools.json");
		return createToolRaterByJson(new InputStreamReader(res));
	}

	private static ToolRater createToolRaterByJson(Reader reader) {
		Gson gson = MinebotSettings.getGson();
		return gson.fromJson(reader, ToolRater.class);
	}
}
