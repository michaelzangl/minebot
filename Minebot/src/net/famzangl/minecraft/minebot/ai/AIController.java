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
package net.famzangl.minecraft.minebot.ai;

import java.util.Hashtable;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.IAIControllable;
import net.famzangl.minecraft.minebot.ai.net.MinebotNetHandler;
import net.famzangl.minecraft.minebot.ai.net.NetworkHelper;
import net.famzangl.minecraft.minebot.ai.profiler.InterceptingProfiler;
import net.famzangl.minecraft.minebot.ai.render.BuildMarkerRenderer;
import net.famzangl.minecraft.minebot.ai.render.PosMarkerRenderer;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy.TickResult;
import net.famzangl.minecraft.minebot.ai.strategy.RunOnceStrategy;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;
import net.minecraft.util.MouseHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

/**
 * The main class that handles the bot.
 * 
 * @author michael
 * 
 */
public class AIController extends AIHelper implements IAIControllable {
	private final class UngrabMouseHelper extends MouseHelper {
		@Override
		public void mouseXYChange() {
		}

		@Override
		public void grabMouseCursor() {
		}

		@Override
		public void ungrabMouseCursor() {
		}
	}

	private static final Marker MARKER_EVENT = MarkerManager.getMarker("event");
	private static final Marker MARKER_STRATEGY = MarkerManager
			.getMarker("strategy");
	private static final Marker MARKER_MOUSE = MarkerManager.getMarker("mouse");
	private static final Logger LOGGER = LogManager
			.getLogger(AIController.class);

	private final static Hashtable<KeyBinding, AIStrategyFactory> uses = new Hashtable<KeyBinding, AIStrategyFactory>();

	protected static final KeyBinding stop = new KeyBinding("Stop",
			Keyboard.getKeyIndex("N"), "Command Mod");
	protected static final KeyBinding ungrab = new KeyBinding("Ungrab",
			Keyboard.getKeyIndex("U"), "Command Mod");

	static {
		// final KeyBinding mine = new KeyBinding("Farm ores",
		// Keyboard.getKeyIndex("K"), "Command Mod");
		// final KeyBinding lumberjack = new KeyBinding("Farm wood",
		// Keyboard.getKeyIndex("J"), "Command Mod");
		// final KeyBinding build_rail = new KeyBinding("Build Minecart tracks",
		// Keyboard.getKeyIndex("H"), "Command Mod");
		// final KeyBinding mobfarm = new KeyBinding("Farm mobs",
		// Keyboard.getKeyIndex("M"), "Command Mod");
		// final KeyBinding plant = new KeyBinding("Plant seeds",
		// Keyboard.getKeyIndex("P"), "Command Mod");
		// uses.put(mine, new MineStrategy());
		// uses.put(lumberjack, new LumberjackStrategy());
		// uses.put(build_rail, new LayRailStrategy());
		// uses.put(mobfarm, new EnchantStrategy());
		// uses.put(plant, new PlantStrategy());
		// ClientRegistry.registerKeyBinding(mine);
		// ClientRegistry.registerKeyBinding(lumberjack);
		// ClientRegistry.registerKeyBinding(build_rail);
		// ClientRegistry.registerKeyBinding(mobfarm);
		// ClientRegistry.registerKeyBinding(plant);
		ClientRegistry.registerKeyBinding(stop);
		ClientRegistry.registerKeyBinding(ungrab);
	}

	private boolean dead;
	private volatile AIStrategy currentStrategy;

	private AIStrategy deactivatedStrategy;

	private String strategyDescr = "";
	private final Object strategyDescrMutex = new Object();

	private AIStrategy requestedStrategy;

	private boolean nextPosIsPos2;

	private PosMarkerRenderer markerRenderer;

	private MouseHelper oldMouseHelper;

	private BuildMarkerRenderer buildMarkerRenderer;
	private NetworkHelper networkHelper;
	private InterceptingProfiler profilerHelper;
	private RenderTickEvent activeDrawEvent;
	private boolean displayWasActiveSinceUngrab;

	public AIController() {
		AIChatController.getRegistry().setControlled(this);
	}

	@SubscribeEvent
	public void connect(ClientConnectedToServerEvent e) {
		networkHelper = MinebotNetHandler.inject(this, e.getManager(), e.getHandler());
		profilerHelper = InterceptingProfiler.inject(getMinecraft());
		// Hook into
		// net.minecraft.client.renderer.RenderGlobal.drawBlockDamageTexture(Tessellator,
		// WorldRenderer, Entity, float)
		profilerHelper.addLisener("hand", new Runnable() {
			@Override
			public void run() {
				drawMakers();
			}
		});
	}

