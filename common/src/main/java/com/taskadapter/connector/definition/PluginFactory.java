package com.taskadapter.connector.definition;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.taskadapter.connector.Field;
import com.taskadapter.connector.NewConnector;

import java.util.List;

/**
 * TODO: Maybe get rid of this class? Configure binding between descriptor
 * and service in a config file? Or, at least, remove descriptor from this
 * plugin factory and leave this as a "connector factory" item.
 */
public interface PluginFactory<C extends ConnectorConfig> {
    List<Field> getAvailableFields();

    NewConnector createConnector(C config);

    Descriptor getDescriptor();

    /**
     * Serializes a config to a Json Element.
     *
     * @param config config to serialize.
     * @return serialized config.
     */
    JsonElement writeConfig(C config);

    /**
     * Reads a config.
     *
     * @param config config to read.
     * @return config from input.
     * @throws JsonParseException if config is in invalid format.
     */
    C readConfig(JsonElement config) throws JsonParseException;

    /**
     * Creates a default ("almost empty") connector config.
     *
     * @return new connector config.
     */
    C createDefaultConfig();
}
