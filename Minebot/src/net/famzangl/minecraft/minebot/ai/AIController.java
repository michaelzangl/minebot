package net.famzangl.minecraft.minebot.ai;

import java.lang.reflect.Field;
import java.util.BitSet;
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
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
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
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AIController extends AIHelper implements IAIControllable {

	private final static Hashtable<KeyBinding, AIStrategyFactory> uses = new Hashtable<KeyBinding, AIStrategyFactory>();

	protected static KeyBinding stop = new KeyBinding("Stop",
			Keyboard.getKeyIndex("N"), "Command Mod");

	static {
		KeyBinding mine = new KeyBinding("Farm ores",
				Keyboard.getKeyIndex("K"), "Command Mod");
		KeyBinding lumberjack = new KeyBinding("Farm wood",
				Keyboard.getKeyIndex("J"), "Command Mod");
		KeyBinding build_rail = new KeyBinding("Build Minecart tracks",
				Keyboard.getKeyIndex("H"), "Command Mod");
		KeyBinding mobfarm = new KeyBinding("Farm mobs",
				Keyboard.getKeyIndex("M"), "Command Mod");
		KeyBinding plant = new KeyBinding("Plant seeds",
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

	public AIController() {
		new AIChatController(this);
	}

	@Override
	public void addTask(AITask task) {
		tasks.add(task);
	}

	@Override
	public void desync() {
		System.out.println("Desync. This is an error. Did the server lag?");
		Thread.dumpStack();
		desync = true;
	}

	@SubscribeEvent
	public void onPlayerTick(ClientTickEvent evt) {
		if (evt.phase != Phase.START || getMinecraft().thePlayer == null) {
			return;
		}
		invalidateObjectMouseOver();
		resetAllInputs();

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
			AITask overrideTask = currentStrategy.getOverrideTask(this);
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
				AITask task = tasks.get(0);
				if (task.isFinished(this)) {
					tasks.remove(0);
					resetTimeout();
					System.out.println("Next task: " + tasks.peekFirst());
				} else if (timeout <= 0) {
					desync = true;
				} else {
					timeout--;
					try {
						task.runTick(this);
					} catch (Throwable t) {
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
			getMinecraft().setIngameNotInFocus();
			doUngrab = false;
		}

		ScaledResolution res = new ScaledResolution(
				getMinecraft().gameSettings, getMinecraft().displayWidth,
				getMinecraft().displayHeight);
		String str;
		synchronized (strategyDescrMutex) {
			str = strategyDescr;
		}
		getMinecraft().fontRenderer.drawStringWithShadow(
				str,
				res.getScaledWidth()
						- getMinecraft().fontRenderer.getStringWidth(str) - 10,
				10, 16777215);
	}

	@SubscribeEvent
	public void resetOnGameEnd(WorldEvent.Unload unload) {
		dead = true;
		buildManager.reset();
	}

	@SubscribeEvent
	public void drawMarkers(RenderWorldLastEvent event) {
		EntityLivingBase player = getMinecraft().renderViewEntity;
		if (player.getHeldItem() != null
				&& player.getHeldItem().getItem() == Items.wooden_axe) {
			double x = player.lastTickPosX
					+ (player.posX - player.lastTickPosX)
					* (double) event.partialTicks;
			double y = player.lastTickPosY
					+ (player.posY - player.lastTickPosY)
					* (double) event.partialTicks;
			double z = player.lastTickPosZ
					+ (player.posZ - player.lastTickPosZ)
					* (double) event.partialTicks;

			if (markerRenderer == null) {
				markerRenderer = new MarkerRenderer();
			}
			markerRenderer.render(x, y, z, pos1, pos2);
		}
	}

	public void positionMarkEvent(int x, int y, int z, int side) {
		Pos pos = new Pos(x, y, z);
		setPosition(pos, nextPosIsPos2);
		nextPosIsPos2 ^= true;
	}

	private AIStrategy findNewStrategy() {
		if (requestedStrategy != null) {
			AIStrategy r = requestedStrategy;
			requestedStrategy = null;
			return r;
		}

		for (Entry<KeyBinding, AIStrategyFactory> e : uses.entrySet()) {
			if (e.getKey().isPressed()) {
				AIStrategy strat = e.getValue().produceStrategy(this);
				if (strat != null) {
					return strat;
				}
			}
		}
		return null;
	}

	private void resetTimeout() {
		timeout = 5 * 20;
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
		ItemStack stack = event.entityPlayer.inventory.getCurrentItem();
		if (stack != null && stack.getItem() == Items.wooden_axe) {
			if (event.action == Action.RIGHT_CLICK_BLOCK) {
				Thread.dumpStack();
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

	private void registerAxe() {
		ItemAxe old = (ItemAxe) Item.itemRegistry.getObject("wooden_axe");
		MarkingAxe axe = (new MarkingAxe(Item.ToolMaterial.WOOD, this));
		axe.setUnlocalizedName("hatchetWood").setTextureName("wood_axe");
		axe.setIcon(old.getIconFromDamage(0));

		// call the real Item.itemRegistry.addObject(271, "wooden_axe", axe);
		try {
			// clear bit in availability map
			Field availabilityMapField = Item.itemRegistry.getClass()
					.getDeclaredField("availabilityMap");
			availabilityMapField.setAccessible(true);
			BitSet map = (BitSet) availabilityMapField.get(Item.itemRegistry);
			map.clear(271);

			// set namespace
			// Loader.instance().modController = null
			Field modControllerField = Loader.instance().getClass()
					.getDeclaredField("modController");
			modControllerField.setAccessible(true);
			Object oldModController = modControllerField.get(Loader.instance());
			modControllerField.set(Loader.instance(), null);

			Item.itemRegistry.addObject(271, "minecraft:wooden_axe", axe);
			modControllerField.set(Loader.instance(), oldModController);

			System.out.println("Registered wooden axe: "
					+ Item.itemRegistry.getObject("wooden_axe"));

			for (Object r : CraftingManager.getInstance().getRecipeList()) {
				if (r instanceof ShapedRecipes) {
					ShapedRecipes recipe = (ShapedRecipes) r;
					ItemStack out = recipe.getRecipeOutput();
					if (out != null && out.getItem() == old) {
						out.func_150996_a(axe);
						System.out.println("Repalced axe...");
					}
				}
			}
		} catch (Throwable t) {
			System.err.println("Could not register axe");
			t.printStackTrace();
		}
	}
}
