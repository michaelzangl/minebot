package net.famzangl.minecraft.minebot.ai.command;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import net.famzangl.minecraft.minebot.ai.AIHelper;

public class FileNameBuilder extends ParameterBuilder {
	public static boolean isFilenameValid(String file) {
		File f = new File(file);
		try {
			f.getCanonicalPath();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	private final static class FileArgumentDefinition extends
			ArgumentDefinition {
		public FileArgumentDefinition(String description) {
			super("File", description);
		}

		@Override
		public boolean couldEvaluateAgainst(String string) {
			return isFilenameValid(string);
		}

		@Override
		public void getTabCompleteOptions(String currentStart,
				Collection<String> addTo) {
			for (File r : File.listRoots()) {
				if (r.getAbsolutePath().startsWith(currentStart)) {
					addTo.add(r.getAbsolutePath());
				}
			}
			if (!isFilenameValid(currentStart)) {
				return;
			}
			File dir = new File(currentStart);
			String namePrefix = "";
			if (!currentStart.endsWith(File.separator)) {
				namePrefix = dir.getName();
				dir = dir.getParentFile();
			}
			if (dir != null && dir.isDirectory()) {
				for (File f : dir.listFiles()) {
					if (f.getName().startsWith(namePrefix)) {
						addTo.add(f.getPath() + (f.isDirectory() ? File.separator : ""));
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
		list.add(new FileArgumentDefinition(annot.description()));
	}

	@Override
	public Object getParameter(AIHelper helper, String[] arguments) {
		return arguments[0];
	}

}
