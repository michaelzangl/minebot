package net.famzangl.minecraft.minebot.ai.strategy;

import java.util.ArrayList;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.selectors.AndSelector;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

public abstract class FaceInteractStrategy extends AIStrategy {

	private final class NotOnBlacklistSelector implements
			IEntitySelector {
		@Override
		public boolean isEntityApplicable(Entity var1) {
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
		if (entitiesToInteract(helper).isEntityApplicable(entityHit)) {
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
		IEntitySelector collect = entitiesToFace(helper);
		collect = new AndSelector(collect, new NotOnBlacklistSelector());
		return helper.getClosestEntity(DISTANCE, collect);
	}

	protected abstract IEntitySelector entitiesToInteract(AIHelper helper);

	protected IEntitySelector entitiesToFace(AIHelper helper) {
		return entitiesToInteract(helper);
	}
}
