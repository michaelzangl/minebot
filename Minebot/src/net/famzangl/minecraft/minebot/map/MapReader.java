package net.famzangl.minecraft.minebot.map;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.net.ChunkListener;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData.ChunkAccessorUnmodified;
import net.famzangl.minecraft.minebot.ai.utils.PrivateFieldUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

/**
 * Reads the map that is sent to the user,
 * 
 * @author michael
 *
 */
public class MapReader implements ChunkListener {
	private static final boolean DO_USE_HASHES = false;

	private static final int ALL_CHUNKS_LOAD_INTERVALL = 30 * 20;

	static final int BLOCK_SIZE = 1024;

	private final File baseFile;

	private BlockingQueue<Chunk> chunksToProcess = new LinkedBlockingQueue<Chunk>();
	private BlockingQueue<WriteableImage> imagesToWrite = new LinkedBlockingQueue<WriteableImage>();

	private MapDisplay mapDisplay = new MapDisplay(this);
	MapDisplayDialog mapDialog = new MapDisplayDialog(mapDisplay);

	private WorldChangeManager wcm = new WorldChangeManager(mapDialog);

	private ChunkQueue chunkQueue = new ChunkQueue();

	final MapReaderTask task = new MapReaderTask();
	private final MapWriterTask writer = new MapWriterTask();

	final class WriteableImage {

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

		public void renderAt(WorldData world, Chunk chunk, int dx, int dz) {
			int color = mode.getColor(world, chunk, chunk.xPosition * 16 + dx,
					chunk.zPosition * 16 + dz);
			getPaintingImage().setRGB(
					-pos.topLeftX + chunk.xPosition * 16 + dx,
					-pos.topLeftZ + chunk.zPosition * 16 + dz, color);
		}