	/**
	 * Checks if the Bot is active and what it should do.
	 * 
	 * @param evt
	 */
	@SubscribeEvent
	public void onPlayerTick(ClientTickEvent evt) {
		if (evt.phase != ClientTickEvent.Phase.START) {
			return;
		} else if (getMinecraft().player == null
				|| getMinecraft().world == null) {
			LOGGER.debug(MARKER_STRATEGY,
					"Player tick but player is not in world.");
			return;
		}

		LOGGER.debug(MARKER_STRATEGY, "Strategy game tick. World time: "
				+ getMinecraft().world.getTotalWorldTime());
		testUngrabMode();
		invalidateObjectMouseOver();
		resetAllInputs();
		invalidateChunkCache();

		if (ungrab.isPressed()) {
			doUngrab = true;
		}

		getStats().setGameTickTimer(getWorld());
		
		AIStrategy newStrategy;
		if (dead || stop.isPressed() || stop.isKeyDown()) {
			// FIXME: Better way to determine of this stategy can be resumed.
			if (deactivatedStrategy == null
					&& !(currentStrategy instanceof RunOnceStrategy)) {
				LOGGER.trace(MARKER_STRATEGY,
						"Store strategy to be resumed later: "
								+ currentStrategy);
				deactivatedStrategy = currentStrategy;
			}
			deactivateCurrentStrategy();
			dead = false;
		} else if ((newStrategy = findNewStrategy()) != null) {
			deactivateCurrentStrategy();
			currentStrategy = newStrategy;
			deactivatedStrategy = null;
			LOGGER.debug(MARKER_STRATEGY, "Using new root strategy: "
					+ newStrategy);
			currentStrategy.setActive(true, this);
		}

		if (currentStrategy != null) {
			TickResult result = null;
			for (int i = 0; i < 100; i++) {
				result = currentStrategy.gameTick(this);
				if (result != TickResult.TICK_AGAIN) {
					break;
				}
				LOGGER.trace(MARKER_STRATEGY,
						"Strategy requests to tick again.");
			}
			if (result == TickResult.ABORT || result == TickResult.NO_MORE_WORK) {
				LOGGER.debug(MARKER_STRATEGY, "Strategy is dead.");
				dead = true;
			}
			synchronized (strategyDescrMutex) {
				strategyDescr = currentStrategy.getDescription(this);
			}
		} else {
			synchronized (strategyDescrMutex) {
				strategyDescr = "";
			}
		}
		
		keyboardPostTick();
		LOGGER.debug(MARKER_STRATEGY, "Strategy game tick done");

		if (activeMapReader != null) {
			activeMapReader.tick(this);
		}

	}

	private boolean isStopPressed() {
		return stop.isPressed() || stop.isKeyDown() || Keyboard.isKeyDown(stop.getKeyCode());
	}

	private void deactivateCurrentStrategy() {
		if (currentStrategy != null) {
			LOGGER.trace(MARKER_STRATEGY, "Deactivating strategy: "
					+ currentStrategy);
			currentStrategy.setActive(false, this);
		}
		currentStrategy = null;
	}

	@SubscribeEvent
	public void drawHUD(RenderTickEvent event) {
		if (event.phase != Phase.END) {
			return;
		}
		if (doUngrab) {
			LOGGER.trace(MARKER_MOUSE, "Un-grabbing mouse");
			// TODO: Reset this on grab
			getMinecraft().gameSettings.pauseOnLostFocus = false;
			// getMinecraft().mouseHelper.ungrabMouseCursor();
			if (getMinecraft().inGameHasFocus) {
				startUngrabMode();
			}
			doUngrab = false;
		}

		ScaledResolution res = new ScaledResolution(getMinecraft());

		String[] str;
		synchronized (strategyDescrMutex) {
			str = (strategyDescr == null ? "?" : strategyDescr).split("\n");
		}
		int y = 10;
		for (String s : str) {
			getMinecraft().fontRendererObj.drawStringWithShadow(
					s,
					res.getScaledWidth()
							- getMinecraft().fontRendererObj.getStringWidth(s)
							- 10, y, 16777215);
			y += 15;
		}
	}

