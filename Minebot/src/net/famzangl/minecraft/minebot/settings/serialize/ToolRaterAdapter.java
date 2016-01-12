package net.famzangl.minecraft.minebot.settings.serialize;

import java.lang.reflect.Type;
import java.util.Map.Entry;

import net.famzangl.minecraft.minebot.ai.path.world.BlockFloatMap;
import net.famzangl.minecraft.minebot.ai.tools.ToolRater;
import net.famzangl.minecraft.minebot.ai.tools.rate.Rater;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ToolRaterAdapter implements JsonSerializer<ToolRater>,
		JsonDeserializer<ToolRater> {

	@Override
	public ToolRater deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		try {
			JsonObject obj = json.getAsJsonObject();
			ToolRater rater = new ToolRater();
			for (Entry<String, JsonElement> e : obj.entrySet()) {
				BlockFloatMap map;
				if (e.getValue().isJsonPrimitive()) {
					map = new BlockFloatMap();
					map.setDefault(e.getValue().getAsFloat());
				} else {
					map = context.<BlockFloatMap> deserialize(e.getValue(),
							BlockFloatMap.class);
				}
				rater.addRater(e.getKey(), map);
			}
			return rater;
		} catch (IllegalArgumentException t) {
			throw new JsonParseException("Error creating rater: "
					+ t.getMessage(), t);
		}
	}

	@Override
	public JsonElement serialize(ToolRater src, Type typeOfSrc,
			JsonSerializationContext context) {
		JsonObject obj = new JsonObject();
		for (Rater rater : src.getRaters()) {
			obj.add(rater.getName(),
					context.serialize(rater.getValues(), BlockFloatMap.class));
		}

		return obj;
	}

}
