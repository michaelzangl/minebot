package net.famzangl.minecraft.minebot.ai.path.world;

import java.util.Arrays;

import net.famzangl.minecraft.minebot.ai.command.BlockWithData;
import net.minecraft.block.Block;

/**
 * This is a Block-ID + Meta -> Float map.
 * 
 * @author Michael Zangl
 */
public class BlockFloatMap {
	private float[] floats = new float[BlockSet.MAX_BLOCKIDS * 16];
	private float defaultValue = Float.NaN;

	public BlockFloatMap() {
		Arrays.fill(floats, Float.NaN);
	}

	public void setDefault(float defaultValue) {
		if (!Float.isNaN(this.defaultValue)) {
			throw new IllegalStateException("Default already set.");
		}

		if (Float.isNaN(defaultValue)) {
			throw new IllegalArgumentException("Default is NaN.");
		}
		for (int i = 0; i < floats.length; i++) {
			if (Float.isNaN(floats[i])) {
				floats[i] = defaultValue;
			}
		}
		this.defaultValue = defaultValue;
	}

	public void setBlock(int blockId, float value) {
		for (int i = 0; i < 16; i++) {
			floats[blockId * 16 + i] = value;
		}
	}

	public void set(int blockWithMeta, float value) {
		floats[blockWithMeta] = value;
	}

	public void set(BlockWithData block, float value) {
		set(block.getBlockWithMeta(), value);
	}

	public float get(int blockAndMeta) {
		return floats[blockAndMeta];
	}

	public float getMax() {
		float max = Float.NaN;
		for (float f : floats) {
			if (f != Float.NaN && (f > max || Float.isNaN(max))) {
				max = f;
			}
		}
		return max;
	}

	public void setBlock(Block block, float value) {
		setBlock(Block.getIdFromBlock(block), value);
	}
	
	public float getDefaultValue() {
		return defaultValue;
	}

	@Override
	public String toString() {
		return "BlockFloatMap [floats=..., defaultValue=" + defaultValue + "]";
	}
	
	
}