	private synchronized void startUngrabMode() {
		LOGGER.trace(MARKER_MOUSE, "Starting mouse ungrab");
		getMinecraft().mouseHelper.ungrabMouseCursor();
		getMinecraft().inGameHasFocus = true;
		if (!(getMinecraft().mouseHelper instanceof UngrabMouseHelper)) {
			LOGGER.trace(MARKER_MOUSE, "Storing old mouse helper.");
			oldMouseHelper = getMinecraft().mouseHelper;
		}
		getMinecraft().mouseHelper = new UngrabMouseHelper();
		displayWasActiveSinceUngrab = true;
	}

	private synchronized void testUngrabMode() {
		displayWasActiveSinceUngrab &= Display.isActive();
		if (oldMouseHelper != null) {
			if ((userTookOver() || !displayWasActiveSinceUngrab) && Display.isActive()) {
				LOGGER.debug(MARKER_MOUSE, "Preparing to re-grab the mouse.");
				// Tell minecraft what really happened.
				getMinecraft().mouseHelper = oldMouseHelper;
				getMinecraft().inGameHasFocus = false;
				getMinecraft().setIngameFocus();
				oldMouseHelper = null;
			}
		}
	}

	@SubscribeEvent
	public void resetOnGameEnd(PlayerChangedDimensionEvent unload) {
		LOGGER.trace(MARKER_EVENT, "Unloading world.");
		dead = true;
		buildManager.reset();
		setActiveMapReader(null);
	}

	@SubscribeEvent
	public void resetOnGameEnd2(PlayerRespawnEvent unload) {
		resetOnGameEnd(null);
	}

	/**
	 * Draws the position markers.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public void beforeDrawMarkers(RenderTickEvent event) {
		if (event.phase != Phase.START) {
			return;
		}
		activeDrawEvent = event;
	}

	public void drawMakers() {
		final Entity view = getMinecraft().getRenderViewEntity();
		if (!(view instanceof EntityPlayerSP)) {
			return;
		}
		EntityPlayerSP player = (EntityPlayerSP) view;
		if (player.getHeldItem(EnumHand.MAIN_HAND) != null
				&& player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.WOODEN_AXE) {
			if (markerRenderer == null) {
				markerRenderer = new PosMarkerRenderer(1, 0, 0);
			}
			markerRenderer.render(activeDrawEvent, this, pos1, pos2);
		} else if (player.getHeldItem(EnumHand.MAIN_HAND) != null
				&& player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.STICK) {
			if (buildMarkerRenderer == null) {
				buildMarkerRenderer = new BuildMarkerRenderer();
			}
			buildMarkerRenderer.render(activeDrawEvent, this);
		}
		AIStrategy strategy = currentStrategy;
		if (strategy != null) {
			strategy.drawMarkers(activeDrawEvent, this);
		}
	}

	public void positionMarkEvent(int x, int y, int z, int side) {
		final BlockPos pos = new BlockPos(x, y, z);
		setPosition(pos, nextPosIsPos2);
		nextPosIsPos2 ^= true;
	}

	private AIStrategy findNewStrategy() {
		if (requestedStrategy != null) {
			final AIStrategy strategy = requestedStrategy;
			requestedStrategy = null;
			return strategy;
		}

		for (final Entry<KeyBinding, AIStrategyFactory> e : uses.entrySet()) {
			if (e.getKey().isPressed()) {
				final AIStrategy strategy = e.getValue().produceStrategy(this);
				if (strategy != null) {
					return strategy;
				}
			}
		}
		return null;
	}

	@Override
	public AIHelper getAiHelper() {
		return this;
	}

	@Override
	public void requestUseStrategy(AIStrategy strategy) {
		LOGGER.trace(MARKER_STRATEGY, "Request to use strategy " + strategy);
		requestedStrategy = strategy;
	}

	// @SubscribeEvent
	// @SideOnly(Side.CLIENT)
	// public void playerInteract(PlayerInteractEvent event) {
	// final ItemStack stack = event.entityPlayer.inventory.getCurrentItem();
	// if (stack != null && stack.getItem() == Items.wooden_axe) {
	// if (event.action == Action.RIGHT_CLICK_BLOCK) {
	// if (event.entityPlayer.worldObj.isRemote) {
	// positionMarkEvent(event.x, event.y, event.z, 0);
	// }
	// }
	// }
	// }

	public void initialize() {
		FMLCommonHandler.instance().bus().register(this);

		// registerAxe();
	}

	@Override
	public AIStrategy getResumeStrategy() {
		return deactivatedStrategy;
	}

	@Override
	public NetworkHelper getNetworkHelper() {
		return networkHelper;
	}

}
