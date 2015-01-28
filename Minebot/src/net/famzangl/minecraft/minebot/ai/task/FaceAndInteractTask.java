package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.strategy.TaskOperations;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

import com.google.common.base.Predicate;

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

	public FaceAndInteractTask(Entity preferedAnimal,
			Predicate<Entity> alsoAcceptedAnimal, boolean doRightClick) {
		this.preferedAnimal = preferedAnimal;
		this.alsoAcceptedAnimal = alsoAcceptedAnimal;
		this.doRightClick = doRightClick;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		final boolean collect = preferedAnimal instanceof EntityItem
				|| preferedAnimal instanceof EntityXPOrb;
		return collect ? preferedAnimal.getEntityBoundingBox().intersectsWith(h
				.getMinecraft().thePlayer.getEntityBoundingBox()) : interacted;
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		final MovingObjectPosition over = h.getObjectMouseOver();
		if (ticksRun > 2 && over != null && over.typeOfHit == MovingObjectType.ENTITY
				&& alsoAcceptedAnimal.apply(over.entityHit)) {
			doInteractWithCurrent(h);
		} else {
			final double speed = h.getMinecraft().thePlayer.motionX
					* h.getMinecraft().thePlayer.motionX
					+ h.getMinecraft().thePlayer.motionZ
					* h.getMinecraft().thePlayer.motionZ;
			h.face(preferedAnimal.posX, preferedAnimal.posY,
					preferedAnimal.posZ);
			final MovementInput i = new MovementInput();
			i.jump =  speed < 0.01 && ticksRun > 8;
			i.moveForward = 1;
			h.overrideMovement(i);
		}
		ticksRun++;
	}

	protected void doInteractWithCurrent(AIHelper h) {
		if (doRightClick) {
			h.overrideUseItem();
		} else {
			h.overrideAttack();
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
