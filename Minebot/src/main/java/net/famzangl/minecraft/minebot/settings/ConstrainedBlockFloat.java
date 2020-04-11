package net.famzangl.minecraft.minebot.settings;

public @interface ConstrainedBlockFloat {

	float min();

	float max();

	float defaultValue();

}
