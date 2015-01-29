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
package net.famzangl.minecraft.minebot.ai.command;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.settings.MinebotSettings;

public class FileNameBuilder extends ParameterBuilder {
	public static boolean isFilenameValid(String file) {
		final File f = new File(file);
		try {
			f.getCanonicalPath();
			return true;
		} catch (final IOException e) {
			return false;
		}
	}

	private final static class FileArgumentDefinition extends
			ArgumentDefinition {
		private String relativeToSettingsFile;

		public FileArgumentDefinition(String description, String relativeToSettingsFile) {
			super("File", description);
			this.relativeToSettingsFile = relativeToSettingsFile;
		}

		@Override
		public boolean couldEvaluateAgainst(String string) {
			return isFilenameValid(string);
		}

		@Override
		public void getTabCompleteOptions(String currentStart,
				Collection<String> addTo) {
			for (final File r : File.listRoots()) {
				if (r.getAbsolutePath().startsWith(currentStart)) {
					addTo.add(r.getAbsolutePath());
				}
			}
			if (!isFilenameValid(currentStart)) {
				return;
			}
			File base;
			if (relativeToSettingsFile.isEmpty()) {
				base = MinebotSettings.getDataDir();
			} else {
				base = MinebotSettings.getDataDirFile(relativeToSettingsFile + "/");
			}
			String currentPath = Paths.get(base.getAbsolutePath()).resolve(currentStart).toString();
			
			File dir = new File(currentPath);
			String namePrefix = "";
			if (!currentStart.endsWith(File.separator)) {
				namePrefix = dir.getName();
				dir = dir.getParentFile();
			}
			if (dir != null && dir.isDirectory()) {
				for (final File f : dir.listFiles()) {
					if (f.getName().startsWith(namePrefix)) {
						String path = f.getPath()
								+ (f.isDirectory() ? File.separator : "");
						if (path.startsWith(base.getAbsolutePath())) {
							path = path.substring(base.getAbsolutePath().length() + 1);
						}
						addTo.add(path);
					}
				}
			}
		}
	}

	public FileNameBuilder(AICommandParameter annot) {
		super(annot);
	}

	@Override
	public void addArguments(ArrayList<ArgumentDefinition> list) {
		list.add(new FileArgumentDefinition(annot.description(), annot.relativeToSettingsFile()));
	}

	@Override
	public Object getParameter(AIHelper helper, String[] arguments) {
		String relativeToSettingsFile = annot.relativeToSettingsFile();
		File base;
		if (relativeToSettingsFile.isEmpty()) {
			base = MinebotSettings.getDataDir();
		} else {
			base = MinebotSettings.getDataDirFile(relativeToSettingsFile);
		}
		String currentPath = Paths.get(base.getAbsolutePath()).resolve(arguments[0]).toString();
		return new File(currentPath);
	}

}
