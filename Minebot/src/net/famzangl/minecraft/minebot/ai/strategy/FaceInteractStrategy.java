package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

public abstract class FaceInteractStrategy extends AIStrategy {

	private static final int DISTANCE = 20;
	private int ticksRun;

	@Override
	public boolean checkShouldTakeOver(AIHelper helper) {
		return getCloseEntity(helper) != null;
	}

	@Override
	protected TickResult onGameTick(AIHelper helper) {

		final Entity found = getCloseEntity(helper);

		if (found == null) {
			ticksRun = 0;
			return TickResult.NO_MORE_WORK;
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
			i.jump = speed < 0.01 && ticksRun > 8;
			i.moveForward = 1;
			helper.overrideMovement(i);
			ticksRun++;
		}
		return TickResult.TICK_HANDLED;
	}

	private boolean doInteractWithCurrent(Entity entityHit, AIHelper helper) {
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
		final IEntitySelector collect = entitiesToFace(helper);

		final Entity found = helper.getClosestEntity(DISTANCE, collect);
		return found;
	}

	protected abstract IEntitySelector entitiesToInteract(AIHelper helper);

	protected IEntitySelector entitiesToFace(AIHelper helper) {
		return entitiesToInteract(helper);
	}
}
