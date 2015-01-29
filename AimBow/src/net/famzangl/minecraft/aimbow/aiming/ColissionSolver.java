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
package net.famzangl.minecraft.aimbow.aiming;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public abstract class ColissionSolver {

	protected final Minecraft minecraft;
	protected final EntityLivingBase shootingEntity;
	private final ArrayList<RayData> simulated = new ArrayList<RayData>();
	private ArrayList<ColissionData> colissions;

	public ColissionSolver(Minecraft mc, EntityLivingBase renderViewEntity) {
		super();
		minecraft = mc;
		shootingEntity = renderViewEntity;
	}

	private void runTick(int tick) {
		for (RayData s : simulated) {
			if (s.isDead()) {
				continue;
			}
			s.moveTick();

			MovingObjectPosition hit = computeHit(s, tick);
			if (hit != null) {
				// System.out.println("Hit: " + hit.entityHit + " at " +
				// hit.hitVec.xCoord + ","+
				// hit.hitVec.yCoord +"," + hit.hitVec.zCoord + "," +
				// hit.typeOfHit);
				colissions.add(new ColissionData(hit.hitVec.xCoord,
						hit.hitVec.yCoord, hit.hitVec.zCoord, hit.entityHit,
						tick));
				s.setDead(true);
			}

		}
	}

	protected abstract MovingObjectPosition computeHit(RayData s, int tick);

	private void generateRays(Entity entity) {
		simulated.clear();
		RayData data = generateRayData();
		data.shootFrom(entity);
		simulated.add(data);
	}

	public ArrayList<ColissionData> computeCurrentColissionPoints() {
		colissions = new ArrayList<ColissionData>();
		generateRays(minecraft.getRenderViewEntity());
		run();
		return colissions;
	}

	public ArrayList<ColissionData> computeColissionWithLook(Vec3 look) {
		colissions = new ArrayList<ColissionData>();
		simulated.clear();
		RayData data = generateRayData();
		data.shootFromTowards(shootingEntity, look);
		simulated.add(data);
		run();
		return colissions;
	}

	protected abstract RayData generateRayData();

	private void run() {
		for (int i = 0; i < 200; i++) {
			runTick(i);
		}
	}

	public static ColissionSolver forItem(ItemStack heldItem, Minecraft mc) {
		if (heldItem == null) {
			return null;
		} else if (heldItem.getItem() == Items.snowball || heldItem.getItem() == Items.egg) {
			return new ThrowableColissionSolver(mc, (EntityLivingBase) mc.getRenderViewEntity());
		} else if (heldItem.getItem() == Items.bow) {
			return new BowColissionSolver(mc, (EntityLivingBase) mc.getRenderViewEntity());
		}
		return null;
	}

	public float getGravity() {
		return generateRayData().getGravity();
	}

	public abstract float getVelocity();

}