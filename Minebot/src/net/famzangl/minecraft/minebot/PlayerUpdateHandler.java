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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * This sends a list of all visible players to a server, e.g. to display it on a
 * map. The server is configured in the minebot preferences.
 * 
 * @author michael
 * 
 */
public class PlayerUpdateHandler {
	private final class SendToServerTask implements Runnable {
		private final String json;

		private SendToServerTask(String json) {
			this.json = json;
		}

		@Override
		public void run() {
			HttpURLConnection connection = null;
			try {
				final String urlParameters = "players="
						+ URLEncoder.encode(json);
				if (!toLoaded) {
//					to = new MinebotSettings().get("report_position_to",
//							null);
					//FIXME: Allow something here.
					to = null;
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
	}

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
		final String name = player.getDisplayName().getUnformattedText();
		final Long blocked = blockTimes.get(name);
		if (blocked != null && blocked > System.currentTimeMillis()) {
			return;
		}
		blockTimes.put(name, System.currentTimeMillis() + 2000);

		final String json = String
				.format("{\"players\":[{\"username\": \"%s\", \"x\": %d, \"y\" : %d, \"z\": %d, \"world\" : \"world\"}]}",
						StringEscapeUtils.escapeJava(name), (int) player.posX,
						(int) player.posY, (int) player.posZ);
		sendThread.execute(new SendToServerTask(json));
	}
}
