package net.famzangl.minecraft.minebot.build.blockbuild;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ColoredBlockItemFilter;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.place.JumpingPlaceAtHalfTask;
import net.famzangl.minecraft.minebot.build.WoodType;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildNormalStairsTask.Half;
import net.minecraft.block.Block;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class BuildTask {

	protected final Pos forPosition;

	protected BuildTask(Pos forPosition) {
		this.forPosition = forPosition;
	}

	public static TaskDescription getTaskDescription(Block b, AIHelper h,
			int x, int y, int z) throws UnknownBlockException {
		final String name = Block.blockRegistry.getNameForObject(b)
				.replaceFirst("minecraft:", "");
		final int blockMetadata = h.getMinecraft().theWorld.getBlockMetadata(x,
				y, z);
		if (AIHelper.blockIsOneOf(b, BlockBuildTask.BLOCKS)) {
			return new TaskDescription(name, CubeBuildTask.STANDABLE);
		} else if (AIHelper.blockIsOneOf(b, ColoredCubeBuildTask.BLOCKS)) {
			return new TaskDescription(name + " "
					+ ColoredBlockItemFilter.COLORS[blockMetadata],
					CubeBuildTask.STANDABLE);
		} else if (AIHelper.blockIsOneOf(b, FenceBuildTask.BLOCKS)) {
			return new TaskDescription(name, FenceBuildTask.STANDABLE);
		} else if (AIHelper.blockIsOneOf(b, LogBuildTask.BLOCKS)) {
			for (final WoodType t : WoodType.values()) {
				if (t.lowerBits == (blockMetadata & 0x3) && t.block == b) {
					String dir;
					Pos[] pos;
					if ((blockMetadata & 0xc) == 0x4) {
						dir = "east";
						pos = LogBuildTask.EAST_WEST_POS;
					} else if ((blockMetadata & 0xc) == 0x8) {
						dir = "north";
						pos = LogBuildTask.NORTH_SOUTH_POS;
					} else {
						dir = "up";
						pos = LogBuildTask.UP_DOWN_POS;
					}
					return new TaskDescription(name + " "
							+ t.toString().toLowerCase() + " " + dir, pos);
				}
			}
			throw new UnknownBlockException("Unknown wood type " + b);
		} else if (AIHelper.blockIsOneOf(b, WoodBuildTask.BLOCK)) {
			return new TaskDescription(
					name
							+ " "
							+ WoodType.values()[blockMetadata].toString()
									.toLowerCase(), CubeBuildTask.STANDABLE);
		} else if (AIHelper.blockIsOneOf(b, BuildNormalStairsTask.BLOCKS)) {
			ForgeDirection dir;
			switch (blockMetadata & 0x3) {
			case 0:
				dir = ForgeDirection.WEST;
				break;
			case 1:
				dir = ForgeDirection.EAST;
				break;
			case 2:
				dir = ForgeDirection.NORTH;
				break;
			default:
				dir = ForgeDirection.SOUTH;
				break;
			}
			final Pos p1 = Pos.fromDir(dir.getOpposite()).add(0, 1, 0);
			final Pos p2 = Pos.fromDir(dir.getRotation(ForgeDirection.UP)).add(
					0, 1, 0);
			final Pos p3 = Pos.fromDir(dir.getRotation(ForgeDirection.DOWN))
					.add(0, 1, 0);
			String up;
			Pos[] standable;
			if ((blockMetadata & 0x4) == 0) {
				up = "lower";
				standable = new Pos[] { new Pos(0, 0, 0), p1, p2, p3 };
			} else {
				up = "upper";
				standable = new Pos[] { p1, p2, p3 };
			}
			return new TaskDescription(name + " "
					+ dir.toString().toLowerCase() + " " + up, standable);
		} else if (AIHelper.blockIsOneOf(b, BuildHalfslabTask.BLOCKS)) {
			for (final SlabType t : SlabType.values()) {
				if (t.slabBlock == b && t.meta == (blockMetadata & 0x7)) {
					Half up;
					ForgeDirection[] standable;
					if ((blockMetadata & 0x8) == 0) {
						up = Half.LOWER;
						standable = JumpingPlaceAtHalfTask.TRY_FOR_LOWER;
					} else {
						up = Half.UPPER;
						standable = JumpingPlaceAtHalfTask.TRY_FOR_UPPER;
					}

					return new TaskDescription(name + " "
							+ t.toString().toLowerCase() + " "
							+ up.toString().toLowerCase(),
							Pos.fromDir(standable));
				}
			}
			throw new UnknownBlockException("Cannot find halfslabs " + b);

		} else {
			throw new UnknownBlockException("Cannot reverse build task for "
					+ b);
		}
	}

	public abstract Pos[] getStandablePlaces();

	public Pos getForPosition() {
		return forPosition;
	}

	public abstract AITask getPlaceBlockTask(Pos relativeFromPos);

	public boolean isStandablePlace(Pos relativeFromPos) {
		for (final Pos p : getStandablePlaces()) {
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
		final Pos pos = getForPosition();
		for (final Pos p : getStandablePlaces()) {
			if (p.x + pos.x == x && p.y + pos.y == y && p.z + pos.z == z) {
				return true;
			}
		}
		return false;
	}

	public abstract ItemFilter getRequiredItem();

	/**
	 * @param add
	 * @param rotateSteps 0..3 Steps of ForgeDirection.rotate(UP).
	 * @param mirror Applied after rotate (if possible);
	 * @return
	 */
	public abstract BuildTask withPositionAndRotation(Pos add, int rotateSteps, MirrorDirection mirror);

}
