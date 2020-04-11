package net.famzangl.minecraft.minebot.build.reverse;

import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.minecraft.util.math.BlockPos;

public class UnsupportedBlockException extends Exception {

	private final WorldData world;
	private final BlockPos pos;

	public UnsupportedBlockException(WorldData world, BlockPos pos,
			String message, Throwable cause) {
		super(pos + ": " + message, cause);
		this.world = world;
		this.pos = pos;
	}

	public UnsupportedBlockException(WorldData world, BlockPos pos,
			String message) {
		this(world, pos, message, null);
	}

	public UnsupportedBlockException(WorldData world, BlockPos pos,
			Throwable cause) {
		this(world, pos, "", cause);
	}

}
