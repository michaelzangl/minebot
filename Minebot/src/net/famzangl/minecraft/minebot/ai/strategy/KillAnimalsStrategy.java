package net.famzangl.minecraft.minebot.ai.strategy;

import java.util.HashSet;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.animals.AnimalyType;
import net.famzangl.minecraft.minebot.ai.selectors.AndSelector;
import net.famzangl.minecraft.minebot.ai.selectors.ColorSelector;
import net.famzangl.minecraft.minebot.ai.selectors.ItemSelector;
import net.famzangl.minecraft.minebot.ai.selectors.NotSelector;
import net.famzangl.minecraft.minebot.ai.selectors.OrSelector;
import net.famzangl.minecraft.minebot.ai.selectors.OwnTameableSelector;
import net.famzangl.minecraft.minebot.ai.selectors.XPOrbSelector;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

/**
 * Kills all animals in range by mving towards them and hitting them.
 * 
 * @author michael
 * 
 */
public class KillAnimalsStrategy extends FaceInteractStrategy {

	private final class KillableSelector implements IEntitySelector {
		@Override
		public boolean isEntityApplicable(Entity e) {
			if (!type.hasAnimalClass(e)) {
				return false;
			}

			return ((EntityAnimal) e).getGrowingAge() >= 0
					&& ((EntityAnimal) e).getHealth() > 0;
		}
	}

	private final int maxKills;
	private final AnimalyType type;
	private final HashSet<Entity> hitEntities = new HashSet<Entity>();
	private final int color;
	private int cooldown;
	private int lastKills;

	public KillAnimalsStrategy() {
		this(-1, AnimalyType.ANY, -1);
	}

	public KillAnimalsStrategy(int maxKills, AnimalyType type, int color) {
		this.maxKills = maxKills;
		this.type = type;
		this.color = color;
	}

	@Override
	protected IEntitySelector entitiesToInteract(AIHelper helper) {
		if (maxKillsReached()) {
			return new IEntitySelector() {
				@Override
				public boolean isEntityApplicable(Entity var1) {
					return false;
				}
			};
		} else {
			IEntitySelector s = new KillableSelector();
			if (color >= 0) {
				s = new AndSelector(new ColorSelector(color), s);
			}
			return new AndSelector(s, new NotSelector(new OwnTameableSelector(
					helper.getMinecraft().thePlayer)));
		}
	}

	@Override
	protected IEntitySelector entitiesToFace(AIHelper helper) {
		return new OrSelector(super.entitiesToFace(helper), new ItemSelector(),
				new XPOrbSelector());
	}

	@Override
	protected boolean doInteractWithCurrent(Entity entityHit, AIHelper helper) {
		if (cooldown > 0) {
			cooldown--;
			return false;
		} else {
			helper.selectCurrentItem(new ItemFilter() {
				@Override
				public boolean matches(ItemStack itemStack) {
					return itemStack != null && itemStack.getItem() instanceof ItemSword;
				}
			});
			boolean interacted = super.doInteractWithCurrent(entityHit, helper);
			if (interacted) {
				cooldown = 5;
			}
			return interacted;
		}
	}

	@Override
	protected void doInteract(Entity entityHit, AIHelper helper) {
		hitEntities.add(entityHit);
		helper.overrideAttack();
	}

	public boolean maxKillsReached() {
		if (maxKills >= 0) {
			lastKills = countKills();
			if (lastKills >= maxKills) {
				return true;
			}
		}
		return false;
	}

	private int countKills() {
		int kills = 0;
		for (Entity e : hitEntities) {
			if (e instanceof EntityAnimal
					&& ((EntityAnimal) e).getHealth() <= 0) {
				kills++;
			}
		}
		return kills;
	}

	@Override
	public String getDescription(AIHelper helper) {
		if (maxKills < 0) {
			return "Killing";
		} else {
			return "Killing " + lastKills + "/" + maxKills;
		}
	}

	@Override
	public boolean hasFailed() {
		return maxKills >= 0 && !maxKillsReached();
	}
}
