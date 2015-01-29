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
package net.famzangl.minecraft.aimbow;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

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
