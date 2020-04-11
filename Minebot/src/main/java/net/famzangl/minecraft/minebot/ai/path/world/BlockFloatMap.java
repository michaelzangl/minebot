package net.famzangl.minecraft.minebot.ai.path.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

import java.util.Arrays;

/**
 * This is a Block-ID + Meta -> Float map.
 * 
 * @author Michael Zangl
 */
public class BlockFloatMap {
	private float[] floats = new float[] {Float.NaN, Float.NaN, Float.NaN, Float.NaN};
	private float defaultValue = Float.NaN;

	public BlockFloatMap() {
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

	private void set(int blockWithMeta, float value) {
		floats[blockWithMeta] = value;
	}

	public void set(BlockState block, float value) {
		set(Block.getStateId(block), value);
	}

	public float get(BlockState blockAndMeta) {
		return get(Block.getStateId(blockAndMeta));
	}

	public float get(int blockAndMeta) {
		return floats[blockAndMeta];
	}

	public float getMax() {
		float max = defaultValue;
		for (float f : floats) {
			if (!Float.isNaN(f) && (f > max || Float.isNaN(max))) {
				max = f;
			}
		}
		return max;
	}

	public void setBlock(Block block, float value) {
		block.getStateContainer().getValidStates().forEach(state -> setBlockState(state, value));
	}

	public void setBlockState(BlockState blockState, float value) {
		int stateId = Block.getStateId(blockState);
		if (floats.length <= stateId) {
			float[] newFloats = Arrays.copyOf(floats, Math.max(stateId + 1, floats.length * 2));
			Arrays.fill(newFloats, floats.length, newFloats.length, Float.NaN);
			this.floats = newFloats;
		}
		floats[stateId] = value;
	}

	public float getDefaultValue() {
		return defaultValue;
	}

	@Override
	public String toString() {
		return "BlockFloatMap [floats=..., defaultValue=" + defaultValue + "]";
	}
	
	
}
