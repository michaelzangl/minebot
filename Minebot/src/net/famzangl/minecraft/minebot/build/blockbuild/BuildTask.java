package net.famzangl.minecraft.minebot.build.blockbuild;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ColoredBlockItemFilter;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.JumpingPlaceAtHalfTask;
import net.famzangl.minecraft.minebot.build.WoodType;
import net.minecraft.block.Block;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class BuildTask {

	protected final Pos forPosition;

	protected BuildTask(Pos forPosition) {
		this.forPosition = forPosition;
	}

	public static BuildTask taskFor(Pos forPosition, Block blockToPlace,
			String extra, String extra2) {
		if (AIHelper.blockIsOneOf(blockToPlace, BlockBuildTask.BLOCKS)) {
			return new BlockBuildTask(forPosition, blockToPlace);
		} else if (AIHelper.blockIsOneOf(blockToPlace,
				ColoredCubeBuildTask.BLOCKS)) {
			return new ColoredCubeBuildTask(forPosition, blockToPlace, extra);
		} else if (AIHelper.blockIsOneOf(blockToPlace, FenceBuildTask.BLOCKS)) {
			return new FenceBuildTask(forPosition, blockToPlace);
		} else if (AIHelper.blockIsOneOf(blockToPlace, LogBuildTask.BLOCKS)) {
			return new LogBuildTask(forPosition, extra, extra2);
		} else if (AIHelper.blockIsOneOf(blockToPlace, WoodBuildTask.BLOCK)) {
			return new WoodBuildTask(forPosition, extra);
		} else if (AIHelper.blockIsOneOf(blockToPlace,
				BuildNormalStairsTask.BLOCKS)) {
			return new BuildNormalStairsTask(forPosition, blockToPlace, extra,
					extra2);
		} else if (AIHelper
				.blockIsOneOf(blockToPlace, BuildHalfslabTask.BLOCKS)) {
			return new BuildHalfslabTask(forPosition, extra, extra2);
		} else {
			throw new IllegalArgumentException("Cannot build " + blockToPlace);
		}
	}

	public static TaskDescription getTaskDescription(Block b, AIHelper h,
			int x, int y, int z) {
		String name = Block.blockRegistry.getNameForObject(b);
		int blockMetadata = h.getMinecraft().theWorld.getBlockMetadata(x, y, z);
		if (AIHelper.blockIsOneOf(b, BlockBuildTask.BLOCKS)) {
			return new TaskDescription(name, CubeBuildTask.STANDABLE);
		} else if (AIHelper.blockIsOneOf(b, ColoredCubeBuildTask.BLOCKS)) {
			return new TaskDescription(name
					+ ColoredBlockItemFilter.COLORS[blockMetadata],
					CubeBuildTask.STANDABLE);
		} else if (AIHelper.blockIsOneOf(b, FenceBuildTask.BLOCKS)) {
			return new TaskDescription(name, FenceBuildTask.STANDABLE);
		} else if (AIHelper.blockIsOneOf(b, LogBuildTask.BLOCKS)) {
			for (WoodType t : WoodType.values()) {
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
			throw new IllegalArgumentException("Unknown wood type " + b);
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
			Pos p1 = Pos.fromDir(dir.getOpposite()).add(0, 1, 0);
			Pos p2 = Pos.fromDir(dir.getRotation(ForgeDirection.UP)).add(0, 1,
					0);
			Pos p3 = Pos.fromDir(dir.getRotation(ForgeDirection.DOWN)).add(0,
					1, 0);
			String up;
			Pos[] standable;
			if ((blockMetadata & 0x4) == 0) {
				up = "normal";
				standable = new Pos[] { new Pos(0, 0, 0), p1, p2, p3 };
			} else {
				up = "up";
				standable = new Pos[] { p1, p2, p3 };
			}
			return new TaskDescription(name + " "
					+ dir.toString().toLowerCase() + " " + up, standable);
		} else if (AIHelper.blockIsOneOf(b, BuildHalfslabTask.BLOCKS)) {
			for (SlabType t : SlabType.values()) {
				if (t.slabBlock == b && t.meta == (blockMetadata & 0x7)) {
					String up;
					ForgeDirection[] standable;
					if ((blockMetadata & 0x8) == 0) {
						up = "normal";
						standable = JumpingPlaceAtHalfTask.TRY_FOR_LOWER;
					} else {
						up = "up";
						standable = JumpingPlaceAtHalfTask.TRY_FOR_UPPER;
					}

					return new TaskDescription(name + " "
							+ t.toString().toLowerCase() + " " + up,
							Pos.fromDir(standable));
				}
			}
			throw new IllegalArgumentException("Cannot find halfslabs " + b);

		} else {
			throw new IllegalArgumentException("Cannot reverse build task for "
					+ b);
		}
	}

	public abstract Pos[] getStandablePlaces();

	public Pos getForPosition() {
		return forPosition;
	}

	public abstract AITask getPlaceBlockTask(Pos relativeFromPos);

	public boolean isStandablePlace(Pos relativeFromPos) {
		for (Pos p : getStandablePlaces()) {
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
		Pos pos = getForPosition();
		for (Pos p : getStandablePlaces()) {
			if (p.x + pos.x == x && p.y + pos.y == y && p.z + pos.z == z) {
				return true;
			}
		}
		return false;
	}
}
