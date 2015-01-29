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
package net.famzangl.minecraft.minebot.ai.enchanting;

import java.util.List;
import java.util.Random;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.AxisAlignedBB;

import com.google.common.base.Predicate;

public class FaceAnyMobTask extends AITask {
	private final class LivingSelector implements Predicate<Entity> {
		@Override
		public boolean apply(Entity var1) {
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
			final AxisAlignedBB ebb = e.getEntityBoundingBox();
			h.face((ebb.maxX + ebb.minX) / 2, ebb.minY + 0.2,
					(ebb.maxZ + ebb.minZ) / 2);
		}
	}

}
