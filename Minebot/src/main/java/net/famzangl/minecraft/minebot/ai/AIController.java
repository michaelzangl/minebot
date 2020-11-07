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

import com.mojang.blaze3d.matrix.MatrixStack;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.IAIControllable;
import net.famzangl.minecraft.minebot.ai.command.SafeStrategyRule;
import net.famzangl.minecraft.minebot.ai.command.StackBuilder;
import net.famzangl.minecraft.minebot.ai.net.MinebotNetHandler;
import net.famzangl.minecraft.minebot.ai.net.NetworkHelper;
import net.famzangl.minecraft.minebot.ai.profiler.InterceptingProfiler;
import net.famzangl.minecraft.minebot.ai.render.BuildMarkerRenderer;
import net.famzangl.minecraft.minebot.ai.render.MinebotDebugRenderer;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy.TickResult;
import net.famzangl.minecraft.minebot.ai.strategy.RunOnceStrategy;
import net.famzangl.minecraft.minebot.ai.utils.PrivateFieldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHelper;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Hashtable;
import java.util.Map.Entry;

/**
 * The main class that handles the bot.
 * 
 * @author michael
 * 
 */
public class AIController extends AIHelper implements IAIControllable {
	private final StackBuilder stackBuilder = new StackBuilder();

	private final class UngrabMouseHelper extends MouseHelper {
		public UngrabMouseHelper(Minecraft minecraftIn) {
			super(minecraftIn);
		}

		@Override
		public void grabMouse() {
		}

		@Override
		public void ungrabMouse() {
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
			InputMappings.getInputByName("key.keyboard.n").getKeyCode(), "Command Mod");
	protected static final KeyBinding ungrab = new KeyBinding("Ungrab",
			InputMappings.getInputByName("key.keyboard.u").getKeyCode(), "Command Mod");

