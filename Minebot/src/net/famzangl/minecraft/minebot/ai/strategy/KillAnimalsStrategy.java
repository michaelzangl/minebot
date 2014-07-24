package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.animals.AnimalyType;
import net.famzangl.minecraft.minebot.ai.selectors.OrSelector;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.FaceAndInteractTask;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;

/**
 * Kills all animals in range by mving towards them and hitting them.
 * 
 * @author michael
 * 
 */
public class KillAnimalsStrategy implements AIStrategy {

	private final class KillableSelector implements
			IEntitySelector {
		private final AIHelper helper;

		private KillableSelector(AIHelper helper) {
			this.helper = helper;
		}

		@Override
		public boolean isEntityApplicable(Entity e) {
			if (!type.hasAnimalClass(e)) {
				return false;
			}
			if (e instanceof EntityTameable
					&& helper.getMinecraft().thePlayer
							.equals(((EntityTameable) e).getOwner())) {
				return false;
			}

			return ((EntityAnimal) e).getGrowingAge() >= 0
					&& ((EntityAnimal) e).getHealth() > 0;
		}
	}

	private final class ItemSelector implements
			IEntitySelector {
		@Override
		public boolean isEntityApplicable(Entity e) {
			return e instanceof EntityItem;
		}
	}

	private static final int DISTANCE = 20;
	private final int maxKills;
	private final AnimalyType type;

	public KillAnimalsStrategy() {
		this(-1, AnimalyType.ANY);
	}

	public KillAnimalsStrategy(int maxKills, AnimalyType type) {
		this.maxKills = maxKills;
		this.type = type;
	}

	@Override
	public void searchTasks(final AIHelper helper) {

		final IEntitySelector selector = new KillableSelector(helper);

		final IEntitySelector collect = new ItemSelector();

		final Entity found = helper.getClosestEntity(DISTANCE, new OrSelector(collect, selector));

		if (found != null) {
			helper.addTask(new FaceAndInteractTask(found, selector, false));
		}
	}

	@Override
	public String getDescription() {
		return "Killing";
	}

	@Override
	public AITask getOverrideTask(AIHelper helper) {
		return null;
	}

}
