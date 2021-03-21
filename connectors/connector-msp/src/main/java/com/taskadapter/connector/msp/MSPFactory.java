package com.taskadapter.connector.msp;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.taskadapter.connector.common.ConfigUtils;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.FileSetup;
import com.taskadapter.connector.definition.PluginFactory;
import com.taskadapter.model.Field;

import java.util.List;

public class MSPFactory implements PluginFactory<MSPConfig, FileSetup> {
    private static final Descriptor DESCRIPTOR = new Descriptor(MSPConnector.ID, "Microsoft Project");

    @Override
    public List<Field<?>> getAllFields() {
        return MspField.fields;
    }

    @Override
    public List<Field<?>> getDefaultFieldsForNewConfig() {
        return MspField.fields;
    }

    @Override
    public MSPConnector createConnector(MSPConfig config, FileSetup setup) {
        return new MSPConnector(setup);
    }

    @Override
    public Descriptor getDescriptor() {
        return DESCRIPTOR;
    }

    @Override
    public JsonElement writeConfig(MSPConfig config) {
        return ConfigUtils.createDefaultGson().toJsonTree(config);
    }

    @Override
    public MSPConfig readConfig(JsonElement config) throws JsonParseException {
        return ConfigUtils.createDefaultGson().fromJson(config, MSPConfig.class);
    }

    @Override
    public MSPConfig createDefaultConfig() {
        return new MSPConfig();
    }
}
