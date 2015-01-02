package net.famzangl.minecraft.aimbow;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid="aimbow-mod", name = "AimBow", version = "0.1.0")
public class AimBowMod {

	@Instance(value = "minebot-mod")
	public static AimBowMod instance;

	@EventHandler
	public void init(FMLInitializationEvent event) {
		final AimBowController controller = new AimBowController();
		controller.initialize();
	}

	public static String getVersion() {
		return AimBowMod.class.getAnnotation(Mod.class).version();
	}

}
