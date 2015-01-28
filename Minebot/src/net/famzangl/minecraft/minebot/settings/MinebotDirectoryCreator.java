package net.famzangl.minecraft.minebot.settings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;

/**
 * Creates the Minebot directory structure. This includes the files:
 * 
 * <pre>
 * minebot/
 *   minebot.properties
 *   build/
 *     *.build
 *   scripts/
 *     *.js
 *   README.txt
 * </pre>
 * 
 * @author michael
 *
 */
public class MinebotDirectoryCreator {

	private static final String BASE = "net/famzangl/minecraft/minebot/settings/minebot/";

	public File createDirectory(File dir) throws IOException {
		CodeSource src = MinebotDirectoryCreator.class.getProtectionDomain()
				.getCodeSource();
		if (src != null) {
			URL jar = src.getLocation();
			if (jar.getFile().endsWith("class")) {
				// We are in a dev enviroment.
				return new File(new File(jar.getFile()).getParentFile(), "minebot");
			}
			if (dir.isFile()) {
				dir.delete();
			}

			dir.mkdirs();
			ZipInputStream zip = new ZipInputStream(jar.openStream());
			try {
				ZipEntry e;
				while ((e = zip.getNextEntry()) != null) {
					String name = e.getName();
					if (name.startsWith(BASE)) {
						String[] localName = name.substring(BASE.length())
								.split("/");
						File currentDir = dir;
						for (int i = 0; i < localName.length; i++) {
							currentDir = new File(currentDir, localName[i]);
							currentDir.mkdir();
						}
						File copyTo = new File(currentDir,
								localName[localName.length - 1]);
						extract(zip, copyTo);
					}
				}
			} finally {
				zip.close();
			}
			return dir;
		} else {
			throw new IOException(
					"Could not find minebot directory to extract.");
		}
	}

	private void extract(ZipInputStream zip, File copyTo) {
		if (!copyTo.exists()) {
			System.out.println("Extracting " + copyTo.getAbsolutePath());

			InputStream source = zip;
			FileOutputStream destination = null;

			try {
				copyTo.createNewFile();
				destination = new FileOutputStream(copyTo);
				IOUtils.copy(source, destination);
			} catch (final IOException e) {
				System.err.println("Error copying default settings.");
				copyTo.delete();
			} catch (final NullPointerException e) {
				System.err.println("Could not find default settings.");
				copyTo.delete();
			} finally {
				if (destination != null) {
					try {
						destination.close();
					} catch (final IOException e) {
					}
				}
			}
		}
	}
}
