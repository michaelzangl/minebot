package net.famzangl.minecraft.minebot.ai;

import net.minecraft.client.settings.KeyBinding;

public final class InteractAlways extends KeyBinding {
	private boolean isPressed;

	InteractAlways(String p_i45001_1_, int p_i45001_2_, String p_i45001_3_,
			boolean pressed) {
		super(p_i45001_1_, p_i45001_2_, p_i45001_3_);
		isPressed = pressed;
	}

	@Override
	public boolean getIsKeyPressed() {
		return true;
	}

	@Override
	public boolean isPressed() {
		final boolean ret = isPressed;
		isPressed = false;
		return ret;
	}
}
