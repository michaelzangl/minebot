package net.famzangl.minecraft.minebot.ai.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public abstract class BlockWithDataOrDontcare {

	protected final int blockIdWithMeta;

	public BlockWithDataOrDontcare(int blockWithMeta) {
		blockIdWithMeta = blockWithMeta;
	}

	public static BlockWithDataOrDontcare getFromString(String blockWithMeta) {
		// Works on minecraft:dirt:1
		Matcher matcher = Pattern.compile("^(.+?)(?:\\:(.+))?$").matcher(blockWithMeta);
		if (!matcher.matches()) {
			throw new IllegalArgumentException(
					"Illegal block name: " + blockWithMeta);
		}
		Block block = (Block) Block.blockRegistry.getObject(matcher.group(1));
		if (block == null) {
			// minecraft:dirt case
			block = (Block) Block.blockRegistry.getObject(blockWithMeta);
			matcher = null;
		}
		if (block == null) {
			throw new IllegalArgumentException(
					"Could not understand block name: " + blockWithMeta);
		} else if (matcher == null || matcher.group(2) == null || matcher.group(2).matches("\\*?")) {
			return new BlockWithDontcare(block);
		} else if (matcher.group(2).matches("(1[0-5]|\\d)")) {
			int meta = Integer.parseInt(matcher.group(2));
			return new BlockWithData(block, meta);
		} else {
			return BlockWithData.fromNiceMeta(block, matcher.group(2));
		}
	}

	public Block getBlock() {
		return Block.getBlockById(getBlockId());
	}

	public int getBlockId() {
		return blockIdWithMeta >> 4;
	}

	public abstract String toBlockString();

	@Override
	public String toString() {
		return toBlockString();
	}

	protected String getBlockString() {
		Block block = getBlock();
		final ResourceLocation name = ((ResourceLocation) Block.blockRegistry
				.getNameForObject(block));
		String domain = name.getResourceDomain().equals("minecraft") ? ""
				: name.getResourceDomain() + ":";
		String blockName = domain + name.getResourcePath();
		return blockName;
	}

	public abstract BlockSet toBlockSet();

	public abstract boolean containedIn(BlockSet blockSet);
}
