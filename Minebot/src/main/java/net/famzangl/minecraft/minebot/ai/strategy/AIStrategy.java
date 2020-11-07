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
package net.famzangl.minecraft.minebot.ai.strategy;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.debug.DebugRenderer;

/**
 * An {@link AIStrategy} tells the bot what to do next. It should recover from
 * any state that might happen while the bot executes.
 * 
 * @author michael
 * @see RunOnceStrategy
 * @see StackStrategy
 * 
 */
public abstract class AIStrategy {

	public static final DebugRenderer.IDebugRenderer NOP_DEBUG_RENDERER = new DebugRenderer.IDebugRenderer() {
		@Override
		public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, double camX, double camY, double camZ) {
			// NOP
		}
	};

	public enum TickResult {
		/**
		 * To do this tick again, either calling the same strategy again or
		 * deactivating it.
		 */
		TICK_AGAIN,
		/**
		 * The tick is handled, the player was moved and did some work.
		 */
		TICK_HANDLED,
		/**
		 * This stategy did not do anything and has nothing more to do
		 * currently. Deactivate it.
		 */
		NO_MORE_WORK,
		ABORT
	};

	private boolean active;
	/**
	 * Disable autojump while the bot is active but store user preference
	 */
	private boolean oldAutoJump;

	/**
	 * (de) Activates the strategy. Always called in a game tick. This can be
	 * called any time, especially deactivate.
	 * 
	 * @param active
	 */
	public final void setActive(boolean active, AIHelper helper) {
		this.active = active;
		if (active) {
			//System.out.println("ACTIVATED" + helper.getResumeStrategy());
			onActivate(helper);
			System.out.println("ACTIVATED" + helper.getResumeStrategy());
		} else {
			System.out.println("DEACTIVATED" + helper.getResumeStrategy());
			onDeactivate(helper);
			//System.out.println("DEACTIVATED" + helper.getResumeStrategy());
		}
	}

	public final boolean isActive() {
		return active;
	}

	protected void onActivate(AIHelper helper) {
		oldAutoJump = helper.getMinecraft().gameSettings.autoJump;
		helper.getMinecraft().gameSettings.autoJump = false;
	}
	
	protected void onDeactivate(AIHelper helper) {
		helper.getMinecraft().gameSettings.autoJump = oldAutoJump;
	}

	/**
	 * Tests if this strategy needs to take over immediately. Chances are good
	 * that you are standing on a cliff / ... if you get activated.
	 * 
	 * @return <code>true</code> if {@link #checkShouldTakeOver(AIHelper)} needs
	 *         to be called every game tick, <code>false</code> to only call on
	 *         good/safe times.
	 */
	public boolean takesOverAnyTime() {
		return false;
	}

	/**
	 * Checks if this should take over.
	 * 
	 * @param helper
	 *            TODO
	 * 
	 * @return
	 */
	public boolean checkShouldTakeOver(AIHelper helper) {
		if (active) {
			throw new IllegalStateException();
		}
		return true;
	}

	/**
	 * Runs a game tick.
	 * 
	 * @param helper
	 *            The helper.
	 * @return
	 */
	public final TickResult gameTick(AIHelper helper) {
		if (!active) {
			throw new IllegalStateException();
		}

		return onGameTick(helper);
	}

	protected abstract TickResult onGameTick(AIHelper helper);

	/**
	 * 
	 * @param helper
	 *            TODO
	 * @return A String to display in the top right hand corner of the screen.
	 */
	public String getDescription(AIHelper helper) {
		return "No description so far... " + getClass().getSimpleName();
	}

	/**
	 * A flag indicating that this strategy has failed. Most strategies cannot
	 * fail, they just do as good as they can and then finish. This flag should
	 * only be set if the strategy has no more work.
	 * 
	 * @return
	 */
	public boolean hasFailed() {
		return false;
	}

	public DebugRenderer.IDebugRenderer getDebugRenderer(AIHelper helper) {
		return NOP_DEBUG_RENDERER;
	}
}
