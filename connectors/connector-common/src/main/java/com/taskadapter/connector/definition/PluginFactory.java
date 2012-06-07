package com.taskadapter.connector.definition;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.taskadapter.connector.common.ProjectLoader;

/**
 * @author Alexey Skorokhodov
 */
/*
 * TODO: Maybe get rid of this class? Configure binding between descriptor
 * and service in a config file? Or, at least, remove descriptor from this
 * plugin factory and leave this as a "connector factory" item.
 */
public interface PluginFactory<C extends ConnectorConfig> {
    public Connector<? extends C> createConnector(C config);

    //TODO delete method with the same name from Connector class.
    public Descriptor getDescriptor();

    // TODO: move it to connector, but it must have proper configs (at least - partial).
    public ProjectLoader getProjectLoader();
    
    /**
     * Serializes a config to a Json Element.
     * @param config config to serialize.
     * @return serialized config.
     */
    public JsonElement writeConfig(C config);
    
    /**
     * Reads a config.
     * @param config config to read.
     * @return config from input.
     * @throws JsonParseException if config is in invalid format.
     */
    public C readConfig(JsonElement config) throws JsonParseException;
}
