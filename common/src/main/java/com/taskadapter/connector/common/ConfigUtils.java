package com.taskadapter.connector.common;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.model.GTaskDescriptor.FIELD;

/**
 * Utilities for a connector configuration.
 * 
 * @author maxkar
 * 
 */
public class ConfigUtils {

	/**
	 * Mappings serializer
	 */
	private static JsonSerializer<Mappings> MAPPINGS_SERIALIZER = new JsonSerializer<Mappings>() {
		@Override
		public JsonElement serialize(Mappings src, Type typeOfSrc,
				JsonSerializationContext context) {
			return addVersion(RAW_GSON.toJsonTree(src, typeOfSrc)
					.getAsJsonObject(), 1);
		}
	};

	/**
	 * Mappings parser.
	 */
	private static JsonDeserializer<Mappings> MAPPINGS_PARSER = new JsonDeserializer<Mappings>() {
		@Override
		public Mappings deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			return parseMapping(json, typeOfT, context);
		}
	};

	/**
	 * "Raw" gson writer.
	 */
	static final Gson RAW_GSON = new Gson();

	/**
	 * Adds a version to a json object. Original object is modified and returned
	 * as a value.
	 * 
	 * @param object
	 *            object to add a version for.
	 * @param version
	 *            object version.
	 * @return object with updated version.
	 */
	public static JsonObject addVersion(JsonObject object, int version) {
		object.add("version", new JsonPrimitive(version));
		return object;
	}

	/**
	 * Parses a mapping.
	 * 
	 * @param json
	 *            json.
	 * @param typeOfT
	 *            element type.
	 * @param context
	 *            active context.
	 * @return parsed mappings.
	 */
	static Mappings parseMapping(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) {
		int version = getVersion(json);
		switch (version) {
		case 0:
			return readMappings0(json);
		case 1:
			return RAW_GSON.fromJson(json, Mappings.class);
		}
		throw new JsonParseException("Unsupported version " + version);
	}

	/**
	 * Reads mapping from a default version.
	 * @param json json to parse.
	 * @return parsed mapping.
	 */
	private static Mappings readMappings0(JsonElement json) {
		final Mappings result = new Mappings();
		final JsonObject obj = json.getAsJsonObject();
		
		for (Map.Entry<String, JsonElement> elem : obj.entrySet()) {
			final FIELD field = FIELD.valueOf(elem.getKey());
			final JsonObject fmapping = elem.getValue().getAsJsonObject();
			final boolean useMapping = fmapping.get("selected")
					.getAsJsonPrimitive().getAsBoolean();
			final String mapTo = fmapping.has("currentValue") ? fmapping.get(
					"currentValue").getAsString() : null;
			final String defaultValueForEmptyField = fmapping.has("defaultValueForEmptyField") ? fmapping.get(
					"defaultValueForEmptyField").getAsString() : "";
			result.setMapping(field, useMapping, mapTo, defaultValueForEmptyField);
		}
		return result;
	}

	/**
	 * Returns an object version. If object have no "version" field or value is
	 * incorrect, returns 0.
	 * 
	 * @param object
	 *            object to get a version from.
	 * @return object version.
	 */
	public static int getVersion(JsonElement object) {
		if (!object.isJsonObject())
			return 0;
		final JsonObject obj = object.getAsJsonObject();
		if (!obj.has("version"))
			return 0;
		final JsonElement versionElt = obj.get("version");
		if (!versionElt.isJsonPrimitive())
			return 0;
		final JsonPrimitive version = versionElt.getAsJsonPrimitive();
		if (!version.isNumber())
			return 0;
		return version.getAsInt();
	}

	/**
	 * Creates a default "GSON" builder for a project. This builder have
	 * configured mappings for following classes:
	 * <ul>
	 * </ul>
	 * 
	 * @return default "GSON" builder for a project.
	 */
	public static GsonBuilder createDefaultGsonBuilder() {
		final GsonBuilder result = new GsonBuilder();
		result.registerTypeAdapter(Mappings.class, MAPPINGS_SERIALIZER);
		result.registerTypeAdapter(Mappings.class, MAPPINGS_PARSER);
		return result;
	}

	/**
	 * Creates a default GSON serializer.
	 * 
	 * @return default gson serializer.
	 */
	public static Gson createDefaultGson() {
		return createDefaultGsonBuilder().create();
	}
}
