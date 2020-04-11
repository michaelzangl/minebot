package net.famzangl.minecraft.minebot.ai.utils;

import java.util.Collection;
import java.util.HashSet;

import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.minecraft.util.math.BlockPos;

public class BlockPosList extends BlockArea {

	public final HashSet<BlockPos> positions;

	public BlockPosList(Collection<BlockPos> posititons) {
		this.positions = new HashSet<BlockPos>(posititons);
	}

	@Override
	public void accept(AreaVisitor visitor, WorldData world) {
		for (BlockPos pos : positions) {
			visitor.visit(world, pos.getX(), pos.getY(), pos.getZ());
		}
	}

	@Override
	public boolean contains(WorldData world, int x, int y, int z) {
		return positions.contains(new BlockPos(x, y, z));
	}

}
