package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;

/**
 * Prevents a suffocation in walls because of server lags.
 * 
 * @author michael
 * 
 */
public class DoNotSuffocateStrategy extends AIStrategy {

	@Override
	public boolean takesOverAnyTime() {
		return true;
	}
	
	@Override
	public boolean checkShouldTakeOver(AIHelper helper) {
		Pos p = helper.getPlayerPosition();
		return (!safeGround(helper, p) || !safeHead(helper, p));
	}
	
	@Override
	protected TickResult onGameTick(AIHelper helper) {
		Pos p = helper.getPlayerPosition();
		if (!safeGround(helper, p)) {
			helper.faceAndDestroy(p);
			return TickResult.TICK_HANDLED;
		} else if (!safeHead(helper, p)) {
			helper.faceAndDestroy(p.add(0,1,0));
			return TickResult.TICK_HANDLED;
		}
		return TickResult.NO_MORE_WORK;
	}

	private boolean safeHead(AIHelper helper, Pos p) {
		return helper.canWalkThrough(helper.getBlock(p.getX(), p.getY() + 1, p.getZ()));
	}

	private boolean safeGround(AIHelper helper, Pos p) {
		return helper.canWalkOn(helper.getBlock(p.getX(), p.getY(), p.getZ()));
	}
	
	@Override
	public String getDescription(AIHelper helper) {
		return "Do not suffocate in walls.";
	}

}
