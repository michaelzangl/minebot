package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.selectors.AndSelector;
import net.famzangl.minecraft.minebot.ai.selectors.ColorSelector;
import net.famzangl.minecraft.minebot.ai.selectors.ItemSelector;
import net.famzangl.minecraft.minebot.ai.selectors.OrSelector;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;

public class ShearStrategy extends FaceInteractStrategy {
	private final int color;

	public ShearStrategy(int color) {
		this.color = color;
	}

	private final class ShearsFilter implements ItemFilter {
		@Override
		public boolean matches(ItemStack itemStack) {
			return itemStack != null
					&& itemStack.getItem() instanceof ItemShears;
		}
	}

	private final class SheepSelector implements IEntitySelector {
		@Override
		public boolean isEntityApplicable(Entity var1) {
			return var1 instanceof EntitySheep
					&& ((EntitySheep) var1).isShearable(null, null, 0, 0, 0);
		}
	}
	
	@Override
	protected IEntitySelector entitiesToInteract(AIHelper helper) {
		IEntitySelector selector = new SheepSelector();

		if (color >= 0) {
			selector = new AndSelector(selector, new ColorSelector(color));
		}
		return selector;
	}
	@Override
	protected IEntitySelector entitiesToFace(AIHelper helper) {
		return new OrSelector(super.entitiesToFace(helper), new ItemSelector());
	}
	
	@Override
	public boolean checkShouldTakeOver(AIHelper helper) {
		return super.checkShouldTakeOver(helper) && helper.canSelectItem(new ShearsFilter());
	}
	
	@Override
	protected TickResult onGameTick(AIHelper helper) {
		if (!helper.canSelectItem(new ShearsFilter())) {
			return TickResult.NO_MORE_WORK;
		}
		return super.onGameTick(helper);
	}
	
	@Override
	protected void doInteract(Entity entityHit, AIHelper helper) {
		helper.selectCurrentItem(new ShearsFilter());
		super.doInteract(entityHit, helper);
	}

	@Override
	public String getDescription(AIHelper helper) {
		return "Shearing...";
	}

}
