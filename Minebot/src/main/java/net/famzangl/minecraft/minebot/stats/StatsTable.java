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

import net.famzangl.minecraft.minebot.stats.BlockBreakStats.BlockBreakStatsChangeListener;

import javax.swing.*;
import java.awt.*;

public class StatsTable extends JPanel implements BlockBreakStatsChangeListener {
	private final JLabel all = new JLabel("");
	private final JLabel perHour = new JLabel("");
	private final JLabel perMinute = new JLabel("");
	private final JLabel perSecond = new JLabel("");
	private BlockBreakStats blockStats;

	public StatsTable(BlockBreakStats blockStats) {
		this.blockStats = blockStats;
		setLayout(new GridLayout(4, 2));
		add(new JLabel("Blocks last second:"));
		add(perSecond);
		add(new JLabel("Blocks last minute:"));
		add(perMinute);
		add(new JLabel("Blocks last hour:"));
		add(perHour);
		add(new JLabel("Blocks since start:"));
		add(all);
		
		blockStats.addChangeListener(this);
	}

	@Override
	public void blockStatsChanged() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				update();
			}
		});
	}

	protected void update() {
		perSecond.setText("" + blockStats.getStatsSlice(1).getAverage());
		perMinute.setText("" + blockStats.getStatsSlice(60).getAverage());
		perHour.setText("" + blockStats.getStatsSlice(3600).getAverage());
		all.setText("" + blockStats.getStatsSlice(1000000).getAverage());
	}
}
