package com.taskadapter.connector.basecamp;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.taskadapter.connector.basecamp.transport.BaseCommunicator;
import com.taskadapter.connector.basecamp.transport.ObjectAPIFactory;
import com.taskadapter.connector.common.ConfigUtils;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.PluginFactory;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.model.Field;

import java.util.List;

public class BasecampFactory implements PluginFactory<BasecampConfig, WebConnectorSetup> {

    private static final Descriptor DESCRIPTOR = new Descriptor(BasecampConnector.ID, "Basecamp 2");

    private final ObjectAPIFactory factory = new ObjectAPIFactory(
            new BaseCommunicator());

    @Override
    public List<Field<?>> getAllFields() {
        return BasecampField.fields;
    }

    @Override
    public List<Field<?>> getDefaultFieldsForNewConfig() {
        return BasecampField.fields;
    }

    @Override
    public BasecampConnector createConnector(BasecampConfig config, WebConnectorSetup setup) {
        return new BasecampConnector(config, setup, factory);
    }

    @Override
    public Descriptor getDescriptor() {
        return DESCRIPTOR;
    }

    @Override
    public JsonElement writeConfig(BasecampConfig config) {
        return ConfigUtils.createDefaultGson().toJsonTree(config);
    }

    @Override
    public BasecampConfig readConfig(JsonElement config)
            throws JsonParseException {
        return ConfigUtils.createDefaultGson().fromJson(config, BasecampConfig.class);
    }

    @Override
    public BasecampConfig createDefaultConfig() {
        return new BasecampConfig();
    }
}
