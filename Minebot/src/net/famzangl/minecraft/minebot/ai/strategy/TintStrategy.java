package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.selectors.AndSelector;
import net.famzangl.minecraft.minebot.ai.selectors.ColorSelector;
import net.famzangl.minecraft.minebot.ai.selectors.NotSelector;
import net.famzangl.minecraft.minebot.ai.selectors.OrSelector;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.FaceAndInteractTask;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;

public class TintStrategy implements AIStrategy {
	private static final int DISTANCE = 20;
	private final int color;
	private final int current;
	private final TintType type;

	public TintStrategy(int color, TintType type, int current) {
		this.color = color;
		this.type = type;
		this.current = current;
	}

	private final class SheepSelector implements IEntitySelector {
		@Override
		public boolean isEntityApplicable(Entity var1) {
			return var1 instanceof EntitySheep;
		}
	}

	private final class WolfSelector implements IEntitySelector {
		private final EntityClientPlayerMP owner;

		private WolfSelector(EntityClientPlayerMP owner) {
			this.owner = owner;
		}

		@Override
		public boolean isEntityApplicable(Entity var1) {
			return var1 instanceof EntityWolf
					&& ((EntityWolf) var1).getOwner() == owner;
		}
	}

	public static enum TintType {
		WOLF,
		SHEEP,
		ANY;

	}

	private static class DyeItemFilter implements ItemFilter {

		private final int color;

		public DyeItemFilter(int color) {
			this.color = color;
		}

		@Override
		public boolean matches(ItemStack itemStack) {
			return itemStack != null && itemStack.getItem() != null
					&& itemStack.getItem() instanceof ItemDye
					&& (color < 0 || 15 - itemStack.getItemDamage() == color);
		}

	}

	@Override
	public void searchTasks(AIHelper helper) {
		if (!helper.selectCurrentItem(new DyeItemFilter(color))) {
			return;
		}

		final EntityClientPlayerMP owner = helper.getMinecraft().thePlayer;
		int holdingColor = 15 - owner.inventory.getCurrentItem().getItemDamage();
		IEntitySelector wolfSelector = new WolfSelector(owner);
		IEntitySelector sheepSelector = new SheepSelector();

		IEntitySelector selector;
		switch (type) {
		case WOLF:
			selector = wolfSelector;
			break;
		case SHEEP:
			selector = sheepSelector;
			break;
		default:
			selector = new OrSelector(sheepSelector, wolfSelector);
		}
		if (current >= 0) {
			selector = new AndSelector(selector, new ColorSelector(current));
		}
		selector = new AndSelector(selector, new NotSelector(new ColorSelector(
				holdingColor)));

		final Entity found = helper.getClosestEntity(DISTANCE, selector);

		if (found != null) {
			helper.addTask(new FaceAndInteractTask(found, selector));
		}
	}

	@Override
	public String getDescription() {
		return "Tinting...";
	}

	@Override
	public AITask getOverrideTask(AIHelper helper) {
		return null;
	}

	@Override
	public String toString() {
		return "TintStrategy [color=" + color + ", current=" + current
				+ ", type=" + type + "]";
	}
	
	

}
