package net.famzangl.minecraft.minebot.ai;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map.Entry;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.IAIControllable;
import net.famzangl.minecraft.minebot.ai.enchanting.EnchantStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.LayRailStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.LumberjackStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.MineStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.PlantStrategy;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MouseHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.WorldEvent;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * The main class that handles the bot.
 * 
 * @author michael
 * 
 */
public class AIController extends AIHelper implements IAIControllable {

	private static final int TIMEOUT = 5 * 20;

	private final static Hashtable<KeyBinding, AIStrategyFactory> uses = new Hashtable<KeyBinding, AIStrategyFactory>();

	protected static final KeyBinding stop = new KeyBinding("Stop",
			Keyboard.getKeyIndex("N"), "Command Mod");
	protected static final KeyBinding ungrab = new KeyBinding("Ungrab",
			Keyboard.getKeyIndex("U"), "Command Mod");

	static {
		final KeyBinding mine = new KeyBinding("Farm ores",
				Keyboard.getKeyIndex("K"), "Command Mod");
		final KeyBinding lumberjack = new KeyBinding("Farm wood",
				Keyboard.getKeyIndex("J"), "Command Mod");
		final KeyBinding build_rail = new KeyBinding("Build Minecart tracks",
				Keyboard.getKeyIndex("H"), "Command Mod");
		final KeyBinding mobfarm = new KeyBinding("Farm mobs",
				Keyboard.getKeyIndex("M"), "Command Mod");
		final KeyBinding plant = new KeyBinding("Plant seeds",
				Keyboard.getKeyIndex("P"), "Command Mod");
		uses.put(mine, new MineStrategy());
		uses.put(lumberjack, new LumberjackStrategy());
		uses.put(build_rail, new LayRailStrategy());
		uses.put(mobfarm, new EnchantStrategy());
		uses.put(plant, new PlantStrategy());
		ClientRegistry.registerKeyBinding(mine);
		ClientRegistry.registerKeyBinding(lumberjack);
		ClientRegistry.registerKeyBinding(build_rail);
		ClientRegistry.registerKeyBinding(mobfarm);
		ClientRegistry.registerKeyBinding(plant);
		ClientRegistry.registerKeyBinding(stop);
		ClientRegistry.registerKeyBinding(ungrab);
	}

	private final LinkedList<AITask> tasks = new LinkedList<AITask>();
	private boolean desync;
	private int timeout = 10 * 20;
	private boolean dead;
	private AIStrategy currentStrategy;

	private String strategyDescr = "";
	private final Object strategyDescrMutex = new Object();

	private AIStrategy requestedStrategy;

	private boolean nextPosIsPos2;

	private MarkerRenderer markerRenderer;

	private boolean skipNextTick;

	private boolean inUngrabMode;

	private MouseHelper oldMouseHelper;

	public AIController() {
		new AIChatController(this);
	}

	@Override
	public void addTask(AITask task) {
		if (task == null) {
			throw new NullPointerException();
		}
		tasks.add(task);
	}

	@Override
	public void desync() {
		System.out.println("Desync. This is an error. Did the server lag?");
		Thread.dumpStack();
		desync = true;
	}

	/**
	 * Checks if the Bot is active and what it should do.
	 * 
	 * @param evt
	 */
	@SubscribeEvent
	public void onPlayerTick(ClientTickEvent evt) {
		if (evt.phase != Phase.START || getMinecraft().thePlayer == null) {
			return;
		}
		if (skipNextTick) {
			skipNextTick = false;
			return;
		}
		testUngrabMode();
		invalidateObjectMouseOver();
		resetAllInputs();
		
		if (ungrab.isPressed()) {
			doUngrab = true;
		}

		AIStrategy newStrat;
		if (dead || stop.isPressed() || stop.getIsKeyPressed()) {
			dead = false;
			currentStrategy = null;
			tasks.clear();
			System.out.println("New Strategy: None");
			resetTimeout();
		} else if ((newStrat = findNewStrategy()) != null) {
			tasks.clear();
			currentStrategy = newStrat;
			System.out.println("New Strategy: " + newStrat);
			resetTimeout();
		} else if (desync) {
			tasks.clear();
		}
		desync = false;

		if (currentStrategy != null) {
			synchronized (strategyDescrMutex) {
				strategyDescr = currentStrategy.getDescription();
			}
			final AITask overrideTask = currentStrategy.getOverrideTask(this);
			if (overrideTask != null) {
				tasks.clear();
				tasks.push(overrideTask);
			} else if (tasks.isEmpty()) {
				currentStrategy.searchTasks(this);
				resetTimeout();
				System.out.println("Found new task: " + tasks.peekFirst());
			}

			if (tasks.isEmpty()) {
				dead = true;
			} else {
				final AITask task = tasks.get(0);
				if (task.isFinished(this)) {
					tasks.remove(0);
					resetTimeout();
					System.out.println("Next task: " + tasks.peekFirst());
					if (tasks.peekFirst() != null) {
						timeout = tasks.peekFirst().getGameTickTimeout();
					}
				} else if (timeout <= 0) {
					desync = true;
				} else {
					timeout--;
					try {
						task.runTick(this);
					} catch (final Throwable t) {
						t.printStackTrace();
						AIChatController
								.addChatLine("Unexpected Error ("
										+ t.getMessage()
										+ "). Please report (and send the output on the console)!");
					}
				}
			}
		} else {
			synchronized (strategyDescrMutex) {
				strategyDescr = "";
			}
		}
	}

