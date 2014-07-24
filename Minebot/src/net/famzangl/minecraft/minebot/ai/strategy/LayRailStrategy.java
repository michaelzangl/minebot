package net.famzangl.minecraft.minebot.ai.strategy;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.AIStrategyFactory;
import net.famzangl.minecraft.minebot.ai.path.LayRailPathFinder;
import net.minecraft.util.Vec3;

public class LayRailStrategy implements AIStrategyFactory {

	@Override
	public AIStrategy produceStrategy(AIHelper helper) {
		final Pos p = helper.getPlayerPosition();
		final Vec3 lookVec = helper.getMinecraft().thePlayer.getLookVec();
		int dx = 0, dz = 0;
		if (Math.abs(lookVec.xCoord) > Math.abs(lookVec.zCoord)) {
			dx = (int) Math.signum(lookVec.xCoord);
		} else {
			dz = (int) Math.signum(lookVec.zCoord);
		}

		return new PathFinderStrategy(new LayRailPathFinder(helper, dx, dz,
				p.x, p.y, p.z), "Building a railway");
	}
}
