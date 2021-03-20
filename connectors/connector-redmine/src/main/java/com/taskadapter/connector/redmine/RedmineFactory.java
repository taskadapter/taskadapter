package com.taskadapter.connector.redmine;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.taskadapter.connector.NewConnector;
import com.taskadapter.connector.common.ConfigUtils;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.PluginFactory;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.model.Field;

import java.util.List;

public class RedmineFactory implements PluginFactory<RedmineConfig, WebConnectorSetup> {
    private static Descriptor DESCRIPTOR = new Descriptor(RedmineConnector.ID, "Redmine");

    @Override
    public List<Field<?>> getAllFields() {
        return RedmineField.fields;
    }

    @Override
    public NewConnector createConnector(RedmineConfig config, WebConnectorSetup setup) {
        return new RedmineConnector(config, setup);
    }

    @Override
    public Descriptor getDescriptor() {
        return DESCRIPTOR;
    }

    @Override
    public JsonElement writeConfig(RedmineConfig config) {
        return ConfigUtils.createDefaultGson().toJsonTree(config);
    }

    @Override
    public RedmineConfig readConfig(JsonElement config) throws JsonParseException {
        return ConfigUtils.createDefaultGson().fromJson(config, RedmineConfig.class);
    }

    @Override
    public RedmineConfig createDefaultConfig() {
        return new RedmineConfig();
    }

    public List<Field<?>> getDefaultFieldsForNewConfig() {
        return RedmineField.defaultFieldsForNewConfig();
    }
}
