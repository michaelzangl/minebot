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

import net.famzangl.minecraft.minebot.ai.AIController;
import net.famzangl.minecraft.minebot.ai.path.world.BlockBoundsCache;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;

import java.net.URISyntaxException;

@Mod("minebot-mod")
public class MinebotMod {
	public static MinebotMod instance;

	public MinebotMod() {
		instance = this;
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
	}
	
	static {
		// logging
		String doLogging = System.getProperty("MINEBOT_LOG", "0");
		if (doLogging.equals("1")) {
			LoggerContext context = (LoggerContext) LogManager.getContext(false);
			Configuration config = context.getConfiguration();
			try {
				context.setConfigLocation(MinebotMod.class.getResource("log4j.xml").toURI());
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}
	
	// Note: 6364136223846793005L * 0xc097ef87329e28a5l = 1

	public void init(FMLCommonSetupEvent event) {
		BlockBoundsCache.initialize();
		FMLJavaModLoadingContext.get().getModEventBus().addListener(new PlayerUpdateHandler()::onPlayerTick);
		final AIController controller = new AIController();
		controller.initialize();
	}

	public static String getVersion() {
		// TODO: Get version
		return "TODO";
		//return MinebotMod.class.getAnnotation(Mod.class).version();
	}
}
