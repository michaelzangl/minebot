package net.famzangl.minecraft.minebot.map;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockWhitelist;
import net.famzangl.minecraft.minebot.ai.utils.PrivateFieldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

/**
 * Reads the map that is sent to the user,
 * 
 * @author michael
 *
 */
public class MapReader {
	private static final int BLOCK_SIZE = 1024;

	private final File baseFile;

	private final MapReaderTask task = new MapReaderTask();
	private final MapWriterTask writer = new MapWriterTask();
	private BlockingQueue<Chunk> chunksToProcess = new LinkedBlockingQueue<Chunk>();
	private BlockingQueue<WriteableImage> imagesToWrite = new LinkedBlockingQueue<WriteableImage>();

	private MapDisplayDialog mapDialog;

	private enum RenderMode {
		UNDERGROUND(new UndergroundRenderer(), "-underground"), MAP(
				new MapRenderer(), ""), BIOME(new BiomeRenderer(), "-biome");

		private interface IRenderer {

			int getColor(Chunk chunk, int dx, int dz);
		}

		private static class UndergroundRenderer implements IRenderer {
			private static final BlockWhitelist IGNORED_COVER_BLOCKS = new BlockWhitelist(
					Blocks.air, Blocks.leaves, Blocks.leaves2, Blocks.log,
					Blocks.log2, Blocks.torch, Blocks.water,
					Blocks.flowing_water, Blocks.waterlily, Blocks.lava,
					Blocks.flowing_lava);
			private static final BlockWhitelist UNDERGROUND_BLOCKS = new BlockWhitelist(
					Blocks.air, Blocks.torch);
			private static final BlockWhitelist STRUCTURE_BLOCKS = new BlockWhitelist(
					Blocks.oak_fence, Blocks.end_portal_frame,
					Blocks.end_stone, Blocks.bookshelf, Blocks.prismarine,
					Blocks.planks, Blocks.nether_brick, Blocks.nether_wart,
					Blocks.torch);
			private static final BlockWhitelist INTERESTING_BLOCKS = new BlockWhitelist(
					Blocks.chest, Blocks.mob_spawner, Blocks.gold_block);

			@Override
			public int getColor(Chunk chunk, int dx, int dz) {
				int h = chunk.getHeight(dx, dz) + 1;
				while (h > 3
						&& IGNORED_COVER_BLOCKS.contains(chunk.getBlock(dx, h,
								dz))) {
					h--;
				}
				int underground = 0;
				int structure = 0;
				int interesting = 0;
				for (int i = 0; i < h; i++) {
					Block block = chunk.getBlock(dx, i, dz);
					if (UNDERGROUND_BLOCKS.contains(block)) {
						underground++;
					}
					if (STRUCTURE_BLOCKS.contains(block)) {
						structure++;
					}
					if (INTERESTING_BLOCKS.contains(block)) {
						interesting++;
					}
				}
				int r = Math.min((int) (structure / 6.0 * 0xff), 0xff);
				int g = Math.min((int) (interesting / 2.0 * 0xff), 0xff);
				int b = Math.min((int) (Math.sqrt(underground) / 6.0 * 0xff),
						0xff);
				return 0xff000000 | (r << 16) | (g << 8) | b;
			}
		}

		private static class MapRenderer implements IRenderer {
			@Override
			public int getColor(Chunk chunk, int dx, int dz) {
				int h = chunk.getHeight(dx, dz) + 1;
				IBlockState state;
				do {
					--h;
					state = chunk.getBlockState(new BlockPos(dx, h, dz));
				} while (state.getBlock().getMapColor(state) == MapColor.airColor
						&& h > 0);

				MapColor color = (state.getBlock().getMapColor(state));
				return getColor(color);
			}

			private int getColor(MapColor color) {
				return 0xff000000 | color.colorValue;
			}
		}

		private static class BiomeRenderer implements IRenderer {
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
			public int getColor(Chunk chunk, int dx, int dz) {
				int i = dx & 15;
				int j = dz & 15;
				int k = chunk.getBiomeArray()[j << 4 | i] & 255;
				// assume it is already loaded. If not, we ignore it.
				Integer color = COLORS.get(k);
				return color != null ? color : DEFAULT_COLOR;
			}

		}

		private IRenderer renderer;
		private String ext;

		private RenderMode(IRenderer renderer, String ext) {
			this.renderer = renderer;
			this.ext = ext;
		}

