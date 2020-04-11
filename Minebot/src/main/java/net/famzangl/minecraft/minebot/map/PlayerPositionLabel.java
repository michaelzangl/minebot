package net.famzangl.minecraft.minebot.map;

import net.minecraft.util.math.BlockPos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class PlayerPositionLabel extends JPanel {
	
	public interface FollowPlayerListener {
		public void setFollowPlayer(boolean follow);
	}

	private final JLabel x = new JLabel();
	private final JLabel y = new JLabel();
	private final JLabel z = new JLabel();
	private final FollowPlayerListener listener;

	public PlayerPositionLabel(FollowPlayerListener listener) {
		this.listener = listener;
		add(x);
		add(y);
		add(z);
		x.setPreferredSize(new Dimension(50, 15));
		y.setPreferredSize(new Dimension(30, 15));
		z.setPreferredSize(new Dimension(50, 15));
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				PlayerPositionLabel.this.listener.setFollowPlayer(true);
			}
		});
	}

	public void setPosition(BlockPos newPlayer) {
		x.setText(newPlayer == null ? "?" : newPlayer.getX() + "");
		y.setText(newPlayer == null ? "?" : newPlayer.getY() + "");
		z.setText(newPlayer == null ? "?" : newPlayer.getZ() + "");
		
		String pos = newPlayer == null ? "?" : newPlayer.getX() + " " + newPlayer.getY() + " " + newPlayer.getZ();
		setToolTipText("<html><p>Player position: <b>" + pos + "</b>.</p><p>Click to follow player.</p></html>");
	}
}