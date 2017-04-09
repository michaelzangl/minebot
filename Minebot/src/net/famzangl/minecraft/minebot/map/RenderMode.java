package net.famzangl.minecraft.minebot.map;

import java.util.Hashtable;

import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.utils.BlockCounter;
import net.famzangl.minecraft.minebot.ai.utils.BlockCuboid;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;

public enum RenderMode {
	UNDERGROUND(new UndergroundRenderer(), "-underground"), MAP(
			new MapRenderer(), ""), BIOME(new BiomeRenderer(), "-biome");
	private static final BlockSet GLOBAL_COVER_BLACKLIST = new BlockSet(
			Blocks.wooden_slab, Blocks.stone_slab, Blocks.stone_slab2,
			Blocks.air);
	private static final BlockSet IGNORED_COVER_BLOCKS = new BlockSet(
			Blocks.air, Blocks.leaves, Blocks.leaves2, Blocks.log,
			Blocks.log2, Blocks.torch, Blocks.water, Blocks.flowing_water,
			Blocks.waterlily, Blocks.lava, Blocks.flowing_lava,
			Blocks.snow, Blocks.snow_layer, Blocks.ice)
			.unionWith(GLOBAL_COVER_BLACKLIST);
	private static final BlockSet UNDERGROUND_BLOCKS = new BlockSet(
			Blocks.air, Blocks.torch);
	private static final BlockSet STRUCTURE_BLOCKS = new BlockSet(
			Blocks.oak_fence, Blocks.end_portal_frame, Blocks.end_stone,
			Blocks.bookshelf, Blocks.prismarine, Blocks.planks,
			Blocks.nether_brick, Blocks.nether_wart, Blocks.torch);
	private static final BlockSet INTERESTING_BLOCKS = new BlockSet(
			Blocks.chest, Blocks.mob_spawner, Blocks.gold_block);

	private interface IRenderer {
		/**
		 * Gets the color for one pixel of the map.
		 * 
		 * @param world
		 *            The world to use.
		 * @param chunk
		 *            The chunk we are rendering
		 * @param dx
		 *            World x coordinate
		 * @param dz
		 *            World y coordinate
		 * @return The rgba color.
		 */
		int getColor(WorldData world, Chunk chunk, int dx, int dz);
	}

	private static class UndergroundRenderer implements RenderMode.IRenderer {

		@Override
		public int getColor(WorldData world, Chunk chunk, int dx, int dz) {
			int height = chunk.getHeight(dx & 0xf, dz & 0xf) + 1;
			while (height > 3
					&& IGNORED_COVER_BLOCKS.contains(chunk.getBlock(dx, height, dz))) {
				height--;
			}
			BlockCuboid area = new BlockCuboid(new BlockPos(dx, 0, dz),
					new BlockPos(dx, height, dz));

			int[] count = BlockCounter.countBlocks(world, area,
					STRUCTURE_BLOCKS, INTERESTING_BLOCKS, UNDERGROUND_BLOCKS);
			// structure
			int r = Math.min((int) (count[0] / 6.0 * 0xff), 0xff);
			// interesting
			int g = Math.min((int) (count[1] / 2.0 * 0xff), 0xff);
			// underground
			int b = Math.min((int) (Math.sqrt(count[2]) / 6.0 * 0xff), 0xff);
			return 0xff000000 | (r << 16) | (g << 8) | b;
		}
	}

	private static class MapRenderer implements RenderMode.IRenderer {
		@Override
		public int getColor(WorldData world, Chunk chunk, int dx, int dz) {
			int height = chunk.getHeight(dx & 0xf, dz & 0xf) + 1;
			IBlockState state;
			do {
				--height;
				state = chunk.getBlockState(new BlockPos(dx, height, dz));
			} while ((GLOBAL_COVER_BLACKLIST.contains(state.getBlock()) || state
					.getBlock().getMapColor(state) == MapColor.airColor)
					&& height > 0);

			MapColor color = (state.getBlock().getMapColor(state));
			return getColor(color);
		}

		private int getColor(MapColor color) {
			return 0xff000000 | color.colorValue;
		}
	}

	private static class BiomeRenderer implements RenderMode.IRenderer {
		private static final Hashtable<Integer, Integer> COLORS = new Hashtable<Integer, Integer>();
		private static final Integer DEFAULT_COLOR = 0xff000000;

