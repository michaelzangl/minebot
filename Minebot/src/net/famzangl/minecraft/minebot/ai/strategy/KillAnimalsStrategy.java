package net.famzangl.minecraft.minebot.ai.strategy;

import java.util.HashSet;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.animals.AnimalyType;
import net.famzangl.minecraft.minebot.ai.selectors.AndSelector;
import net.famzangl.minecraft.minebot.ai.selectors.ItemSelector;
import net.famzangl.minecraft.minebot.ai.selectors.NotSelector;
import net.famzangl.minecraft.minebot.ai.selectors.OrSelector;
import net.famzangl.minecraft.minebot.ai.selectors.OwnTameableSelector;
import net.famzangl.minecraft.minebot.ai.selectors.XPOrbSelector;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;

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

	public KillAnimalsStrategy() {
		this(-1, AnimalyType.ANY);
	}

	public KillAnimalsStrategy(int maxKills, AnimalyType type) {
		this.maxKills = maxKills;
		this.type = type;
	}

	@Override
	public boolean checkShouldTakeOver(AIHelper helper) {
		return super.checkShouldTakeOver(helper);
	}

	@Override
	protected TickResult onGameTick(AIHelper helper) {
		return super.onGameTick(helper);
	}

	@Override
	protected IEntitySelector entitiesToInteract(AIHelper helper) {
		if (maxKillsReached(helper)) {
			return new IEntitySelector() {
				@Override
				public boolean isEntityApplicable(Entity var1) {
					return false;
				}
			};
		} else {
			return new AndSelector(new KillableSelector(), new NotSelector(
					new OwnTameableSelector(helper.getMinecraft().thePlayer)));
		}
	}

	@Override
	protected IEntitySelector entitiesToFace(AIHelper helper) {
		return new OrSelector(super.entitiesToFace(helper), new ItemSelector(), new XPOrbSelector());
	}

	@Override
	protected void doInteract(Entity entityHit, AIHelper helper) {
		hitEntities.add(entityHit);
		helper.overrideAttack();
	}

	public boolean maxKillsReached(final AIHelper helper) {
		if (maxKills >= 0) {
			int kills = countKills();
			if (kills >= maxKills) {
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
		return "Killing";
	}

}