	@SubscribeEvent
	public void drawHUD(RenderGameOverlayEvent.Post event) {
		if (event.type != ElementType.CHAT) {
			return;
		}
		if (doUngrab) {
			System.out.println("Un-grabbing mouse");
			// TODO: Reset this on grab
			getMinecraft().gameSettings.pauseOnLostFocus = false;
			// getMinecraft().mouseHelper.ungrabMouseCursor();
			if (getMinecraft().inGameHasFocus) {
				startUngrabMode();
			}
			doUngrab = false;
		}

		try {
			// Dynamic 1.7.2 / 1.7.10 fix.
			ScaledResolution res;
			Constructor<?> method = ScaledResolution.class.getConstructors()[0];
			Object arg1 = method.getParameterTypes()[0] == Minecraft.class ? getMinecraft()
					: getMinecraft().gameSettings;
			res = (ScaledResolution) method.newInstance(arg1,
					getMinecraft().displayWidth, getMinecraft().displayHeight);

			String str;
			synchronized (strategyDescrMutex) {
				str = strategyDescr;
			}
			getMinecraft().fontRenderer.drawStringWithShadow(
					str,
					res.getScaledWidth()
							- getMinecraft().fontRenderer.getStringWidth(str)
							- 10, 10, 16777215);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private synchronized void startUngrabMode() {
		getMinecraft().mouseHelper.ungrabMouseCursor();
		getMinecraft().inGameHasFocus = true;
		oldMouseHelper = getMinecraft().mouseHelper;
		getMinecraft().mouseHelper = new MouseHelper() {
			@Override
			public void mouseXYChange() {
			}
			@Override
			public void grabMouseCursor() {
			}
			@Override
			public void ungrabMouseCursor() {
			}
		};
	}
	
	private synchronized void testUngrabMode() {
		if (oldMouseHelper != null) {
			if (userTookOver()) {
				System.out.println("Preparing to re-grab the mouse.");
				// Tell minecraft what really happened.
				getMinecraft().mouseHelper = oldMouseHelper;
				getMinecraft().inGameHasFocus = false;
				getMinecraft().setIngameFocus();
				oldMouseHelper = null;
			}
		}
	}

	@SubscribeEvent
	public void resetOnGameEnd(WorldEvent.Unload unload) {
		dead = true;
		buildManager.reset();
	}

	/**
	 * Draws the position markers.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public void drawMarkers(RenderWorldLastEvent event) {
		final EntityLivingBase player = getMinecraft().renderViewEntity;
		if (player.getHeldItem() != null
				&& player.getHeldItem().getItem() == Items.wooden_axe) {
			final double x = player.lastTickPosX
					+ (player.posX - player.lastTickPosX) * event.partialTicks;
			final double y = player.lastTickPosY
					+ (player.posY - player.lastTickPosY) * event.partialTicks;
			final double z = player.lastTickPosZ
					+ (player.posZ - player.lastTickPosZ) * event.partialTicks;

			if (markerRenderer == null) {
				markerRenderer = new MarkerRenderer();
			}
			markerRenderer.render(x, y, z, pos1, pos2);
		}
	}

	public void positionMarkEvent(int x, int y, int z, int side) {
		final Pos pos = new Pos(x, y, z);
		setPosition(pos, nextPosIsPos2);
		nextPosIsPos2 ^= true;
	}

	private AIStrategy findNewStrategy() {
		if (requestedStrategy != null) {
			final AIStrategy r = requestedStrategy;
			requestedStrategy = null;
			return r;
		}

		for (final Entry<KeyBinding, AIStrategyFactory> e : uses.entrySet()) {
			if (e.getKey().isPressed()) {
				final AIStrategy strat = e.getValue().produceStrategy(this);
				if (strat != null) {
					return strat;
				}
			}
		}
		return null;
	}

	private void resetTimeout() {
		timeout = TIMEOUT;
	}

	@Override
	public AIHelper getAiHelper() {
		return this;
	}

	@Override
	public void requestUseStrategy(AIStrategy strategy) {
		System.out.println("Request to use " + strategy);
		requestedStrategy = strategy;
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void playerInteract(PlayerInteractEvent event) {
		final ItemStack stack = event.entityPlayer.inventory.getCurrentItem();
		if (stack != null && stack.getItem() == Items.wooden_axe) {
			if (event.action == Action.RIGHT_CLICK_BLOCK) {
				if (event.entityPlayer.worldObj.isRemote) {
					positionMarkEvent(event.x, event.y, event.z, 0);
				}
			}
		}
	}

	public void initialize() {
		FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);

		// registerAxe();
	}

}