		static {
			COLORS.put(0, 0xff0036ff); // Ocean
			COLORS.put(1, 0xff5fd15c); // Plains
			COLORS.put(2, 0xffe8e874); // Desert
			COLORS.put(3, 0xff8b6d50); // Extreme Hills
			COLORS.put(4, 0xff1ea31a); // Forest
			COLORS.put(5, 0xff004d24); // Taiga
			COLORS.put(6, 0xff008340); // Swampland
			COLORS.put(7, 0xff315dff); // River
			COLORS.put(8, 0xffba4627); // Hell (Nether)
			COLORS.put(9, 0xff31ffa3); // Sky (End)
			COLORS.put(10, 0xff6686ff); // Frozen Ocean
			COLORS.put(11, 0xff86a0ff); // Frozen River
			COLORS.put(12, 0xffe9eeff); // Ice Plains
			COLORS.put(13, 0xffe9eeff); // Ice Mountains
			COLORS.put(14, 0xffff0000);// 0xffcdbaba); // Mushroom Island
			COLORS.put(15, 0xffff0000);// 0xffcdbaba); // Mushroom Island
										// Shore
			COLORS.put(16, 0xffe0e02d); // Beach
			COLORS.put(17, 0xffe8e874); // Desert Hills
			COLORS.put(18, 0xff1ea31a); // Forest Hills
			COLORS.put(19, 0xff004d24); // Taiga Hills
			COLORS.put(20, 0xff8b6d50); // Extreme Hills Edge
			COLORS.put(21, 0xff47bd21); // Jungle
			COLORS.put(22, 0xff47bd21); // Jungle Hills
			COLORS.put(23, 0xff47bd21); // Jungle Edge
			COLORS.put(24, 0xff002098); // Deep Ocean
			COLORS.put(25, 0xff989898); // Stone Beach
			COLORS.put(26, 0xffe0e069); // Cold Beach
			COLORS.put(27, 0xff31a32d); // Birch Forest
			COLORS.put(28, 0xff31a32d); // Birch Forest Hills
			COLORS.put(29, 0xff125d16); // Roofed Forest
			COLORS.put(30, 0xff69c594); // Cold Taiga
			COLORS.put(31, 0xff69c594); // Cold Taiga Hills
			COLORS.put(32, 0xff00391a); // Mega Taiga
			COLORS.put(33, 0xff00391a); // Mega Taiga Hills
			COLORS.put(34, 0xff8b6d50); // Extreme Hills+
			COLORS.put(35, 0xffa0ba00); // Savanna
			COLORS.put(36, 0xffa0ba00); // Savanna Plateau
			COLORS.put(37, 0xffe8822e); // Mesa
			COLORS.put(38, 0xffe8822e); // Mesa Plateau F
			COLORS.put(39, 0xffe8822e); // Mesa Plateau
			COLORS.put(129, 0xff5fd15c); // Sunflower Plains
			COLORS.put(130, 0xffe8e874); // Desert M
			COLORS.put(131, 0xff8b6d50); // Extreme Hills M
			COLORS.put(132, 0xff1ea31a); // Flower Forest
			COLORS.put(133, 0xff004d24); // Taiga M
			COLORS.put(134, 0xff008340); // Swampland M
			COLORS.put(140, 0xff89d9e8); // Ice Plains Spikes
			COLORS.put(149, 0xff47bd21); // Jungle M
			COLORS.put(151, 0xff47bd21); // JungleEdge M
			COLORS.put(155, 0xff31a32d); // Birch Forest M
			COLORS.put(156, 0xff31a32d); // Birch Forest Hills M
			COLORS.put(157, 0xff125d16); // Roofed Forest M
			COLORS.put(158, 0xff69c594); // Cold Taiga M
			COLORS.put(160, 0xff00391a); // Mega Spruce Taiga
			COLORS.put(161, 0xff00391a); // Mega Spruce Taiga Hills
			COLORS.put(162, 0xff8b6d50); // Extreme Hills+ M
			COLORS.put(163, 0xffa0ba00); // Savanna M
			COLORS.put(164, 0xffa0ba00); // Savanna Plateau M
			COLORS.put(165, 0xffe8822e); // Mesa (Bryce)
			COLORS.put(166, 0xffe8822e); // Mesa Plateau F M
			COLORS.put(167, 0xffe8822e); // Mesa Plateau M
		}

		@Override
		public int getColor(WorldData world, Chunk chunk, int dx, int dz) {
			int i = dx & 15;
			int j = dz & 15;
			int k = chunk.getBiomeArray()[j << 4 | i] & 255;
			// assume it is already loaded. If not, we ignore it.
			Integer color = COLORS.get(k);
			return color != null ? color : DEFAULT_COLOR;
		}

	}

	private RenderMode.IRenderer renderer;
	private String ext;

	private RenderMode(RenderMode.IRenderer renderer, String ext) {
		this.renderer = renderer;
		this.ext = ext;
	}

	public String getExt() {
		return ext;
	}

	public int getColor(WorldData world, Chunk chunk, int dx, int dz) {
		return renderer.getColor(world, chunk, dx, dz);
	}

	public String getName() {
		return toString();
	}
}