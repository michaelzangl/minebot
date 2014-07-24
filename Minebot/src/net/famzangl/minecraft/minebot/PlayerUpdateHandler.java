package net.famzangl.minecraft.minebot;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.entity.player.EntityPlayer;

import org.apache.commons.lang3.StringEscapeUtils;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class PlayerUpdateHandler {
	private final ExecutorService sendThread;
	private final Hashtable<String, Long> blockTimes = new Hashtable<String, Long>();
	private boolean toLoaded;
	private String to;

	public PlayerUpdateHandler() {
		sendThread = Executors.newFixedThreadPool(2);
	}

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent evt) {
		if (toLoaded && to == null) {
			return;
		}
		final EntityPlayer player = evt.player;
		final String name = player.getDisplayName();
		final Long blocked = blockTimes.get(name);
		if (blocked != null && blocked > System.currentTimeMillis()) {
			return;
		}
		blockTimes.put(name, System.currentTimeMillis() + 2000);

		final String json = String
				.format("{\"players\":[{\"username\": \"%s\", \"x\": %d, \"y\" : %d, \"z\": %d, \"world\" : \"world\"}]}",
						StringEscapeUtils.escapeJava(name), (int) player.posX,
						(int) player.posY, (int) player.posZ);
		sendThread.execute(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					final String urlParameters = "players="
							+ URLEncoder.encode(json);
					if (!toLoaded) {
						to = new MinebotSettings().get("report_position_to",
								null);
						toLoaded = true;
					}
					if (to == null) {
						return;
					}
					final URL url = new URL(to);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("POST");
					connection.setRequestProperty("Content-Type",
							"application/x-www-form-urlencoded");
					connection.setRequestProperty("Content-Length",
							Integer.toString(urlParameters.getBytes().length));
					connection.setRequestProperty("Content-Language", "en-US");
					connection.setUseCaches(false);
					connection.setDoInput(true);
					connection.setDoOutput(true);

					final DataOutputStream wr = new DataOutputStream(connection
							.getOutputStream());
					wr.writeBytes(urlParameters);
					wr.flush();
					wr.close();
					final InputStream is = connection.getInputStream();
					final BufferedReader rd = new BufferedReader(
							new InputStreamReader(is));
					while (rd.readLine() != null) {
					}
					rd.close();
				} catch (final Throwable t) {
					t.printStackTrace();
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		});
	}
}