		public String getExt() {
			return ext;
		}

		public int getColor(Chunk chunk, int dx, int dz) {
			return renderer.getColor(chunk, dx, dz);
		}
	}

	private final class WriteableImage {

		/**
		 * Overlay this image over the original image. Set whenever there was an
		 * original image and this image has not been set.
		 */
		private boolean overlay = false;

		/**
		 * Sub image position.
		 */
		private final ImagePos pos;

		private BufferedImage image;
		private BufferedImage writeImage;

		private boolean changed;

		private RenderMode mode;

		public WriteableImage(ImagePos pos, RenderMode mode) {
			super();
			this.pos = pos;
			this.mode = mode;

		}

		private File getWithExt(String prefix) {
			return new File(baseFile.getAbsolutePath() + prefix + "."
					+ pos.topLeftX + "." + pos.topLeftZ + mode.getExt() + "."
					+ "png");
		}

		public File getPath() {
			return getWithExt("");
		}

		public File getTempPath() {
			return getWithExt(".tmp");
		}

		/**
		 * Get the image to paint on.
		 * 
		 * @return
		 */
		public synchronized BufferedImage getPaintingImage() {
			try {
				if (image == null) {
					System.out.println("Reading old image for " + pos);
					image = ImageIO.read(getPath());
				}
			} catch (IOException e) {
			}
			if (image == null) {
				System.out.println("Generating new image for " + pos);
				image = new BufferedImage(BLOCK_SIZE, BLOCK_SIZE,
						BufferedImage.TYPE_4BYTE_ABGR);
			}
			return image;
		}

		public synchronized void markChanged() {
			changed = true;
		}

		/**
		 * Mark the current image version for writing.
		 * 
		 * @return <code>true</code> if the image was changed and marked for
		 *         writing.
		 */
		public synchronized boolean prepareToWrite() {
			if (changed && writeImage == null) {
				long time = System.currentTimeMillis();
				ColorModel cm = image.getColorModel();
				writeImage = new BufferedImage(BLOCK_SIZE, BLOCK_SIZE,
						BufferedImage.TYPE_4BYTE_ABGR);
				Graphics graphics = writeImage.getGraphics();
				graphics.drawImage(image, 0, 0, null);
				graphics.dispose();
				System.out.println(pos + ": Storing for write took "
						+ (System.currentTimeMillis() - time) + " ms");
				changed = false;
				return true;
			}
			return false;
		}

