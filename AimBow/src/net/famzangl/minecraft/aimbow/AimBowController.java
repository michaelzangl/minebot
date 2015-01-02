package net.famzangl.minecraft.aimbow;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.famzangl.minecraft.aimbow.aiming.ColissionData;
import net.famzangl.minecraft.aimbow.aiming.ColissionSolver;
import net.famzangl.minecraft.aimbow.aiming.ReverseBowSolver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class AimBowController {
	protected static final KeyBinding autoAimKey = new KeyBinding("Auto aim",
			Keyboard.getKeyIndex("Y"), "AimBow");
	static {
		ClientRegistry.registerKeyBinding(autoAimKey);
	}
	FloatBuffer modelBuffer = BufferUtils.createFloatBuffer(16);
	FloatBuffer projectionBuffer = BufferUtils.createFloatBuffer(16);
	IntBuffer viewPort = BufferUtils.createIntBuffer(4);
	/**
	 * Remporarely used.
	 */
	private final FloatBuffer win_pos = BufferUtils.createFloatBuffer(3);
	private final double zLevel = 0;
	Minecraft mc = Minecraft.getMinecraft();

	private final ZoomController zc = new ZoomController();
	private boolean autoAim;

	/**
	 * Checks if the Bot is active and what it should do.
	 * 
	 * @param evt
	 */
	@SubscribeEvent
	public void onPlayerTick(ClientTickEvent evt) {
		if (evt.phase != Phase.START || mc.thePlayer == null) {
			return;
		}
		if (autoAimKey.isPressed()) {
			autoAim = !autoAim;
			
		Minecraft.getMinecraft().thePlayer
				.addChatMessage(new ChatComponentText("Autoaim: " + (autoAim ? "On" : "Off")));
		}
	}
	
	@SubscribeEvent
	public void stealProjectionMatrix(RenderWorldLastEvent event) {
		modelBuffer.rewind();
		projectionBuffer.rewind();
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelBuffer);
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projectionBuffer);
	}

	@SubscribeEvent
	public void drawHUD(RenderGameOverlayEvent.Pre event) {
		if (event.type != ElementType.CROSSHAIRS) {
			return;
		}

		EntityClientPlayerMP player = mc.thePlayer;
		ItemStack heldItem = player.getHeldItem();
		if (heldItem != null && heldItem.getItem() == Items.bow) {
			boolean colissionDrawn = false;
			int count = mc.thePlayer.getItemInUseCount();

			ColissionSolver colissionSolver = new ColissionSolver(mc,
					mc.renderViewEntity);
			ArrayList<ColissionData> colissionPoints = colissionSolver
					.computeCurrentColissionPoints();
			for (ColissionData p : colissionPoints) {
				Pos2 pos = getPositionOnScreen(mc, p.x, p.y, p.z, event);
				boolean hit = p.hitEntity != null;
				drawCrosshairAt(mc, pos.x, pos.y, hit ? 0 : 1, hit ? 1 : 0, 0);
				zc.zoomTowards(Vec3.createVectorHelper(p.x, p.y, p.z),
						event.partialTicks);
				if (!colissionDrawn && !hit && autoAim && count > 0) {
					aimAtCloseEntity(pos, event);
				}
				colissionDrawn = true;
			}
			if (!colissionDrawn) {
				int x = event.resolution.getScaledWidth() / 2;
				int y = event.resolution.getScaledHeight() / 2;
				drawCrosshairAt(mc, x, y, .6f, .6f, .6f);
			}
			if (count == 0 || !colissionDrawn) {
				zc.apply(0);
				zc.reset();
			} else {
				float f1 = count / 20.0F;

				if (f1 > 1.0F) {
					f1 = 1.0F;
				} else {
					f1 *= f1;
				}
				zc.apply(f1);
			}

			event.setCanceled(true);
		} else {
			zc.reset();
		}
	}

	private void drawCrosshairAt(Minecraft mc, int x, int y, float r, float g,
			float b) {
		mc.getTextureManager().bindTexture(Gui.icons);
		GL11.glEnable(GL11.GL_BLEND);
		drawTexturedModalRect(x - 7, y - 7, 0, 0, 16, 16, r, g, b);
		GL11.glDisable(GL11.GL_BLEND);
	}

	/**
	 * Draws a textured rectangle at the stored z-value. Args: x, y, u, v,
	 * width, height
	 * 
	 * @param b
	 * @param g
	 * @param r
	 */
	public void drawTexturedModalRect(int par1, int par2, int par3, int par4,
			int par5, int par6, float r, float g, float b) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_F(r, g, b, .5f);
		tessellator.addVertexWithUV(par1 + 0, par2 + par6, this.zLevel,
				(par3 + 0) * f, (par4 + par6) * f1);
		tessellator.addVertexWithUV(par1 + par5, par2 + par6, this.zLevel,
				(par3 + par5) * f, (par4 + par6) * f1);
		tessellator.addVertexWithUV(par1 + par5, par2 + 0, this.zLevel,
				(par3 + par5) * f, (par4 + 0) * f1);
		tessellator.addVertexWithUV(par1 + 0, par2 + 0, this.zLevel, (par3 + 0)
				* f, (par4 + 0) * f1);
		tessellator.draw();
	}

	public Pos2 getPositionOnScreen(Minecraft mc, double x, double y, double z,
			Pre event) {
		Vec3 player = mc.renderViewEntity.getPosition(event.partialTicks);
		viewPort.rewind();
		viewPort.put(0);
		viewPort.put(0);
		viewPort.put(event.resolution.getScaledWidth());
		viewPort.put(event.resolution.getScaledHeight());
		viewPort.rewind();

		win_pos.rewind();
		modelBuffer.rewind();
		projectionBuffer.rewind();
		GLU.gluProject((float) (x - player.xCoord),
				(float) (y - player.yCoord), (float) (z - player.zCoord),
				modelBuffer, projectionBuffer, viewPort, win_pos);

		win_pos.rewind();
		int sx = (int) win_pos.get();
		int sy = event.resolution.getScaledHeight() - (int) win_pos.get();
		if (win_pos.get() < 1) {
			return new Pos2(sx, sy);
		} else {
			return new Pos2(-100, -100);
		}
	}

	private static class CloseEntity implements Comparable<CloseEntity> {
		private final Entity entity;
		private final double distance;

		public CloseEntity(Entity entity, double distance) {
			super();
			this.entity = entity;
			this.distance = distance;
		}

		@Override
		public int compareTo(CloseEntity o) {
			return Double.compare(distance, o.distance);
		}

		@Override
		public String toString() {
			return "CloseEntity [entity=" + entity + ", distance=" + distance
					+ "]";
		}

	}

	public void aimAtCloseEntity(Pos2 toScreenPos, Pre event) {
		List<Entity> entities = mc.theWorld.getEntitiesWithinAABB(Entity.class,
				mc.thePlayer.boundingBox.expand(200, 100, 200));

		ArrayList<CloseEntity> nearEntities = new ArrayList<CloseEntity>();

		for (Entity e : entities) {
			if (e.canBeCollidedWith()
					&& e != Minecraft.getMinecraft().thePlayer) {
				Pos2 onScreen = getPositionOnScreen(mc, e.posX, e.posY, e.posZ,
						event);
				double d = toScreenPos.distanceTo(onScreen);
				if (d < 100) {
					nearEntities.add(new CloseEntity(e, d));
				}
			}
		}
		Collections.sort(nearEntities);

		ReverseBowSolver rbs = new ReverseBowSolver();

		ColissionSolver colissionSolver = new ColissionSolver(mc,
				mc.renderViewEntity);
		for (CloseEntity e : nearEntities) {
			Vec3 look = rbs.getLookForTarget(e.entity);
			ArrayList<ColissionData> foundColissions = colissionSolver
					.computeColissionWithLook(look);
			if (foundColissions.size() > 0
					&& foundColissions.get(0).hitEntity == e.entity) {
//				System.out.println("Positive: " + e.entity);
				lookAt(look);
				break;
			} else {
//				System.out.println("Negative: " + e + ", got: "
//						+ foundColissions);
			}
		}
	}

	private void lookAt(Vec3 look) {
		final double d0 = look.xCoord;
		final double d1 = look.zCoord;
		final double d2 = look.yCoord;
		final double d3 = d0 * d0 + d2 * d2 + d1 * d1;

		if (d3 >= 2.500000277905201E-7D) {
			final float rotationYaw = mc.thePlayer.rotationYaw;
			final float rotationPitch = mc.thePlayer.rotationPitch;

			final float yaw = (float) (Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
			final float pitch = (float) -(Math.atan2(d2,
					Math.sqrt(d0 * d0 + d1 * d1)) * 180.0D / Math.PI);
			mc.thePlayer.setAngles((yaw - rotationYaw) / 0.15f,
					-(pitch - rotationPitch) / 0.15f);
		}
	}

	// public Pos2 getPositionOnScreenOld(Minecraft mc, double x, double y,
	// double z, Pre event) {
	//
	// EntityLivingBase entitylivingbase = mc.renderViewEntity;
	// // double playerX = entitylivingbase.lastTickPosX
	// // + (entitylivingbase.posX - entitylivingbase.lastTickPosX)
	// // * partialTicks;
	// // double playerY = entitylivingbase.lastTickPosY
	// // + (entitylivingbase.posY - entitylivingbase.lastTickPosY)
	// // * partialTicks;
	// // double playerZ = entitylivingbase.lastTickPosZ
	// // + (entitylivingbase.posZ - entitylivingbase.lastTickPosZ)
	// // * partialTicks;
	// // entitylivingbase.
	// Vec3 player = entitylivingbase.getPosition(event.partialTicks);
	// Vec3 looking = entitylivingbase.getLook(event.partialTicks);
	//
	// Vec3 pos = Vec3.createVectorHelper(x, y, z);
	// Vec3 marking = pos.addVector(-player.xCoord, -player.yCoord,
	// -player.zCoord).normalize();
	// System.out.println("Current d: " + marking);
	// marking.rotateAroundY((float) (entitylivingbase.rotationYaw / 180 *
	// Math.PI));
	// System.out.println("Current d: " + marking);
	// marking.rotateAroundX((float) (entitylivingbase.rotationPitch / 180 *
	// Math.PI));
	// System.out.println("Current d: " + marking);
	// Vec3 screenEdge = Vec3.createVectorHelper(-0.7235458831104901,
	// 0.407043401367207, 0.5574916588955217);
	//
	// double fovY = 70.0F;
	// fovY += mc.gameSettings.fovSetting * 40.0F;
	// fovY *= mc.thePlayer.getFOVMultiplier();
	// System.out.println("Real FOV: " + fovY);
	// fovY *= Math.PI / 180.0 / 2;
	// double fovX = fovY * mc.displayWidth / mc.displayHeight;
	//
	// Vec3 xz = Vec3.createVectorHelper(marking.xCoord, 0, marking.zCoord)
	// .normalize();
	// Vec3 xy = Vec3.createVectorHelper(0, marking.yCoord, marking.zCoord)
	// .normalize();
	// double angX = Math.asin(xz.xCoord);
	// double angY = Math.asin(xy.yCoord);
	//
	// System.out.println("FOV: " + fovX + "," + fovY + "; mark: " + angX
	// + "," + angY);
	//
	// double screenX = event.resolution.getScaledWidth() * (-angX / fovX + 1)
	// / 2.0;
	// double screenY = event.resolution.getScaledHeight()
	// * (-angY / fovY + 1) / 2.0;
	//
	// System.out.println("On Screen: " + screenX + "," + screenY);
	//
	// return new Pos2((int) screenX, (int) screenY);
	// }

	public void initialize() {
		FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);
	}
}
