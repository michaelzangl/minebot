package net.famzangl.minecraft.aimbow;

import java.lang.reflect.Field;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import net.famzangl.minecraft.aimbow.aiming.ColissionData;
import net.famzangl.minecraft.aimbow.aiming.BowColissionSolver;
import net.famzangl.minecraft.aimbow.aiming.ColissionSolver;
import net.famzangl.minecraft.aimbow.aiming.ReverseBowSolver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.ChunkRenderContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

public class AimbowGui extends GuiIngame {
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
	private float partialTicks;
	public boolean autoAim;
	private MatrixCatcher catcher;

	public AimbowGui(Minecraft mcIn) {
		super(mcIn);
	}

	@Override
	public void renderGameOverlay(float partialTicks) {
		this.partialTicks = partialTicks;
		super.renderGameOverlay(partialTicks);
	}

	@Override
	protected boolean showCrosshair() {
		EntityPlayerSP player = mc.thePlayer;
		ItemStack heldItem = player.getHeldItem();
		ColissionSolver colissionSolver = ColissionSolver.forItem(heldItem, mc);
		if (colissionSolver != null) {
			checkForMatrixStealing();
			final ScaledResolution resolution = new ScaledResolution(this.mc,
					this.mc.displayWidth, this.mc.displayHeight);
			boolean colissionDrawn = false;
			ArrayList<ColissionData> colissionPoints = colissionSolver
					.computeCurrentColissionPoints();
			for (ColissionData p : colissionPoints) {
				Pos2 pos = getPositionOnScreen(mc, p.x,
						p.y + player.getEyeHeight(), p.z, resolution);
				//System.out.println("Hitpoint: " + p + " is on screen: " + pos);
				boolean hit = p.hitEntity != null;
				drawCrosshairAt(mc, pos.x, pos.y, hit ? 0 : 1, hit ? 1 : 0, 0);
				zc.zoomTowards(new Vec3(p.x, p.y, p.z));
				if (!colissionDrawn && !hit && autoAim
						&& shouldAutoaim(heldItem)) {
					aimAtCloseEntity(pos, resolution, colissionSolver);
				}
				colissionDrawn = true;
			}
			if (!colissionDrawn) {
				int x = resolution.getScaledWidth() / 2;
				int y = resolution.getScaledHeight() / 2;
				drawCrosshairAt(mc, x, y, .6f, .6f, .6f);
			}
			// if (count == 0 || !colissionDrawn) {
			// zc.apply(0);
			// zc.reset();
			// } else {
			// float f1 = count / 20.0F;
			//
			// if (f1 > 1.0F) {
			// f1 = 1.0F;
			// } else {
			// f1 *= f1;
			// }
			// zc.apply(f1);
			// }
			return false;
		} else {
			return super.showCrosshair();
		}
	}

