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
package net.famzangl.minecraft.minebot.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;

/**
 * This wraps a minebot setting file and provides convenient access to the
 * values stored in it.
 * 
 * @author michael
 * 
 */
public class MinebotSettings {
	private Properties settings;
	private ArrayList<String> keys;

	private static Object mutex = new Object();

	public MinebotSettings() {
	}

	private Properties getSettings() {
		if (settings == null) {
			File settingsFile;
			synchronized (mutex) {
				settingsFile = getSettingsFile();
			}
			settings = new Properties();
			try {
				System.out.println("Loading " + settingsFile.getAbsolutePath()
						+ " ...");
				settings.load(new FileInputStream(settingsFile));
			} catch (final IOException e) {
				System.err.println("Could not read settings file.");
			}
		}

		return settings;
	}

	private File getSettingsFile() {
		return getDataDirFile("minebot.properties");
	}

	public static File getDataDirFile(String name) {
		return new File(getDataDir(), name);
	}

	public static File getDataDir() {
		File dir = new File(Minecraft.getMinecraft().mcDataDir, "minebot");
		System.out.println("Data directory: " + dir);
		if (!dir.isDirectory()) {
			try {
				return new MinebotDirectoryCreator().createDirectory(dir);
			} catch (IOException e) {
				System.err.println("Could not create settings directory.");
				e.printStackTrace();
			}
		}
		return dir;
	}

	public String get(String key, String defaultValue) {
		final String property = getSettings().getProperty(key);
		return property == null ? defaultValue : property;
	}

	public int getInt(String key, int defaultValue) {
		final String property = getSettings().getProperty(key);
		try {
			return Integer.parseInt(property);
		} catch (final Throwable t) {
			return defaultValue;
		}
	}

	public float getFloat(String key, float defaultValue) {
		final String property = getSettings().getProperty(key);
		try {
			return Float.parseFloat(property);
		} catch (final Throwable t) {
			return defaultValue;
		}
	}

	/**
	 * GEt a list of blocks.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public BlockSet getBlocks(String key, BlockSet defaultValue) {
		final String property = getSettings().getProperty(key);
		if (property == null) {
			return defaultValue;
		}
		ArrayList<Block> blocks = new ArrayList<Block>();
		for (final String name : property.split("\\s*[\\,\\s\\;]\\s*")) {
			final Block block = (Block) Block.blockRegistry.getObject(name);
			if (block != null) {
				blocks.add(block);
			} else {
				System.out.println("Invalid block name: " + name);
			}
		}
		return new BlockSet(blocks.toArray(new Block[blocks.size()]));
	}

	public float getFloat(String string, float defaultValue, float min,
			float max) {
		return Math.max(min, Math.min(max, getFloat(string, defaultValue)));
	}

	public Collection<String> getKeys() {
		if (keys == null) {
			keys = new ArrayList<String>();
			for (final Object k : getSettings().keySet()) {
				keys.add((String) k);
			}
		}

		return keys;
	}

}
