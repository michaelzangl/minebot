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
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import net.famzangl.minecraft.minebot.ai.path.world.BlockFloatMap;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.settings.FieldValidation.FieldValidator;
import net.famzangl.minecraft.minebot.settings.serialize.BlockFloatAdapter;
import net.famzangl.minecraft.minebot.settings.serialize.BlockSetAdapter;
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
	private static final MinebotSettings INSTANCE = new MinebotSettings();

	private static final MinebotSettingsRoot defaultSettings = new MinebotSettingsRoot();

	private MinebotSettingsRoot settings;
	private ArrayList<String> keys;

	private long settingsLastModified = 0;

	private static Object mutex = new Object();

	private MinebotSettings() {
	}

	private synchronized MinebotSettingsRoot createSettings() {
		File settingsFile = getSettingsFile();
		if (settings == null || changedSinceLastLoad(settingsFile)) {
			settings = null;
			try {
				settingsLastModified = settingsFile.lastModified();
				System.out.println("Loading " + settingsFile.getAbsolutePath()
						+ " ... (date: " + new Date(settingsLastModified) + ")");
				Gson gson = getGson();
				settings = gson.fromJson(new FileReader(settingsFile),
						MinebotSettingsRoot.class);
				validateAfterLoad(settings);
			} catch (final IOException e) {
				System.err.println("Could not read settings file.");
			} catch (final JsonParseException e) {
				System.err.println("Error in settings file:" + e.getMessage());
			}
			if (settings == null) {
				System.err.println("Fall back to default settings.");
				settings = defaultSettings;
			}
		}

		return settings;
	}

	private boolean changedSinceLastLoad(File settingsFile) {
		return settingsFile.lastModified() > settingsLastModified;
	}

	private void doWriteSettings() {
		MinebotSettingsRoot s = createSettings();

		File settingsFile = getSettingsFile();
		try {
			System.out.println("Writing " + settingsFile.getAbsolutePath()
					+ " ...");
			Gson gson = getGson();
			PrintWriter writer = new PrintWriter(settingsFile);
			gson.toJson(s, writer);
			writer.close();
		} catch (final IOException e) {
			System.err.println("Could not write settings file.");
		}
	}

	private Gson getGson() {
		GsonBuilder gson = new GsonBuilder();
		gson.setPrettyPrinting();
		gson.registerTypeAdapter(BlockSet.class, new BlockSetAdapter());
		gson.registerTypeAdapter(BlockFloatMap.class, new BlockFloatAdapter());
		return gson.create();
	}

	private void validateAfterLoad(MinebotSettingsRoot loaded) {
		FieldValidation.validateAfterLoad(loaded, new MinebotSettingsRoot());
	}

	public static MinebotSettingsRoot getSettings() {
		return getInstance().createSettings();
	}

	public static void writeSettings() {
		INSTANCE.doWriteSettings();
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

	public static File getDataDirFile(String name) {
		return new File(getDataDir(), name);
	}

	public static File getSettingsFile() {
		return getDataDirFile("minebot.json");
	}

	private static MinebotSettings getInstance() {
		return INSTANCE;
	}
}
