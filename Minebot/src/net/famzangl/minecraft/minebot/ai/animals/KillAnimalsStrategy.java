package net.famzangl.minecraft.minebot.ai.animals;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;

public class KillAnimalsStrategy implements AIStrategy {

	private static final int DISTANCE = 20;
	private int maxKills;
	private AnimalyType type;

	public KillAnimalsStrategy() {
		this(-1, AnimalyType.ANY);
	}

	public KillAnimalsStrategy(int maxKills, AnimalyType type) {
		this.maxKills = maxKills;
		this.type = type;
	}

	@Override
	public void searchTasks(final AIHelper helper) {

		final IEntitySelector selector = new IEntitySelector() {
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
		};

		IEntitySelector collect = new IEntitySelector() {
			@Override
			public boolean isEntityApplicable(Entity e) {
				return e instanceof EntityItem
						|| selector.isEntityApplicable(e);
			}
		};

		Entity found = helper.getClosestEntity(DISTANCE, collect);

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
