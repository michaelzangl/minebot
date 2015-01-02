package net.famzangl.minecraft.aimbow.aiming;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

/**
 * This is an incremental colission solver.
 * <p>
 * It can simulate multiple entities and detect which one is hit.
 * <p>
 * It uses a list of rays.
 * 
 * @author michael
 *
 */
public class ColissionSolver {
	private final Minecraft minecraft;
	private final EntityLivingBase shootingEntity;

	private final ArrayList<BowRayData> simulated = new ArrayList<BowRayData>();
	private ArrayList<ColissionData> colissions;

	public ColissionSolver(Minecraft mc, EntityLivingBase renderViewEntity) {
		minecraft = mc;
		shootingEntity = renderViewEntity;
	}

	private void runTick(int tick) {
		for (BowRayData s : simulated) {
			if (s.isDead()) {
				continue;
			}
			s.moveTick();

			Vec3 vec31 = Vec3.createVectorHelper(s.prevPosX, s.prevPosY, s.prevPosZ);
			Vec3 vec3 = Vec3.createVectorHelper(s.posX, s.posY, s.posZ);
			MovingObjectPosition hit = minecraft.theWorld.func_147447_a(vec31,
					vec3, false, true, false);

			vec31 = Vec3.createVectorHelper(s.prevPosX, s.prevPosY, s.prevPosZ);
			if (hit == null) {
				vec3 = Vec3.createVectorHelper(s.posX, s.posY
					, s.posZ);
			} else {
				vec3 = Vec3.createVectorHelper(hit.hitVec.xCoord,
						hit.hitVec.yCoord, hit.hitVec.zCoord);
			}

			double d0 = 0.0D;
			List<Entity> entities = minecraft.theWorld.getEntitiesWithinAABB(
					Entity.class,
					s.boundingBox.addCoord(s.motionX, s.motionY, s.motionZ)
							.expand(1.0D, 1.0D, 1.0D));
			for (Entity e : entities) {
				if (e.canBeCollidedWith()
						&& (e != this.shootingEntity || tick >= 5)) {
					float f1 = 0.3F;
					AxisAlignedBB axisalignedbb1 = e.boundingBox.expand(f1, f1,
							f1);
					MovingObjectPosition myHit = axisalignedbb1
							.calculateIntercept(vec31, vec3);

					if (myHit != null) {
						double d1 = vec31.distanceTo(myHit.hitVec);

						if (d1 < d0 || d0 == 0.0D) {
							hit = myHit;
							hit.entityHit = e;
							d0 = d1;
						}
					}
				}
			}

			if (hit != null) {
//				System.out.println("Hit: " + hit.entityHit + " at " + hit.hitVec.xCoord + ","+
//						hit.hitVec.yCoord +"," + hit.hitVec.zCoord + "," + hit.typeOfHit);
				colissions.add(new ColissionData(hit.hitVec.xCoord,
						hit.hitVec.yCoord, hit.hitVec.zCoord, hit.entityHit, tick));
				s.setDead(true);
			}

		}
	}

	private void generateRays(EntityLivingBase shootingEntity) {
		simulated.clear();
		BowRayData data = new BowRayData();
		data.shootFrom(shootingEntity, 2);
		simulated.add(data);
	}

	public ArrayList<ColissionData> computeCurrentColissionPoints() {
		colissions = new ArrayList<ColissionData>();
		generateRays(minecraft.renderViewEntity);
		run();
		return colissions;
	}

	public ArrayList<ColissionData> computeColissionWithLook(Vec3 look) {
		colissions = new ArrayList<ColissionData>();
		simulated.clear();
		BowRayData data = new BowRayData();
		data.shootFromTowards(shootingEntity, 2, look);
		simulated.add(data);
		run();
		return colissions;
	}

	private void run() {
		for (int i = 0; i < 100; i++) {
			runTick(i);
		}
	}
}
