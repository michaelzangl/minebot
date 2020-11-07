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
package net.famzangl.minecraft.minebot.ai.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.build.BuildManager;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.event.TickEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

@ParametersAreNonnullByDefault
public class BuildMarkerRenderer implements DebugRenderer.IDebugRenderer {

    private AIHelper helper;

    private final class CornerComparator implements Comparator<BuildTask> {
        private final int[] orders;
        private final boolean[] inverts;

        public CornerComparator(int[] orders, boolean[] inverts) {
            super();
            this.orders = orders;
            this.inverts = inverts;
        }

        @Override
        public int compare(BuildTask o1, BuildTask o2) {
            final BlockPos p1 = o1.getForPosition();
            final BlockPos p2 = o2.getForPosition();
            final int[] points = new int[]{p1.getX(), p1.getY(), p1.getZ()};
            final int[] points2 = new int[]{p2.getX(), p2.getY(), p2.getZ()};
            for (int i = 0; i < 3; i++) {
                final int res = ((Integer) points[orders[i]]).compareTo(points2[orders[i]]);
                if (res != 0) {
                    return inverts[i] ? -res : res;
                }
            }
            return 0;
        }
    }

    public BuildMarkerRenderer(AIHelper helper) {
        this.helper = helper;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, double camX, double camY, double camZ) {
        final BuildManager buildManager = helper.buildManager;
        final List<BuildTask> scheduled = buildManager.getScheduled();
        if (scheduled.size() > 0) {
            final BuildTask nextTask = scheduled.get(0);
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getLines());
            BlockPos pos = nextTask.getForPosition();
            AxisAlignedBB box = new AxisAlignedBB(pos).grow(.02, .02, .02);
            VoxelShape voxelshape = VoxelShapes.create(box);
            WorldRenderer.drawVoxelShapeParts(matrixStackIn, ivertexbuilder, voxelshape,
                        -camX, -camY, -camZ, 1, 0, 1, 1.0F);

            // Places where we can stand
            for (BlockPos p : nextTask.getStandablePlaces()) {
                AxisAlignedBB standBox = new AxisAlignedBB(p).grow(-.3, -.1, -.3)
                        .contract(0, .8, 0);
                VoxelShape standVoxelshape = VoxelShapes.create(standBox);
                RenderUtils.drawVoxelShapeParts(matrixStackIn, ivertexbuilder, standVoxelshape,
                        -camX, -camY, -camZ, 1, 0, 1, 1.0F);
            }
        }
    }

}
