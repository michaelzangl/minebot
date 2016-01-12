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
package net.famzangl.minecraft.minebot.build.blockbuild;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.minecraft.util.BlockPos;

public abstract class BuildTask {

	protected final BlockPos forPosition;

	protected BuildTask(BlockPos forPosition) {
		this.forPosition = forPosition;
	}

//	public static TaskDescription getTaskDescription(WorldData world,
//			BlockPos worldPos) throws UnknownBlockException {
//		int blockWithMeta = world.getBlockIdWithMeta(worldPos);
//		
//		final IBlockState blockState = h.getMinecraft().theWorld.getBlockState(worldPos);
//		final int blockMetadata = h.getWorld().getBlockIdWithMeta(worldPos) & 0xf;
//		if (BlockBuildTask.BLOCKS.contains(b)) {
//			return new TaskDescription(name, CubeBuildTask.STANDABLE);
//		} else if (ColoredCubeBuildTask.BLOCKS.contains(b)) {
//			return new TaskDescription(name + " "
//					+ EnumDyeColor.byMetadata(blockMetadata).getName(),
//					CubeBuildTask.STANDABLE);
//		} else if (FenceBuildTask.BLOCKS.contains(b)) {
//			return new TaskDescription(name, FenceBuildTask.STANDABLE);
//		} else if (LogBuildTask.BLOCKS.contains(b)) {
//			for (final WoodType t : WoodType.values()) {
//				if (t.lowerBits == (blockMetadata & 0x3) && t.block == b) {
//					String dir;
//					BlockPos[] pos;
//					if ((blockMetadata & 0xc) == 0x4) {
//						dir = "east";
//						pos = LogBuildTask.EAST_WEST_POS;
//					} else if ((blockMetadata & 0xc) == 0x8) {
//						dir = "north";
//						pos = LogBuildTask.NORTH_SOUTH_POS;
//					} else {
//						dir = "up";
//						pos = LogBuildTask.UP_DOWN_POS;
//					}
//					return new TaskDescription(name + " "
//							+ t.toString().toLowerCase() + " " + dir, pos);
//				}
//			}
//			throw new UnknownBlockException("Unknown wood type " + b);
//		} else if (WoodBuildTask.BLOCKS.contains(b)) {
//			return new TaskDescription(
//					name
//							+ " "
//							+ WoodType.values()[blockMetadata].toString()
//									.toLowerCase(), CubeBuildTask.STANDABLE);
//		} else if (BuildNormalStairsTask.BLOCKS.contains(b)) {
//			EnumFacing dir;
//			switch (blockMetadata & 0x3) {
//			case 0:
//				dir = EnumFacing.WEST;
//				break;
//			case 1:
//				dir = EnumFacing.EAST;
//				break;
//			case 2:
//				dir = EnumFacing.NORTH;
//				break;
//			default:
//				dir = EnumFacing.SOUTH;
//				break;
//			}
//			final BlockPos p1 = Pos.fromDir(dir.getOpposite()).add(0, 1, 0);
//			final BlockPos p2 = Pos.fromDir(dir.rotateY()).add(
//					0, 1, 0);
//			final BlockPos p3 = Pos.fromDir(dir.rotateYCCW())
//					.add(0, 1, 0);
//			String up;
//			BlockPos[] standable;
//			if ((blockMetadata & 0x4) == 0) {
//				up = "lower";
//				standable = new BlockPos[] { Pos.ZERO, p1, p2, p3 };
//			} else {
//				up = "upper";
//				standable = new BlockPos[] { p1, p2, p3 };
//			}
//			return new TaskDescription(name + " "
//					+ dir.toString().toLowerCase() + " " + up, standable);
//		} else if (BuildHalfslabTask.BLOCKS.contains(b)) {
//			for (final SlabType t : SlabType.values()) {
//				if (t.slabBlock == b && t.meta == (blockMetadata & 0x7)) {
//					Half up;
//					EnumFacing[] standable;
//					if ((blockMetadata & 0x8) == 0) {
//						up = Half.LOWER;
//						standable = JumpingPlaceAtHalfTask.TRY_FOR_LOWER;
//					} else {
//						up = Half.UPPER;
//						standable = JumpingPlaceAtHalfTask.TRY_FOR_UPPER;
//					}
//
//					return new TaskDescription(name + " "
//							+ t.toString().toLowerCase() + " "
//							+ up.toString().toLowerCase(),
//							Pos.fromDir(standable));
//				}
//			}
//			throw new UnknownBlockException("Cannot find halfslabs " + b);
//
//		} else {
//			throw new UnknownBlockException("Cannot reverse build task for "
//					+ b);
//		}
//	}

	/**
	 * Gets a list of relative positions we can stand on when building this.
	 * @return
	 */
	public abstract BlockPos[] getStandablePlaces();

	public BlockPos getForPosition() {
		return forPosition;
	}

	public abstract AITask getPlaceBlockTask(BlockPos relativeFromPos);

	public boolean isStandablePlace(BlockPos relativeFromPos) {
		for (final BlockPos p : getStandablePlaces()) {
			if (p.equals(relativeFromPos)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if we can currently build from that pos.
	 * 
	 * @param helper
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public boolean couldBuildFrom(AIHelper helper, int x, int y, int z) {
		final BlockPos pos = getForPosition();
		for (final BlockPos p : getStandablePlaces()) {
			if (p.getX() + pos.getX() == x && p.getY() + pos.getY() == y && p.getZ() + pos.getZ() == z) {
				return true;
			}
		}
		return false;
	}

	public abstract ItemFilter getRequiredItem();

	/**
	 * @param add
	 * @param rotateSteps
	 *            0..3 Steps of EnumFacing.rotate(UP).
	 * @param mirror
	 *            Applied after rotate (if possible);
	 * @return
	 */
	public abstract BuildTask withPositionAndRotation(BlockPos add, int rotateSteps,
			MirrorDirection mirror);

	public boolean isReadyForBuild(AIHelper helper) {
		return BlockSets.AIR.isAt(helper.getWorld(), getForPosition());
	}

	public Object[] getCommandArguments() {
		throw new UnsupportedOperationException();
	}

}
