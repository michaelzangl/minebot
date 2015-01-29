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

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class ThrowableColissionSolver extends ColissionSolver{

	public ThrowableColissionSolver(Minecraft mc, EntityLivingBase renderViewEntity) {
		super(mc, renderViewEntity);
	}

	@Override
	protected MovingObjectPosition computeHit(RayData s, int tick) {
        Vec3 vec3 = new Vec3(s.posX, s.posY, s.posZ);
        Vec3 vec31 = new Vec3(s.posX + s.motionX, s.posY + s.motionY, s.posZ + s.motionZ);
        MovingObjectPosition movingobjectposition = minecraft.theWorld.rayTraceBlocks(vec3, vec31);
        vec3 = new Vec3(s.posX, s.posY, s.posZ);
        vec31 = new Vec3(s.posX + s.motionX, s.posY + s.motionY, s.posZ + s.motionZ);

        if (movingobjectposition != null)
        {
            vec31 = new Vec3(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
        }

        Entity entity = null;
        List list = minecraft.theWorld.getEntitiesWithinAABB(Entity.class, s.boundingBox.addCoord(s.motionX, s.motionY, s.motionZ).expand(1.0D, 1.0D, 1.0D));
        double d0 = 0.0D;
        EntityLivingBase entitylivingbase = this.shootingEntity;

        for (int j = 0; j < list.size(); ++j)
        {
            Entity entity1 = (Entity)list.get(j);

            if (entity1.canBeCollidedWith() && (entity1 != entitylivingbase || tick >= 5))
            {
                float f = 0.3F;
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand((double)f, (double)f, (double)f);
                MovingObjectPosition movingobjectposition1 = axisalignedbb.calculateIntercept(vec3, vec31);

                if (movingobjectposition1 != null)
                {
                    double d1 = vec3.distanceTo(movingobjectposition1.hitVec);

                    if (d1 < d0 || d0 == 0.0D)
                    {
                        entity = entity1;
                        d0 = d1;
                    }
                }
            }
        }

        if (entity != null)
        {
            return new MovingObjectPosition(entity);
        } else {
        	return movingobjectposition;
        }
	}
	
	@Override
	public float getVelocity() {
		return 1.5f;
	}
	
	@Override
	protected RayData generateRayData() {
		return new ThrowableRayData();
	}

}
