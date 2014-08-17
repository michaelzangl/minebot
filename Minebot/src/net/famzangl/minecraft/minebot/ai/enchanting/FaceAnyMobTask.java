package net.famzangl.minecraft.minebot.ai.enchanting;

import java.util.List;
import java.util.Random;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.strategy.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.minecraft.client.Minecraft;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.AxisAlignedBB;

public class FaceAnyMobTask extends AITask {
	private final class LivingSelector implements IEntitySelector {
		@Override
		public boolean isEntityApplicable(Entity var1) {
			// TODO: better filter
			return var1 instanceof EntityLiving;
		}
	}

	private static final int DIST = 3;
	int tickCount;

	@Override
	public boolean isFinished(AIHelper h) {
		return tickCount > 10;
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		tickCount++;
		if (tickCount > 10) {
			final Minecraft mc = h.getMinecraft();
			final List<Entity> entsInBBList = h.getEntities(DIST,
					new LivingSelector());
			if (entsInBBList.isEmpty()) {
				System.out.println("No entity in range");
				return;
			}
			System.out.println("Face next entity in range");
			final int n = new Random().nextInt(entsInBBList.size());
			final Entity e = entsInBBList.get(n);
			final AxisAlignedBB ebb = e.boundingBox;
			h.face((ebb.maxX + ebb.minX) / 2, ebb.minY + 0.2,
					(ebb.maxZ + ebb.minZ) / 2);
		}
	}

}
