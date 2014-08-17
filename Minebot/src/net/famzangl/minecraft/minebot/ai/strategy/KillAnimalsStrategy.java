package net.famzangl.minecraft.minebot.ai.strategy;

import java.util.HashSet;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.animals.AnimalyType;
import net.famzangl.minecraft.minebot.ai.selectors.AndSelector;
import net.famzangl.minecraft.minebot.ai.selectors.ItemSelector;
import net.famzangl.minecraft.minebot.ai.selectors.NotSelector;
import net.famzangl.minecraft.minebot.ai.selectors.OrSelector;
import net.famzangl.minecraft.minebot.ai.selectors.OwnTameableSelector;
import net.famzangl.minecraft.minebot.ai.task.FaceAndInteractTask;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;

/**
 * Kills all animals in range by mving towards them and hitting them.
 * 
 * @author michael
 * 
 */
public class KillAnimalsStrategy extends TaskStrategy {

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

	private static final int DISTANCE = 20;
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
	public void searchTasks(final AIHelper helper) {
		if (maxKills >= 0) {
			int kills = countKills();
			if (kills >= maxKills) {
				return;
			}
		}

		final IEntitySelector selector = new AndSelector(
				new KillableSelector(),
				new NotSelector(new OwnTameableSelector(
						helper.getMinecraft().thePlayer)));

		final IEntitySelector collect = new ItemSelector();

		final Entity found = helper.getClosestEntity(DISTANCE, new OrSelector(
				collect, selector));

		if (found != null) {
			addTask(new FaceAndInteractTask(found, selector, false) {
				@Override
				protected void doInteractWithCurrent(AIHelper h) {
					hitEntities.add(h.getObjectMouseOver().entityHit);
					super.doInteractWithCurrent(h);
				}
			});
		}
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
	public String getDescription() {
		return "Killing";
	}

}
