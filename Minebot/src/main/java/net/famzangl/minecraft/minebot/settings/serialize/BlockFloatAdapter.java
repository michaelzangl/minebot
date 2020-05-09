package net.famzangl.minecraft.minebot.settings.serialize;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.famzangl.minecraft.minebot.ai.path.world.BlockFloatMap;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

public class BlockFloatAdapter implements JsonSerializer<BlockFloatMap>,
		JsonDeserializer<BlockFloatMap> {

	@Override
	public BlockFloatMap deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		try {
			BlockFloatMap map = new BlockFloatMap();
			JsonElement valuesElement = json.getAsJsonObject().get("values");
			if (valuesElement != null) {
				JsonObject values = valuesElement
						.getAsJsonObject();
				for (Entry<String, JsonElement> e : values.entrySet()) {
					List<BlockState> blockStates;
					if (e.getKey().matches("\\d+")) {
						int blockStateId = Integer.parseInt(e.getKey());
						blockStates = Collections.singletonList(BlockSet.getStateById(blockStateId));
					} else {
						blockStates = Registry.BLOCK.getOrDefault(new ResourceLocation(e.getKey()))
								.getStateContainer().getValidStates();
					}
					if (e.getValue().isJsonArray()) {
						// Compatibility. TODO: Use block properties
					} else {
						blockStates.forEach(state -> map.set(state, e.getValue().getAsFloat()));
					}
				}
			}

			map.setDefault(json.getAsJsonObject().get("defaultValue")
					.getAsFloat());
			return map;
		} catch (Throwable t) {
			throw new JsonParseException("Error while parsing float map: " + t, t);
		}
	}

	@Override
	public JsonElement serialize(BlockFloatMap src, Type typeOfSrc,
			JsonSerializationContext context) {
		JsonObject res = new JsonObject();
		float defaultValue = src.getDefaultValue();
		if (Float.isNaN(defaultValue)) {
			defaultValue = 0;
		}
		res.addProperty("defaultValue", defaultValue);
		JsonObject valueMap = new JsonObject();

		throw new IllegalStateException("TODO");
		/*
		for (int blockId = 0; blockId < BlockSet.MAX_BLOCKIDS; blockId++) {
			boolean hasSameValue = true;
			float last = 0;
			JsonArray items = new JsonArray();
			for (int i = 0; i < 16; i++) {
				float forThisBlock = src.get(blockId * 16 + i);
				if (Float.isNaN(forThisBlock)) {
					throw new RuntimeException("Cannot convert NaN for "
							+ blockId + ":" + i);
				}
				items.add(new JsonPrimitive(forThisBlock));
				if (i > 0) {
					hasSameValue &= last == forThisBlock;
				}
				last = forThisBlock;
			}
			if (!hasSameValue || Math.abs(last - defaultValue) >= .000001) {
				JsonElement blockRes;
				if (hasSameValue) {
					blockRes = new JsonPrimitive(last);
				} else {
					blockRes = items;
				}
				JsonElement name = BlockSetAdapter.getName(blockId);
				valueMap.add(name.getAsString(), blockRes);
			}
		}
		res.add("values", valueMap);
		return res;
		*/
	}

}
