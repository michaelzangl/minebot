package net.famzangl.minecraft.minebot.map;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.minecraft.util.BlockPos;

public final class PlayerPositionLabel extends JPanel {

	private final JLabel x = new JLabel();
	private final JLabel y = new JLabel();
	private final JLabel z = new JLabel();

	public PlayerPositionLabel() {
		add(x);
		add(y);
		add(z);
		x.setPreferredSize(new Dimension(50, 15));
		y.setPreferredSize(new Dimension(20, 15));
		z.setPreferredSize(new Dimension(50, 15));
	}

	public void setPosition(BlockPos newPlayer) {
		x.setText(newPlayer == null ? "?" : newPlayer.getX() + "");
		y.setText(newPlayer == null ? "?" : newPlayer.getY() + "");
		z.setText(newPlayer == null ? "?" : newPlayer.getZ() + "");
	}

}