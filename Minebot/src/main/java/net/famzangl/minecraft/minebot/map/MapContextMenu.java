package net.famzangl.minecraft.minebot.map;

import java.util.Hashtable;
import java.util.Map.Entry;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import net.minecraft.util.math.BlockPos;

public class MapContextMenu extends JPopupMenu {
	private final BlockPos pos;

	public static final class CommandMenuItem extends JMenuItem {
		private final String command;

		public CommandMenuItem(String label, String command, BlockPos position) {
			Hashtable<String, String> replace = new Hashtable<String, String>();

			replace.put("x", position.getX() + "");
			replace.put("y", position.getY() + "");
			replace.put("z", position.getZ() + "");
			replace.put("cx", (position.getX() + .5) + "");
			replace.put("cy", (position.getY() + .5) + "");
			replace.put("cz", (position.getZ() + .5) + "");

			for (Entry<String, String> r : replace .entrySet()) {
				while (command.contains("{" + r.getKey() + "}")) {
					command = command.replace("{" + r.getKey() + "}", r.getValue());
				}
			}
			this.command = command;
			setText(label);
			setToolTipText(command);
			setEnabled(false);
		}
	}

	public MapContextMenu(BlockPos pos) {
		this.pos = pos;
		JLabel headline = new JLabel("<html><p><b>" + pos.getX() + ", " + pos.getY() + ", "
				+ pos.getZ() + "</b></p></html>");
		headline.setAlignmentX(.5f);
		add(headline);
		add(new JSeparator());

		add(new CommandMenuItem("Walk to this position", "/minebot walk {cx} {cz}", pos));
		add(new JSeparator());
		add(new CommandMenuItem("Set position 1", "/minebuild pos1 {x} ~0 {z}", pos));
		add(new CommandMenuItem("Set position 2", "/minebuild pos2 {x} ~0 {z}", pos));
	}

}
