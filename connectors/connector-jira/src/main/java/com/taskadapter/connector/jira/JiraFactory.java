package com.taskadapter.connector.jira;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.taskadapter.connector.NewConnector;
import com.taskadapter.connector.common.ConfigUtils;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.PluginFactory;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.model.Field;

import java.util.List;

public class JiraFactory implements PluginFactory<JiraConfig, WebConnectorSetup> {
    private static Descriptor DESCRIPTOR = new Descriptor(JiraConnector.ID(), "Atlassian JIRA");

    @Override
    public List<Field<?>> getAllFields() {
        return JiraField.fields;
    }

    @Override
    public NewConnector createConnector(JiraConfig config, WebConnectorSetup setup) {
        return new JiraConnector(config, setup);
    }

    @Override
    public Descriptor getDescriptor() {
        return DESCRIPTOR;
    }

    @Override
    public JsonElement writeConfig(JiraConfig config) {
        return ConfigUtils.createDefaultGson().toJsonTree(config);
    }

    @Override
    public JiraConfig readConfig(JsonElement config) throws JsonParseException {
        return ConfigUtils.createDefaultGson().fromJson(config, JiraConfig.class);
    }

    @Override
    public JiraConfig createDefaultConfig() {
        return new JiraConfig();
    }

    public List<Field<?>> getDefaultFieldsForNewConfig() {
        return JiraField.defaultFieldsForNewConfig();
    }
}