	private void checkForMatrixStealing() {
		try {
			// getDeclaredField("renderContainer");
			for (Field field : RenderGlobal.class.getDeclaredFields()) {
				if (field.getType() != ChunkRenderContainer.class)
					continue;

				field.setAccessible(true);
				Object current = field.get(mc.renderGlobal);
				if (!(current instanceof MatrixCatcher)) {
					catcher = new MatrixCatcher((ChunkRenderContainer) current);
					field.set(mc.renderGlobal, catcher);
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private boolean shouldAutoaim(ItemStack heldItem) {
		int count = mc.thePlayer.getItemInUseCount();
		return heldItem.getItem() != Items.bow || count > 0;
	}

	private void drawCrosshairAt(Minecraft mc, int x, int y, float r, float g,
			float b) {
		//GlStateManager.tryBlendFuncSeparate(775, 769, 1, 0);
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		drawTexturedModalRect(x - 7, y - 7, 0, 0, 16, 16, r, g, b);
		// mc.getTextureManager().bindTexture(Gui.icons);
		// GL11.glEnable(GL11.GL_BLEND);
		// drawTexturedModalRect(x - 7, y - 7, 0, 0, 16, 16, r, g, b);
		// GL11.glDisable(GL11.GL_BLEND);
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
		WorldRenderer tessellator = Tessellator.getInstance()
				.getWorldRenderer();
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
		Tessellator.getInstance().draw();
	}

	public Pos2 getPositionOnScreen(Minecraft mc, double x, double y, double z,
			ScaledResolution resolution) {
		Vec3 player = mc.getRenderViewEntity().getPositionEyes(partialTicks);
		viewPort.rewind();
		viewPort.put(0);
		viewPort.put(0);
		viewPort.put(resolution.getScaledWidth());
		viewPort.put(resolution.getScaledHeight());
		viewPort.rewind();

		win_pos.rewind();
		modelBuffer.rewind();
		projectionBuffer.rewind();
		GLU.gluProject((float) (x - player.xCoord),
				(float) (y - player.yCoord), (float) (z - player.zCoord),
				modelBuffer, projectionBuffer, viewPort, win_pos);

		win_pos.rewind();
		int sx = (int) win_pos.get();
		int sy = resolution.getScaledHeight() - (int) win_pos.get();

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

	public void aimAtCloseEntity(Pos2 toScreenPos, ScaledResolution resolution,
			ColissionSolver colissionSolver) {
		AxisAlignedBB bbox = mc.thePlayer.getEntityBoundingBox();
		List<Entity> entities = mc.theWorld.getEntitiesWithinAABB(Entity.class,
				bbox.expand(200, 100, 200));

		ArrayList<CloseEntity> nearEntities = new ArrayList<CloseEntity>();

		// System.out.println("Scanning entites: " + entities.size());
		for (Entity e : entities) {
			if (e.canBeCollidedWith()
					&& e != Minecraft.getMinecraft().thePlayer) {
				Pos2 onScreen = getPositionOnScreen(mc, e.posX, e.posY, e.posZ,
						resolution);
				double d = toScreenPos.distanceTo(onScreen);
				if (d < 100) {
					// System.out.println("Close entity.");
					nearEntities.add(new CloseEntity(e, d));
				}
			}
		}
		Collections.sort(nearEntities);

		ReverseBowSolver rbs = new ReverseBowSolver(
				colissionSolver.getGravity(), colissionSolver.getVelocity());

		for (CloseEntity e : nearEntities) {
			// System.out.println("Try to hit " + e);
			Vec3 look = rbs.getLookForTarget(e.entity);
			ArrayList<ColissionData> foundColissions = colissionSolver
					.computeColissionWithLook(look);
			if (foundColissions.size() > 0
					&& foundColissions.get(0).hitEntity == e.entity) {
				// System.out.println("Positive: " + e.entity);
				lookAt(look);
				break;
			} else {
				// System.out.println("Negative: " + e + ", got: "
				// + foundColissions);
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

	public void stealProjectionMatrix() {
		modelBuffer.rewind();
		projectionBuffer.rewind();
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelBuffer);
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projectionBuffer);
	}

	public class MatrixCatcher extends ChunkRenderContainer {
		ChunkRenderContainer base;

		public void addRenderChunk(RenderChunk p_178002_1_,
				EnumWorldBlockLayer p_178002_2_) {
			base.addRenderChunk(p_178002_1_, p_178002_2_);
		}

		public boolean equals(Object obj) {
			return base.equals(obj);
		}

		public int hashCode() {
			return base.hashCode();
		}

		public void initialize(double p_178004_1_, double p_178004_3_,
				double p_178004_5_) {
			base.initialize(p_178004_1_, p_178004_3_, p_178004_5_);
		}

		public void preRenderChunk(RenderChunk p_178003_1_) {
			base.preRenderChunk(p_178003_1_);
		}

		public void renderChunkLayer(EnumWorldBlockLayer p_178001_1_) {
			if (catcher == this) {
				stealProjectionMatrix();
			}
			base.renderChunkLayer(p_178001_1_);
		}

		public String toString() {
			return base.toString();
		}

		public MatrixCatcher(ChunkRenderContainer base) {
			super();
			this.base = base;
		}
	}

}
