package net.famzangl.minecraft.minebot.ai.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.path.world.BlockMetaSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.RunOnceStrategy;
import net.famzangl.minecraft.minebot.ai.utils.BlockArea;
import net.famzangl.minecraft.minebot.ai.utils.BlockArea.AreaVisitor;
import net.famzangl.minecraft.minebot.ai.utils.BlockCuboid;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;

@AICommand(helpText = "Some performance/... tests.", name = "minebot")
public class CommandTestMinectaft {
	private static final int TEST_RUNS = 50;

	public static final class MutableBlockPos extends BlockPos {
            public int x;
            public int y;
            public int z;

            private MutableBlockPos()
            {
                super(0, 0, 0);
            }

            public int getX()
            {
                return this.x;
            }

            public int getY()
            {
                return this.y;
            }

            public int getZ()
            {
                return this.z;
            }
        }
    
	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "test", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "performance", description = "") String nameArg2) {
		return new RunOnceStrategy() {

			@Override
			protected void singleRun(AIHelper helper) {
				// This performance test might freeze minecraft.

				visitBlocksAroundPlayer(helper.getWorld());
				accessNativeBlocksAroundPlayer(helper.getWorld(),
						helper.getMinecraft().theWorld);
				accessBlocksAroundPlayer(helper.getWorld());
				accessNativeBlocksAroundPlayerLoop(helper.getWorld(),
						helper.getMinecraft().theWorld);
				accessNativeBlocksAroundPlayerLoopMutableBP(helper.getWorld(),
						helper.getMinecraft().theWorld);
				accessBlocksAroundPlayerLoop(helper.getWorld());
				accessBlockSetAroundPlayer(helper.getWorld());
				accessBlockMetaSetAroundPlayer(helper.getWorld());
				accessBlockSetAroundPlayerMultiple(helper.getWorld());
			}
		};
	}

	public static BlockCuboid blocksAroundPlayer(WorldData world) {
		BlockPos pos = world.getPlayerPosition();
		return new BlockCuboid(
				new BlockPos(pos.getX() - 32, 0, pos.getZ() - 32),
				new BlockPos(pos.getX() + 32, 100, pos.getZ() + 32));
	}

	private static void visitBlocksAroundPlayer(WorldData world) {
		BlockArea area = blocksAroundPlayer(world);
		AreaVisitor v = new AreaVisitor() {
			@Override
			public void visit(WorldData world, int x, int y, int z) {
			}
		};
		long start = start();
		for (int i = 0; i < TEST_RUNS; i++) {
			area.accept(v, world);
		}
		done("visitBlocksAroundPlayer", start);
	}

	private static void accessBlocksAroundPlayer(WorldData world) {
		BlockArea area = blocksAroundPlayer(world);
		AreaVisitor v = new AreaVisitor() {
			@Override
			public void visit(WorldData world, int x, int y, int z) {
				world.getBlockIdWithMeta(x, y, z);
			}
		};
		long start = start();
		for (int i = 0; i < TEST_RUNS; i++) {
			area.accept(v, world);
		}
		done("accessBlocksAroundPlayer", start);
	}

	private static void accessNativeBlocksAroundPlayer(WorldData world,
			final WorldClient theWorld) {
		BlockArea area = blocksAroundPlayer(world);
		AreaVisitor v = new AreaVisitor() {
			@Override
			public void visit(WorldData world, int x, int y, int z) {
				theWorld.getBlockState(new BlockPos(x, y, z));
			}
		};
		long start = start();
		for (int i = 0; i < TEST_RUNS; i++) {
			area.accept(v, world);
		}
		done("accessNativeBlocksAroundPlayer", start);
	}

	private static void accessBlocksAroundPlayerLoop(WorldData world) {
		BlockCuboid area = blocksAroundPlayer(world);
		int minX = area.getMin().getX();
		int minY = area.getMin().getY();
		int minZ = area.getMin().getZ();
		int maxX = area.getMax().getX();
		int maxY = area.getMax().getY();
		int maxZ = area.getMax().getZ();
		long start = start();
		for (int i = 0; i < TEST_RUNS; i++) {
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					for (int z = minZ; z <= maxZ; z++) {
						world.getBlockIdWithMeta(x, y, z);
					}
				}
			}
		}
		done("accessBlocksAroundPlayerLoop", start);
	}

	private static void accessNativeBlocksAroundPlayerLoop(WorldData world,
			WorldClient theWorld) {
		BlockCuboid area = blocksAroundPlayer(world);
		int minX = area.getMin().getX();
		int minY = area.getMin().getY();
		int minZ = area.getMin().getZ();
		int maxX = area.getMax().getX();
		int maxY = area.getMax().getY();
		int maxZ = area.getMax().getZ();
		long start = start();
		for (int i = 0; i < TEST_RUNS; i++) {
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					for (int z = minZ; z <= maxZ; z++) {
						theWorld.getBlockState(new BlockPos(x, y, z));
					}
				}
			}
		}
		done("accessNativeBlocksAroundPlayerLoop", start);
	}

	private static void accessNativeBlocksAroundPlayerLoopMutableBP(WorldData world,
			WorldClient theWorld) {
		BlockCuboid area = blocksAroundPlayer(world);
		int minX = area.getMin().getX();
		int minY = area.getMin().getY();
		int minZ = area.getMin().getZ();
		int maxX = area.getMax().getX();
		int maxY = area.getMax().getY();
		int maxZ = area.getMax().getZ();
		long start = start();
		MutableBlockPos p = new MutableBlockPos();
		for (int i = 0; i < TEST_RUNS; i++) {
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					for (int z = minZ; z <= maxZ; z++) {
						p.x = x;
						p.y = y;
						p.z = z;
						theWorld.getBlockState(p);
					}
				}
			}
		}
		done("accessNativeBlocksAroundPlayerLoopMutableBP", start);
	}

	private static void accessBlockSetAroundPlayer(WorldData world) {
		BlockArea area = blocksAroundPlayer(world);
		final BlockSet set = BlockSets.SAFE_CEILING;
		AreaVisitor v = new AreaVisitor() {
			@Override
			public void visit(WorldData world, int x, int y, int z) {
				set.isAt(world, x, y, z);
			}
		};
		long start = start();
		for (int i = 0; i < TEST_RUNS; i++) {
			area.accept(v, world);
		}
		done("accessBlockSetAroundPlayer", start);
	}


	private static void accessBlockMetaSetAroundPlayer(WorldData world) {
		BlockArea area = blocksAroundPlayer(world);
		BlockSet setC = BlockSets.SAFE_CEILING;
		BlockMetaSet sand = new BlockMetaSet(Blocks.sand, 0);
		final BlockSet set = setC.unionWith(sand);
		AreaVisitor v = new AreaVisitor() {
			@Override
			public void visit(WorldData world, int x, int y, int z) {
				set.isAt(world, x, y, z);
			}
		};
		long start = start();
		for (int i = 0; i < TEST_RUNS; i++) {
			area.accept(v, world);
		}
		done("accessBlockMetaSetAroundPlayer", start);
	}
	
	private static void accessBlockSetAroundPlayerMultiple(WorldData world) {
		BlockArea area = blocksAroundPlayer(world);
		final BlockSet set = BlockSets.SAFE_CEILING;
		AreaVisitor v = new AreaVisitor() {
			@Override
			public void visit(WorldData world, int x, int y, int z) {
				set.isAt(world, x, y, z);
				set.isAt(world, x, y + 1, z);
				set.isAt(world, x + 1, y, z);
				set.isAt(world, x, y, z + 1);
				set.isAt(world, x, y, z - 1);
				set.isAt(world, x - 1, y, z);
			}
		};
		long start = start();
		for (int i = 0; i < TEST_RUNS; i++) {
			area.accept(v, world);
		}
		done("accessBlockSetAroundPlayerMultiple", start);
	}


	private static void done(String string, long start) {
		long end = System.currentTimeMillis();
		System.out.println(string + ": " + (end - start) + "ms");
	}

	private static long start() {
		System.gc();
		return System.currentTimeMillis();
	}
}