	static {
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

	private MouseHelper oldMouseHelper;

	private BuildMarkerRenderer buildMarkerRenderer;
	private NetworkHelper networkHelper;
	private InterceptingProfiler profilerHelper;
	private boolean displayWasActiveSinceUngrab;

	public AIController() {
	}

	public void connect(ClientPlayerNetworkEvent.LoggedInEvent e) {
		AIChatController.getRegistry().setControlled(this);

		networkHelper = MinebotNetHandler.inject(getMinecraft().getConnection());

		// We use a fake debug renderer to render our Minebot debug info.
		// Field: Minecraft#debugRenderer is final
		PrivateFieldUtils.setFieldValue(getMinecraft(), Minecraft.class, DebugRenderer.class,
				new MinebotDebugRenderer(getMinecraft(), this, () -> this.currentStrategy));
	}

	/**
	 * Checks if the Bot is active and what it should do.
	 * 
	 * @param evt
	 */
	public void onPlayerTick(TickEvent.ClientTickEvent evt) {
		if (evt.phase != TickEvent.ClientTickEvent.Phase.START) {
			return;
		} else if (getMinecraft().player == null
				|| getMinecraft().world == null) {
			LOGGER.debug(MARKER_STRATEGY,
					"Player tick but player is not in world.");
			return;
		}

		LOGGER.debug(MARKER_STRATEGY, "Strategy game tick. World time: "
				+ getMinecraft().world.getGameTime());
		testUngrabMode();
		invalidateObjectMouseOver();
		resetAllInputs();
		invalidateChunkCache();

		if (ungrab.isPressed()) {
			doUngrab = true;
		}

		getStats().setGameTickTimer(getWorld());
		
		AIStrategy newStrategy;
		if (dead || /*stop.isPressed() ||*/ stop.isKeyDown()) {
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

	public void onUseHoe(UseHoeEvent evt) {
		LOGGER.debug(MARKER_EVENT, "Hoe used at " + evt.getContext().getPos());
	}

	private boolean isStopPressed() {
		return stop.isPressed() || stop.isKeyDown(); // TODO? || keyboard.isKeyDown(stop.getKey().getKeyCode());
	}

	private void deactivateCurrentStrategy() {
		if (currentStrategy != null) {
			LOGGER.trace(MARKER_STRATEGY, "Deactivating strategy: "
					+ currentStrategy);
			currentStrategy.setActive(false, this);
		}
		currentStrategy = null;
	}

	// TODO: Use RenderWorldLastEvent?
	public void drawHUD(TickEvent.RenderTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		if (doUngrab) {
			LOGGER.trace(MARKER_MOUSE, "Un-grabbing mouse");
			// TODO: Reset this on grab
			getMinecraft().gameSettings.pauseOnLostFocus = false;
			// getMinecraft().mouseHelper.ungrabMouseCursor();
			if (getMinecraft().isGameFocused()) {
				startUngrabMode();
			}
			doUngrab = false;
		}

		String[] str;
		synchronized (strategyDescrMutex) {
			str = (strategyDescr == null ? "?" : strategyDescr).split("\n");
		}
		int y = 10;
		for (String s : str) {
			//getMinecraft().fontRenderer.showTextWithShadow or something is what this used to be
			//D
			getMinecraft().fontRenderer.func_238405_a_(new MatrixStack(), s,
					getMinecraft().getMainWindow().getScaledWidth()
							- getMinecraft().fontRenderer.getStringWidth(s)
							- 10, y, 16777215);
			y += 15;

		}
		
	}

	private synchronized void startUngrabMode() {
		LOGGER.trace(MARKER_MOUSE, "Starting mouse ungrab");
		getMinecraft().mouseHelper.ungrabMouse();
		getMinecraft().setGameFocused(true);
		if (!(getMinecraft().mouseHelper instanceof UngrabMouseHelper)) {
			LOGGER.trace(MARKER_MOUSE, "Storing old mouse helper.");
			oldMouseHelper = getMinecraft().mouseHelper;
		}
		// The field is final, so we need to set it.
		setMouseHelper(new UngrabMouseHelper(getMinecraft()));
		displayWasActiveSinceUngrab = true;
	}

	private void setMouseHelper(MouseHelper mouseHelper) {
		PrivateFieldUtils.setFieldValue(getMinecraft(), Minecraft.class,
				MouseHelper.class, mouseHelper);
	}

	private synchronized void testUngrabMode() {
		boolean active = true;// TODO Display.isActive();
		displayWasActiveSinceUngrab &= active;
		if (oldMouseHelper != null) {
			if ((userTookOver() || !displayWasActiveSinceUngrab) && active) {
				LOGGER.debug(MARKER_MOUSE, "Preparing to re-grab the mouse.");
				// Tell minecraft what really happened.
				setMouseHelper(oldMouseHelper);
				getMinecraft().setGameFocused(false);
				oldMouseHelper = null;
			}
		}
	}

	public void resetOnGameEnd(PlayerEvent.PlayerChangedDimensionEvent unload) {
		LOGGER.trace(MARKER_EVENT, "Unloading world.");
		dead = true;
		buildManager.reset();
		setActiveMapReader(null);
	}

	public void resetOnGameEnd2(PlayerEvent.PlayerRespawnEvent unload) {
		resetOnGameEnd(null);
	}

	/**
	 * Draws the position markers.
	 * 
	 * @param event
	 */
	public void beforeDrawMarkers(TickEvent.RenderTickEvent event) {
		if (event.phase != TickEvent.Phase.START) {
			return;
		}
		// We could do profilerHelper = InterceptingProfiler.inject(getMinecraft());
		// But it is much better to fake a debug renderer
	}

	public void positionMarkEvent(int x, int y, int z, int side) {
		// TODO: Reactivate right mouse click with axe
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
	public int requestUseStrategy(AIStrategy strategy) {
		return requestUseStrategy(strategy, SafeStrategyRule.NONE);
	}

	@Override
	public int requestUseStrategy(AIStrategy strategy, SafeStrategyRule rule) {
		if (stackBuilder.collect(strategy, rule)) {
			// We are in strack building mode (/minebot stack), only schedule strategy
			LOGGER.debug(MARKER_STRATEGY, "Scheduled strategy for stack: {}", strategy);
			AIChatController.addChatLine("Strategy scheduled. To start, use: /minebot stack done");
		} else {
			LOGGER.debug(MARKER_STRATEGY, "Request to use strategy {} using saferule {}", strategy, rule);
			requestedStrategy = rule.makeSafe(strategy);
		}
		return 1;
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

	public void initialize(IEventBus bus) {
		bus.addListener(this::connect);
		bus.addListener(this::onPlayerTick);
		bus.addListener(this::onUseHoe);
		bus.addListener(this::drawHUD);
		bus.addListener(this::resetOnGameEnd);
		bus.addListener(this::resetOnGameEnd2);
		bus.addListener(this::beforeDrawMarkers);
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


	@Override
	public StackBuilder getStackBuilder() {
		return stackBuilder;
	}
}
