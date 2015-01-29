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
package net.famzangl.minecraft.minebot.ai.strategy;

import java.util.ArrayList;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.selectors.AndSelector;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

import com.google.common.base.Predicate;

public abstract class FaceInteractStrategy extends AIStrategy {

	private final class NotOnBlacklistSelector implements
			Predicate<Entity> {
		@Override
		public boolean apply(Entity var1) {
			return !blacklist.contains(var1);
		}
	}

	private static final int DISTANCE = 20;
	private int ticksRun;
	private int ticksSlow;
	private Entity lastFound;
	private final ArrayList<Entity> blacklist = new ArrayList<Entity>();

	@Override
	public boolean checkShouldTakeOver(AIHelper helper) {
		return getCloseEntity(helper) != null;
	}

	@Override
	protected TickResult onGameTick(AIHelper helper) {

		final Entity found = getCloseEntity(helper);

		if (found == null) {
			ticksRun = 0;
			ticksSlow = 0;
			lastFound = null;
			return TickResult.NO_MORE_WORK;
		} else if (lastFound != found) {
			ticksRun = 0;
			ticksSlow = 0;
		}

		final MovingObjectPosition over = helper.getObjectMouseOver();
		if (over != null && over.typeOfHit == MovingObjectType.ENTITY
				&& doInteractWithCurrent(over.entityHit, helper)) {
			ticksRun = 0;
		} else {
			final double speed = helper.getMinecraft().thePlayer.motionX
					* helper.getMinecraft().thePlayer.motionX
					+ helper.getMinecraft().thePlayer.motionZ
					* helper.getMinecraft().thePlayer.motionZ;
			helper.face(found.posX, found.posY, found.posZ);
			final MovementInput i = new MovementInput();
			if (speed < 0.01 && ticksRun > 8) {
				i.jump = ++ticksSlow > 5;
			} else {
				ticksSlow = 0;
			}
			i.moveForward = 1;
			helper.overrideMovement(i);
			ticksRun++;
			if (ticksSlow > 3 * 20 || ticksRun > 20 * 20) {
				blacklist .add(found);
			}
		}
		lastFound = found;
		return TickResult.TICK_HANDLED;
	}

	protected boolean doInteractWithCurrent(Entity entityHit, AIHelper helper) {
		if (entitiesToInteract(helper).apply(entityHit)) {
			doInteract(entityHit, helper);
			return true;
		} else {
			return false;
		}
	}

	protected void doInteract(Entity entityHit, AIHelper helper) {
		helper.overrideUseItem();
	}

	private Entity getCloseEntity(AIHelper helper) {
		Predicate<Entity> collect = entitiesToFace(helper);
		collect = new AndSelector(collect, new NotOnBlacklistSelector());
		return helper.getClosestEntity(DISTANCE, collect);
	}

	protected abstract Predicate<Entity> entitiesToInteract(AIHelper helper);

	protected Predicate<Entity> entitiesToFace(AIHelper helper) {
		return entitiesToInteract(helper);
	}
}
