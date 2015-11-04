package net.famzangl.minecraft.minebot.map;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.famzangl.minecraft.minebot.map.MapReader.ImagePos;
import net.famzangl.minecraft.minebot.map.MapReader.MultiModeImage;
import net.famzangl.minecraft.minebot.map.MapReader.WriteableImage;
import net.minecraft.util.BlockPos;

/**
 * This panel displays the map.
 * 
 * @author Michael Zangl
 */
public class MapDisplay extends JPanel {
	private final class ChangeRendermodeAction extends AbstractAction implements RenderModeListener {
		private final RenderMode myMode;

		private ChangeRendermodeAction(RenderMode myMode) {
			super(myMode.getName());
			this.myMode = myMode;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			setMode(myMode);
		}

		@Override
		public void rendermodeChanged(RenderMode mode) {
			setEnabled(mode != myMode);
		}
	}

	public interface RenderModeListener {
		public void rendermodeChanged(RenderMode mode);
	}
	
	private final class MinusAction extends AbstractAction {
		private MinusAction(String name) {
			super(name);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			blocksPerPixelMultiply(2);
		}
	}

	private final class PlusAction extends AbstractAction {
		private PlusAction(String name) {
			super(name);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			blocksPerPixelMultiply(.5);
		}
	}

	public static class PlayerState {
		public final BlockPos playerPosition;

		public final int playerLook;

		public PlayerState(BlockPos playerPosition, int playerLook) {
			super();
			this.playerPosition = playerPosition;
			this.playerLook = playerLook;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + playerLook;
			result = prime
					* result
					+ ((playerPosition == null) ? 0 : playerPosition.hashCode());
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
			PlayerState other = (PlayerState) obj;
			if (playerLook != other.playerLook)
				return false;
			if (playerPosition == null) {
				if (other.playerPosition != null)
					return false;
			} else if (!playerPosition.equals(other.playerPosition))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "PlayerState [playerPosition=" + playerPosition
					+ ", playerLook=" + playerLook + "]";
		}
	}

	/**
	 * The map to display.
	 */
	private final MapReader map;

	/**
	 * @param mapReader
	 */
	public MapDisplay(MapReader mapReader) {
		map = mapReader;
	}

	private static final int BLOCKS_PER_BASE_PIXEL_MAX = 256;

	private static final int BASE_PIXEL = 16;

	private int blocksPerBasePixel = 32;

	private PlayerState player = null;
	// The player state in the future. Used for easy synchronisation.
	private PlayerState activePlayer = null;
	private final Object activePlayerMutex = new Object();
	private BlockPos mapCenter = new BlockPos(0, 0, 0);

	private RenderMode mode = RenderMode.MAP;

	private Action plusAction = new PlusAction("+");
	private Action minusAction = new MinusAction("-");

	private PlayerPositionLabel playerPositionLabel = new PlayerPositionLabel();

	private final ArrayList<RenderModeListener> renderModeListeners = new ArrayList<RenderModeListener>();

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		int scaledSize = MapReader.BLOCK_SIZE * BASE_PIXEL / blocksPerBasePixel;
		synchronized (activePlayerMutex) {
			if (activePlayer == null) {
				return;
			}
			player = activePlayer;
		}
		playerPositionLabel.setPosition(player.playerPosition);
		mapCenter = player.playerPosition;
		g.setColor(Color.RED);
		ArrayList<IconDefinition> icons = new ArrayList<IconDefinition>();
		// System.out.println("Redraw at " + playerPos);
		int minPosX = ImagePos.round(mapCenter.getX() - getWidth()
				* blocksPerBasePixel / BASE_PIXEL / 2);
		int maxPosX = ImagePos.round(mapCenter.getX() + getWidth()
				* blocksPerBasePixel / BASE_PIXEL / 2);
		int minPosZ = ImagePos.round(mapCenter.getZ() - getHeight()
				* blocksPerBasePixel / BASE_PIXEL / 2);
		int maxPosZ = ImagePos.round(mapCenter.getZ() + getHeight()
				* blocksPerBasePixel / BASE_PIXEL / 2);
		for (int x = minPosX; x <= maxPosX; x += MapReader.BLOCK_SIZE) {
			for (int z = minPosZ; z <= maxPosZ; z += MapReader.BLOCK_SIZE) {
				ImagePos pos = new MapReader.ImagePos(x, z);
				MultiModeImage imageChunk = map.task.getImage(pos);
				WriteableImage im = imageChunk.getForRenderMode(mode);
				icons.addAll(imageChunk.setting.getIcons());

				BufferedImage draw = im.getPaintingImage();

				g.drawImage(draw, blockToPanelX(x), blockToPanelY(z),
						scaledSize, scaledSize, null);
				g.drawRect(blockToPanelX(x), blockToPanelY(z), scaledSize,
						scaledSize);
				g.drawString(x + "," + z, blockToPanelX(x), blockToPanelY(z));
			}
		}

