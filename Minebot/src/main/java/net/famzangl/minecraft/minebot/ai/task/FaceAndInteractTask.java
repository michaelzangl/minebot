/*******************************************************************************
 * This file is part of Minebot.
 *
 * Minebot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Minebot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Minebot.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;

import java.util.function.Predicate;

/**
 * Try to interact with a given entity. If it is not clickable, the bot attempts
 * to face it and walk towards it (without any danger checking, jumping it it
 * fins objects in the way.). Stops as soon as it interacts with ANY entity.
 * 
 * @author michael
 *
 */
public class FaceAndInteractTask extends AITask {

	protected boolean interacted = false;
	private final Entity preferedAnimal;
	private final Predicate<Entity> alsoAcceptedAnimal;
	private final boolean doRightClick;
	private int ticksRun = 0;
	protected boolean wasJumping;

	public FaceAndInteractTask(Entity preferedAnimal,
			Predicate<Entity> alsoAcceptedAnimal) {
		this(preferedAnimal, alsoAcceptedAnimal, true);
	}

	/**
	 * Creates a new {@link FaceAndInteractTask}
	 * 
	 * @param preferedAnimal
	 *            The Animal to walk to and click at or the entity to walk to
	 *            (if it is not clickable).
	 * @param alsoAcceptedAnimal
	 *            A predicate that checks if any other animal the bot might
	 *            encounter also matches the criteria and should be clicked.
	 * @param doRightClick
	 *            <code>true</code> for right click, <code>false</code> for left
	 *            click (or whatever their actual key bindings are)
	 */
	public FaceAndInteractTask(Entity preferedAnimal,
			Predicate<Entity> alsoAcceptedAnimal, boolean doRightClick) {
		this.preferedAnimal = preferedAnimal;
		this.alsoAcceptedAnimal = alsoAcceptedAnimal;
		this.doRightClick = doRightClick;
	}

	@Override
	public boolean isFinished(AIHelper aiHelper) {
		final boolean collect = preferedAnimal instanceof ItemEntity
				|| preferedAnimal instanceof ExperienceOrbEntity;
		return collect ? preferedAnimal.getBoundingBox().intersects(
				aiHelper.getMinecraft().player.getBoundingBox()) : interacted;
	}

	@Override
	public void runTick(AIHelper aiHelper, TaskOperations taskOperations) {
		final RayTraceResult position = aiHelper.getObjectMouseOver();
		if (ticksRun > 2 && position != null
				&& position.getType() == RayTraceResult.Type.ENTITY
				// We interact with any accepted entity, gives good results in crowded places
				&& alsoAcceptedAnimal.test(((EntityRayTraceResult)position).getEntity())) {
			doInteractWithCurrent(aiHelper);
		} else {
			LOGGER.debug("Attempting to face entitiy {}", preferedAnimal);
			double x = aiHelper.getMinecraft().player.getMotion().x;
			double z = aiHelper.getMinecraft().player.getMotion().z;
			final double speed = x * x + z * z;
			aiHelper.face(preferedAnimal.getPosX(), preferedAnimal.getPosY(),
					preferedAnimal.getPosZ());
			final MovementInput movement = new MovementInput();
			// jump if we are stuck, e.g. a wall or inside a crowd of entities
			movement.jump = speed < 0.01 && ticksRun > 8;
			movement.moveForward = 1;
			aiHelper.overrideMovement(movement);
		}
		ticksRun++;
	}

	/**
	 * Interacts with the current animal by either right or leftclicking.
	 * 
	 * @param aiHelper
	 */
	protected void doInteractWithCurrent(AIHelper aiHelper) {
		if (doRightClick) {
			LOGGER.debug("Interacting using the use item key");
			aiHelper.overrideUseItem();
		} else {
			LOGGER.debug("Interacting using the attack key");
			aiHelper.overrideAttack();
		}
		interacted = true;
	}

	@Override
	public String toString() {
		return "FaceAndInteractTask [preferedAnimal=" + preferedAnimal
				+ ", alsoAcceptedAnimal=" + alsoAcceptedAnimal
				+ ", doRightClick=" + doRightClick + "]";
	}

}
