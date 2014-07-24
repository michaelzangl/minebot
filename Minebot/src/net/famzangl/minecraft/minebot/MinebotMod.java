package net.famzangl.minecraft.minebot;

import net.famzangl.minecraft.minebot.ai.AIController;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid = "minebot-mod", name = "Minebot", version = "0.2.0")
public class MinebotMod {
	@Instance(value = "minebot-mod")
	public static MinebotMod instance;

	@EventHandler
	public void init(FMLInitializationEvent event) {
		// FMLCommonHandler.instance().bus().register(new KeyHandlers());
		FMLCommonHandler.instance().bus().register(new PlayerUpdateHandler());
		// FMLCommonHandler.instance().bus().register(new PlantKeyHandler());
		final AIController controller = new AIController();
		controller.initialize();
		// FMLCommonHandler.instance().onPlayerPostTick(player)
	}

	public static String getVersion() {
		return MinebotMod.class.getAnnotation(Mod.class).version();
	}

}