		for (IconDefinition icon : icons) {
			BufferedImage img = icon.getIcon(mode);
			int x = blockToPanelX(icon.getPosition().getX()) - img.getWidth()
					/ 2;
			int y = blockToPanelY(icon.getPosition().getZ()) - img.getHeight()
					/ 2;
			g.drawImage(img, x, y, null);
		}

		drawPlayer(g);
	}

	private void drawPlayer(Graphics g) {
		int x = blockToPanelX(player.playerPosition.getX());
		int y = blockToPanelY(player.playerPosition.getZ());
		int offset = BASE_PIXEL / blocksPerBasePixel / 2;
		x += offset;
		y += offset;
		g.setColor(Color.RED);
		g.drawArc(x - 5, y - 5, 10, 10, 0, 360);
		g.setColor(new Color(1, 0, 0, .4f));
		g.fillArc(x - 10, y - 10, 20, 20,
				map.niceDegrees(-player.playerLook - 90 - 20), 40);
	}

	private int blockToPanelX(int blockX) {
		return (blockX - mapCenter.getX()) * BASE_PIXEL / blocksPerBasePixel
				+ getWidth() / 2;
	}

	private int blockToPanelY(int blockZ) {
		return (blockZ - mapCenter.getZ()) * BASE_PIXEL / blocksPerBasePixel
				+ getHeight() / 2;
	}

	private BlockPos getBlockPosition(Point panelPosition) {
		int blockX = (int) ((panelPosition.getX() - getWidth() / 2.0)
				* blocksPerBasePixel / BASE_PIXEL + mapCenter.getX());
		int blockZ = (int) ((panelPosition.getY() - getHeight() / 2.0)
				* blocksPerBasePixel / BASE_PIXEL + mapCenter.getZ());
		return new BlockPos(blockX, 64, blockZ);
	}

	public void blocksPerPixelMultiply(double d) {
		blocksPerBasePixel = Math.min(
				Math.max((int) Math.round(blocksPerBasePixel * d), 1),
				BLOCKS_PER_BASE_PIXEL_MAX);

		minusAction.setEnabled(blocksPerBasePixel < BLOCKS_PER_BASE_PIXEL_MAX);
		plusAction.setEnabled(blocksPerBasePixel > 1);
		invalidateMap();
		map.mapDialog.updateTitle();
	}

	public void setMode(RenderMode mode) {
		if (this.mode != mode) {
			this.mode = mode;
			for (RenderModeListener l : renderModeListeners) {
				l.rendermodeChanged(mode);
			}
			invalidateMap();
		}
	}
	
	public void addRenderModeListener(RenderModeListener l, boolean fireOnce) {
		renderModeListeners .add(l);
		if (fireOnce) {
			l.rendermodeChanged(mode);
		}
	}
	
	public void removeRenderModeListener(RenderModeListener l) {
		renderModeListeners.remove(l);
	}

	public Action createChangeModeAction(final RenderMode myMode) {
		ChangeRendermodeAction action = new ChangeRendermodeAction(myMode);
		addRenderModeListener(action, true);
		return action;
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

	public JComponent getPlayerPosition() {
		return playerPositionLabel;
	}

	public void invalidateMap() {
		repaint();
	}

	public void setPosition(BlockPos newPlayer, int newLook) {
		synchronized (activePlayerMutex) {
			if (newPlayer == null) {
				if (activePlayer != null) {
					activePlayer = null;
					repaint();
				}
			} else {
				PlayerState newActivePlayer = new PlayerState(newPlayer,
						newLook);
				if (!newActivePlayer.equals(activePlayer)) {
					activePlayer = newActivePlayer;
					repaint();
				}
			}
		}
	}

	public float getScale() {
		return ((float) blocksPerBasePixel / BASE_PIXEL);
	}
}