		public void write() {
			try {
				long time = System.currentTimeMillis();
				getTempPath().getParentFile().mkdirs();
				ImageIO.write(writeImage, "png", getTempPath());
				getTempPath().renameTo(getPath());
				System.out.println(pos + ": Writing took "
						+ (System.currentTimeMillis() - time) + " ms");
				synchronized (this) {
					writeImage = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void renderAt(Chunk chunk, int dx, int dz) {
			int color = mode.getColor(chunk, dx, dz);
			getPaintingImage().setRGB(
					-pos.topLeftX + chunk.xPosition * 16 + dx,
					-pos.topLeftZ + chunk.zPosition * 16 + dz, color);
		}
	}

	private final class MapWriterTask implements Runnable {

		private boolean doStop;

		@Override
		public void run() {
			try {
				while (!doStop) {
					WriteableImage image = imagesToWrite.poll(5000,
							TimeUnit.MILLISECONDS);
					if (image != null) {
						image.write();
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public void stop() {
			doStop = true;
		}

		private void write(BufferedImage map) throws IOException {
		}
	}

	private static class ImagePos {
		private int topLeftX;
		private int topLeftZ;

		public ImagePos(int x, int z) {
			topLeftX = round(x);
			topLeftZ = round(z);
		}

		public static final int round(int val) {
			if (val < 0) {
				return (val - (BLOCK_SIZE - 1)) / BLOCK_SIZE * BLOCK_SIZE;
			} else {
				return val / BLOCK_SIZE * BLOCK_SIZE;
			}
		}

		@Override
		public String toString() {
			return "ImagePos [topLeftX=" + topLeftX + ", topLeftZ=" + topLeftZ
					+ "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + topLeftX;
			result = prime * result + topLeftZ;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ImagePos other = (ImagePos) obj;
			if (topLeftX != other.topLeftX)
				return false;
			if (topLeftZ != other.topLeftZ)
				return false;
			return true;
		}

	}

	private class MultiModeImage {
		private WriteableImage[] images = new WriteableImage[RenderMode
				.values().length];

		public MultiModeImage(ImagePos pos) {
			for (int i = 0; i < images.length; i++) {
				images[i] = new WriteableImage(pos, RenderMode.values()[i]);
			}
		}

		public WriteableImage getForRenderMode(RenderMode mode) {
			return images[mode.ordinal()];
		}

		public void offerForWrite(BlockingQueue<WriteableImage> imagesToWrite) {
			for (WriteableImage i : images) {
				if (i.prepareToWrite()) {
					imagesToWrite.offer(i);
				}
			}
		}

		public void renderAt(Chunk chunk, int dx, int dz) {
			for (WriteableImage i : images) {
				i.renderAt(chunk, dx, dz);
			}
		}

		public void markChanged() {
			for (WriteableImage i : images) {
				i.markChanged();
			}
		}
	}

	private final class MapReaderTask implements Runnable {
		private static final long SAVE_TIME = 10000;
		private boolean stopped;
		private boolean doStop;

		private Hashtable<ImagePos, MultiModeImage> images = new Hashtable<ImagePos, MultiModeImage>();
		private final Object imagesMutex = new Object();

		@Override
		public void run() {
			try {
				long nextWrite = System.currentTimeMillis() + SAVE_TIME;

				while (!doStop) {
					Chunk chunk = chunksToProcess.poll(1000,
							TimeUnit.MILLISECONDS);
					// System.out.println("Chunks left to process: "
					// + chunksToProcess.size());
					if (chunk != null) {
						renderChunk(chunk);
						if ((System.currentTimeMillis() > nextWrite || chunksToProcess
								.isEmpty()) && imagesToWrite.isEmpty()) {
							write();
							nextWrite = System.currentTimeMillis() + SAVE_TIME;
						}
					}
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				try {
					write();
				} catch (Throwable e) {
				}
				stopped = true;
			}
		}

		private void write() {
			System.out.println("Marking write");
			synchronized (imagesMutex) {
				for (MultiModeImage i : images.values()) {
					i.offerForWrite(imagesToWrite);
				}
			}
		}

		private void renderChunk(Chunk chunk) {
			ImagePos pos = new ImagePos(chunk.xPosition * 16,
					chunk.zPosition * 16);

			MultiModeImage image = getImage(pos);

			for (int dz = 0; dz < 16; dz++) {
				for (int dx = 0; dx < 16; dx++) {
					image.renderAt(chunk, dx, dz);
				}
			}

			image.markChanged();
		}

		public void stop() {
			doStop = true;
		}

		public MultiModeImage getImage(ImagePos pos) {
			synchronized (imagesMutex) {
				MultiModeImage image = images.get(pos);
				if (image == null) {
					image = new MultiModeImage(pos);
					images.put(pos, image);
				}
				return image;
			}
		}

	}

	private class MapDisplay extends JPanel {
		private int blocksPerPixel = 4;

		private BlockPos playerPos = null;

		private RenderMode mode = RenderMode.MAP;

		private Action plusAction = new AbstractAction("+") {
			@Override
			public void actionPerformed(ActionEvent e) {
				blocksPerPixelMultiply(.5);
			}
		};
		private Action minusAction = new AbstractAction("-") {
			@Override
			public void actionPerformed(ActionEvent e) {
				blocksPerPixelMultiply(2);
			}
		};

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			int scaledSize = BLOCK_SIZE / blocksPerPixel;
			if (player == null) {
				return;
			}
			playerPos = player;
			g.setColor(Color.RED);
			System.out.println("Redraw at " + playerPos);
			int minPosX = ImagePos.round(player.getX() - getWidth()
					* blocksPerPixel / 2);
			int maxPosX = ImagePos.round(player.getX() + getWidth()
					* blocksPerPixel / 2);
			int minPosZ = ImagePos.round(player.getZ() - getHeight()
					* blocksPerPixel / 2);
			int maxPosZ = ImagePos.round(player.getZ() + getHeight()
					* blocksPerPixel / 2);
			for (int x = minPosX; x <= maxPosX; x += BLOCK_SIZE) {
				for (int z = minPosZ; z <= maxPosZ; z += BLOCK_SIZE) {
					WriteableImage im = task.getImage(new ImagePos(x, z))
							.getForRenderMode(mode);
					BufferedImage draw = im.getPaintingImage();

					g.drawImage(draw, blockToPanelX(x), blockToPanelY(z),
							scaledSize, scaledSize, null);
					g.drawRect(blockToPanelX(x), blockToPanelY(z), scaledSize,
							scaledSize);
					g.drawString(x + "," + z, blockToPanelX(x),
							blockToPanelY(z));
				}
			}
			g.drawArc(blockToPanelX(playerPos.getX()) - 5,
					blockToPanelY(playerPos.getZ()) - 5, 10, 10, 0, 360);
		}

		private int blockToPanelX(int blockX) {
			return (blockX - playerPos.getX()) / blocksPerPixel + getWidth()
					/ 2;
		}

		private int blockToPanelY(int blockZ) {
			return (blockZ - playerPos.getZ()) / blocksPerPixel + getHeight()
					/ 2;
		}

		private BlockPos getBlockPosition(Point panelPosition) {
			int blockX = (int) ((panelPosition.getX() - getWidth() / 2.0)
					* blocksPerPixel + playerPos.getX());
			int blockZ = (int) ((panelPosition.getY() - getHeight() / 2.0)
					* blocksPerPixel + playerPos.getZ());
			return new BlockPos(blockX, 64, blockZ);
		}

		public void blocksPerPixelMultiply(double d) {
			blocksPerPixel = Math.min(
					Math.max((int) Math.round(blocksPerPixel * d), 1), 32);

			minusAction.setEnabled(blocksPerPixel < 32);
			plusAction.setEnabled(blocksPerPixel > 1);
		}

		public void setMode(RenderMode mode) {
			this.mode = mode;
			repaint();
		}

		public Action createChangeModeAction(final RenderMode myMode) {
			return new AbstractAction("Mode: " + myMode) {
				@Override
				public void actionPerformed(ActionEvent e) {
					setMode(myMode);
				}
			};
		}

		public Action getMinusAction() {
			return plusAction;
		}

		public Action getPlusAction() {
			return minusAction;
		}

		@Override
		public String getToolTipText(MouseEvent event) {
			BlockPos pos = getBlockPosition(event.getPoint());
			return pos.getX() + "," + pos.getZ();
		}
	}

	private static final class MapDisplayDialog extends JFrame {

		public MapDisplayDialog(MapDisplay d) {
			setLayout(new BorderLayout());
			JToolBar toolbar = new JToolBar();
			for (RenderMode v : RenderMode.values()) {
				toolbar.add(d.createChangeModeAction(v));
			}

			toolbar.add(new JSeparator(JSeparator.VERTICAL));
			toolbar.add(d.getMinusAction());
			toolbar.add(d.getPlusAction());
			add(toolbar, BorderLayout.NORTH);
			add(d);
			setTitle("Map view. Scale: " + d.blocksPerPixel);
			pack();
			setSize(1500, 800);
			setVisible(true);
		}
	}

	public MapReader(File file) {
		super();
		this.baseFile = file;

		new Thread(task, "Map reader").start();
		new Thread(writer, "Map writer").start();

		mapDisplay = new MapDisplay();
		mapDialog = new MapDisplayDialog(mapDisplay);
	}

	int currentIndex = 0;

	private Pos player;

	private MapDisplay mapDisplay;

	public void tick(AIHelper helper) {
		WorldClient theWorld = helper.getMinecraft().theWorld;
		if (theWorld == null) {
			return;
		}
		IChunkProvider provider = PrivateFieldUtils.getFieldValue(
				theWorld, World.class,
				IChunkProvider.class);
		if (!(provider instanceof ChunkProviderClient)) {
			return;
		}
		List<Chunk> list;
			list = PrivateFieldUtils.getFieldValue(
					(ChunkProviderClient) provider, ChunkProviderClient.class,
					List.class);
		if (currentIndex == 0) {
			try {
				for (Chunk chunk : list) {
					chunksToProcess.offer(chunk);
				}
			} catch (IndexOutOfBoundsException e) {
			} catch (ConcurrentModificationException e) {
			}
		}

		currentIndex++;
		if (currentIndex > 10 * 20 && chunksToProcess.isEmpty()) {
			// TODO: mark to save here.
			currentIndex = 0;
		}

		player = helper.getPlayerPosition();
		mapDisplay.repaint();
	}

	public void onStop() {
		task.stop();
		writer.stop();
		chunksToProcess.clear();
		mapDialog.setVisible(false);
		mapDialog.dispose();
	}
}
