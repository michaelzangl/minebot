/*******************************************************************************
 * This file is part of Minebot.
 *
 * Minebot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Minebot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Minebot.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package net.famzangl.minecraft.minebot.stats;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

/**
 * Warning: Dirty, temporary hack.
 * @author Michael Zangl
 *
 */
public class StatsWindow extends JFrame {
	public StatsWindow(StatsManager stats) {
		setLayout(new BoxLayout(getContentPane(), BoxLayout.LINE_AXIS));
		add(new StatsTable(stats.getBlockStats()));
		pack();
	}
}