		public void renderChunk(WorldData world, Chunk chunk) {
			int chunkX = chunk.xPosition * 16;
			int chunkZ = chunk.zPosition * 16;

			byte[] pixels = ((DataBufferByte) getPaintingImage().getRaster()
					.getDataBuffer()).getData();
			boolean wasChanged = false;
			for (int dz = 0; dz < 16; dz++) {
				int offset = 4 * ((-pos.topLeftZ + chunkZ + dz) * BLOCK_SIZE
						+ -pos.topLeftX + chunkX);
				byte[] row = new byte[16 * 4];
				for (int dx = 0; dx < 16; dx++) {
					int x = chunkX + dx;
					int z = chunkZ + dz;

					int color = mode.getColor(world, chunk, x, z);
					row[dx * 4] = (byte) (color >> 24);
					row[dx * 4 + 1] = (byte) (color);
					row[dx * 4 + 2] = (byte) (color >> 8);
					row[dx * 4 + 3] = (byte) (color >> 16);
				}
				for (int i = 0; i < row.length; i++) {
					if (row[i] != pixels[offset + i]) {
						// simply copy the rest.
						System.arraycopy(row, i, pixels, offset + i, row.length
								- i);
						wasChanged = true;
						break;
					}
				}
			}
			if (wasChanged) {
				markChanged();
			}
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

	static class ImagePos {
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

	private static class WorldChangeManager {

		private Frame baseFrame;

		private long ignoreUntil = 0;

		public WorldChangeManager(Frame baseFrame) {
			this.baseFrame = baseFrame;
		}

		private boolean displayWorldWarning() {
			Object[] options = new Object[] { "Skip rendering this chunk",
					"Replace this chunk",
					"Replace all new chunks the next 2 Minutes." };
			int res = JOptionPane
					.showOptionDialog(
							baseFrame,
							"<html>The world seems to have changed. What should I do?<p>You can either replace the recorded chunks with the new world your are in now or you can pause map recording until you are back in your old world.<p>You should select to skip if you went back to the old world.</html>",
							"World changed", JOptionPane.DEFAULT_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, options,
							options[0]);
			if (res == 0) {
			} else if (res == 1) {
				return true;
			} else if (res == 2) {
				ignoreUntil = System.currentTimeMillis() + 2 * 60 * 1000;
				return true;
			}
			return false;
		}

		public synchronized boolean shouldStillRender() {
			// / for now, just use synchronized to delay.
			return true;
		}

		/**
		 * 
		 * @param chunkX
		 * @param chunZ
		 * @return If we should render any ways.
		 */
		public synchronized boolean chunkHashChanged(int chunkX, int chunZ) {
			if (!DO_USE_HASHES) {
				return true;
			} else if (System.currentTimeMillis() < ignoreUntil) {
				// ignored.
				return true;
			} else {
				return displayWorldWarning();
			}
		}
	}

	private static final class SettingsContainer {
		private Hashtable<String, Integer> hashes = new Hashtable<String, Integer>();

		private List<IconDefinition> icons = new LinkedList<IconDefinition>();
	}

	static class WriteableSetting {

		private final ImagePos pos;
		private SettingsContainer settings = null;
		private boolean read;
		private final File baseFile;

		public WriteableSetting(File baseFile, ImagePos pos) {
			this.baseFile = baseFile;
			this.pos = pos;
		}

		private File getFile() {
			return new File(baseFile.getAbsolutePath() + "." + pos.topLeftX
					+ "." + pos.topLeftZ + "." + "json");
		}

		public synchronized Integer getChunkHash(int x, int z) {
			attemptRead();
			return settings.hashes.get(x + "," + z);
		}

		public synchronized void setChunkHash(int x, int z, int hash) {
			settings.hashes.put(x + "," + z, hash);
			write();
		}

		private void attemptRead() {
			if (settings == null) {
				Gson gson = new Gson();
				try {
					settings = gson.fromJson(new FileReader(getFile()),
							SettingsContainer.class);
				} catch (JsonSyntaxException e) {
					e.printStackTrace();
				} catch (JsonIOException e) {
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					// use defaults..
				}
				if (settings == null) {
					settings = new SettingsContainer();
				}
				if (settings.hashes == null) {
					settings.hashes = new Hashtable<String, Integer>();
				}
				if (settings.icons == null) {
					settings.icons = new LinkedList<IconDefinition>();
				}
			}
		}

		private void write() {
			Gson gson = new Gson();
			try {
				// String json = gson.toJson(settings, SettingsContainer.class);
				// SettingsContainer mySettings = gson.fromJson(json,
				// SettingsContainer.class);

				FileWriter fileWriter = new FileWriter(getFile());
				try {
					gson.toJson(settings, SettingsContainer.class, fileWriter);
				} finally {
					fileWriter.close();
				}
			} catch (JsonIOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		public List<IconDefinition> getIcons() {
			attemptRead();
			return settings.icons;
		}

		public void addIcon(IconDefinition icon) {
			settings.icons.add(icon);
			write();
		}
	}

	class MultiModeImage {
		private WriteableImage[] images = new WriteableImage[RenderMode
				.values().length];
		WriteableSetting setting;

		public MultiModeImage(ImagePos pos) {
			for (int i = 0; i < images.length; i++) {
				images[i] = new WriteableImage(pos, RenderMode.values()[i]);
			}
			setting = new WriteableSetting(baseFile, pos);
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

		public void renderChunk(WorldData world, Chunk chunk) {
			for (WriteableImage i : images) {
				i.renderChunk(world, chunk);
			}
		}

		public void markChanged() {
			for (WriteableImage i : images) {
				i.markChanged();
			}
		}

		public boolean isValidForChunkHash(int x, int z, int hash) {
			Integer h = setting.getChunkHash(x, z);
			// System.out.println("Old hash(" + x + "," + z + "): " + h
			// + ", new hash: " + hash);
			return h == null || h == hash;
		}

		public void setChunkHash(int x, int z, int hash) {
			setting.setChunkHash(x, z, hash);
			// System.out.println("Store hash(" + x + "," + z + ") = " + hash);
		}
	}

	/**
	 * A (hopefully) unique hash that identifies the world the chunk is in.
	 * 
	 * @param chunk
	 * @return
	 */
	protected static int getChunkHash(Chunk chunk) {
		ExtendedBlockStorage[] a = chunk.getBlockStorageArray();
		if (a.length == 0) {
			return 0;
		}
		ChunkAccessorUnmodified data = new WorldData.ChunkAccessorUnmodified(
				chunk);

		int y = 1;
		int prime = 31;
		int hash = 1;
		for (int x = 1; x < 15; x++) {
			for (int z = 1; z < 15; z++) {
				if (isBedrock(data, x - 1, y, z)
						&& isBedrock(data, x + 1, y, z)
						&& isBedrock(data, x, y, z - 1)
						&& isBedrock(data, x, y, z + 1)
						&& isBedrock(data, x, y + 1, z + 1)) {
					hash += data.getBlockIdWithMeta(x, y, z);
				}
				hash *= prime;
			}
		}
		return hash;
	}

	private static final int BEDROCK_ID = 7;

	protected static boolean isBedrock(ChunkAccessorUnmodified data, int x,
			int y, int z) {
		return data.getBlockIdWithMeta(x, y, z) >> 4 == BEDROCK_ID;
	}

	final class MapReaderTask implements Runnable {
		private static final long SAVE_TIME = 10000;
		private boolean stopped;
		private boolean doStop;

		private Hashtable<ImagePos, MultiModeImage> images = new Hashtable<ImagePos, MultiModeImage>();
		private final Object imagesMutex = new Object();

		public MapReaderTask() {
		}

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
			if (!wcm.shouldStillRender()) {
				return;
			}
			ImagePos pos = new ImagePos(chunk.xPosition * 16,
					chunk.zPosition * 16);

			MultiModeImage image = getImage(pos);
			int hash = getChunkHash(chunk);
			if (!image.isValidForChunkHash(chunk.xPosition, chunk.zPosition,
					hash)) {
				System.err.println("Abort rendering: Chunk hash has changed.");
				if (!wcm.chunkHashChanged(chunk.xPosition, chunk.zPosition)) {
					return;
				}
			}
			image.setChunkHash(chunk.xPosition, chunk.zPosition, hash);

			WorldData world = registeredHelper.getWorld();
			image.renderChunk(world, chunk);

			// TODO: Only repaint if image was changed and is in view.
			mapDisplay.repaint();
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

	static final class MapDisplayDialog extends JFrame {

		private final MapDisplay d;

		public MapDisplayDialog(MapDisplay d) {
			this.d = d;
			setLayout(new BorderLayout());
			JToolBar toolbar = new JToolBar();
			for (RenderMode v : RenderMode.values()) {
				toolbar.add(d.createChangeModeAction(v));
			}

			toolbar.add(new JSeparator(JSeparator.VERTICAL));
			toolbar.add(d.getPlayerPosition());
			toolbar.add(d.getMinusAction());
			toolbar.add(d.getPlusAction());
			add(toolbar, BorderLayout.NORTH);
			add(d);
			updateTitle();
			pack();
			setSize(1500, 800);
			setVisible(true);
		}

		void updateTitle() {
			setTitle("Map view - scale: " + d.getScale());
		}
	}

	public MapReader(File file) {
		super();
		this.baseFile = file;

		new Thread(task, "Map reader").start();
		new Thread(writer, "Map writer").start();

	}

	private class ChunkQueue {
		/**
		 * How long to dely chunks. In game ticks.
		 */
		private static final int OFFER_TIME = 40;

		/**
		 * All chunks we handle.
		 * <p>
		 * The integer states in how many ticks they will be collected for
		 * rendering.
		 */
		private Hashtable<ChunkPos, Integer> dirtyChunks = new Hashtable<ChunkPos, Integer>();

		/**
		 * 
		 */
		private Hashtable<ChunkPos, Integer> cooldownForLastUsed = new Hashtable<ChunkPos, Integer>();

		public synchronized void offer(ChunkPos pos) {
			if (!dirtyChunks.containsKey(pos)) {
				Integer coolDown = cooldownForLastUsed.get(pos);
				int time = coolDown == null ? 0 : coolDown;
				dirtyChunks.put(pos, time);
			}
		}

		public synchronized ArrayList<ChunkPos> tickAndGet() {
			ArrayList<ChunkPos> ret = new ArrayList<ChunkPos>();
			ArrayList<ChunkPos> keys = new ArrayList<ChunkPos>(
					dirtyChunks.keySet());
			for (ChunkPos k : keys) {
				Integer counter = dirtyChunks.get(k);
				if (counter <= 0) {
					dirtyChunks.remove(k);
					ret.add(k);
					cooldownForLastUsed.put(k, OFFER_TIME);
				} else {
					dirtyChunks.put(k, counter - 1);
				}
			}
			ArrayList<ChunkPos> keys2 = new ArrayList<ChunkPos>(
					dirtyChunks.keySet());
			for (ChunkPos k : keys2) {
				Integer time = cooldownForLastUsed.get(k);
				if (time <= 0) {
					cooldownForLastUsed.remove(k);
				} else {
					cooldownForLastUsed.put(k, time - 1);
				}
			}
			return ret;
		}
	}

	int currentIndex = 0;

	private AIHelper registeredHelper;

	private boolean wasAlive;

	public void tick(AIHelper helper) {
		if (registeredHelper == null) {
			registeredHelper = helper;
			helper.getNetworkHelper().addChunkChangeListener(this);
		}
		if (currentIndex == 0) {
			loadAllChunks(helper);
		}

		for (ChunkPos d : chunkQueue.tickAndGet()) {
			Chunk chunkFromChunkCoords = helper.getMinecraft().world
					.getChunkFromChunkCoords(d.chunkXPos, d.chunkZPos);
			if (chunkFromChunkCoords != null) {
				chunksToProcess.offer(chunkFromChunkCoords);
			}
		}

		currentIndex++;
		if (currentIndex > ALL_CHUNKS_LOAD_INTERVALL
				&& chunksToProcess.isEmpty()) {
			// TODO: mark to save here.
			currentIndex = 0;
		}

		checkIsAlive(helper);
		EntityPlayerSP playerSP = helper.getMinecraft().player;
		BlockPos newPlayer = playerSP == null ? null : helper
				.getPlayerPosition();
		int newLook = niceDegrees(playerSP == null ? 0
				: (int) playerSP.rotationYaw);
		mapDisplay.setPosition(newPlayer, newLook);
		mapDisplay.setMarks(helper.getPos1(), helper.getPos2());

		// TODO: Draw highlighted blocks.
	}

	private void checkIsAlive(AIHelper helper) {
		boolean isAlive = helper.isAlive();
		if (!isAlive && wasAlive && helper.getMinecraft().player != null) {
			addIcon(new IconDefinition(helper.getPlayerPosition(), "Death: "
					+ new Date().toLocaleString(), IconType.DEATH));
		}
		wasAlive = isAlive;
	}

	private void loadAllChunks(AIHelper helper) {
		try {
			WorldClient theWorld = helper.getMinecraft().world;
			if (theWorld == null) {
				return;
			}
			IChunkProvider provider = PrivateFieldUtils.getFieldValue(theWorld,
					World.class, IChunkProvider.class);
			if (!(provider instanceof ChunkProviderClient)) {
				return;
			}
			Long2ObjectMap<Chunk> list;
			list = PrivateFieldUtils.getFieldValue(
					(ChunkProviderClient) provider, ChunkProviderClient.class,
					Long2ObjectMap.class);
			for (Chunk chunk : list.values()) {
				chunkQueue.offer(chunk.getPos());
			}
		} catch (IndexOutOfBoundsException e) {
		} catch (ConcurrentModificationException e) {
		}
	}

	@Override
	public void chunkChanged(int chunkX, int chunkZ) {
		chunkQueue.offer(new ChunkPos(chunkX, chunkZ));
	}

	int niceDegrees(int cameraYaw) {
		return ((cameraYaw % 360 + 360) % 360);
	}

	public void onStop() {
		registeredHelper.getNetworkHelper().removeChunkChangeListener(this);
		task.stop();
		writer.stop();
		chunksToProcess.clear();
		mapDialog.setVisible(false);
		mapDialog.dispose();
	}

	public void addIcon(IconDefinition iconDefinition) {
		ImagePos pos = new ImagePos(iconDefinition.getPosition().getX(),
				iconDefinition.getPosition().getZ());
		MultiModeImage image = task.getImage(pos);
		image.setting.addIcon(iconDefinition);
		mapDisplay.repaint();
	}
}
