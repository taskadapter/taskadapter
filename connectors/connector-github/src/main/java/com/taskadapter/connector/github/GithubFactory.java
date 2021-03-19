package com.taskadapter.connector.github;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.taskadapter.connector.NewConnector;
import com.taskadapter.connector.common.ConfigUtils;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.PluginFactory;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.model.Field;

import java.util.List;

public class GithubFactory implements PluginFactory<GithubConfig, WebConnectorSetup> {
    private static final Descriptor DESCRIPTOR = new Descriptor(GithubConnector.ID, GithubConfig.DEFAULT_LABEL);

    @Override
    public List<Field<?>> getAllFields() {
        return GithubField.fields;
    }

    @Override
    public List<Field<?>> getDefaultFieldsForNewConfig() {
        return GithubField.fields;
    }

    @Override
    public NewConnector createConnector(GithubConfig config, WebConnectorSetup setup) {
        return new GithubConnector(config, setup);
    }

    @Override
    public Descriptor getDescriptor() {
        return DESCRIPTOR;
    }

    @Override
    public JsonElement writeConfig(GithubConfig config) {
        return ConfigUtils.createDefaultGson().toJsonTree(config);
    }

    @Override
    public GithubConfig readConfig(JsonElement config) throws JsonParseException {
        return ConfigUtils.createDefaultGson().fromJson(config, GithubConfig.class);
    }

    @Override
    public GithubConfig createDefaultConfig() {
        return new GithubConfig();
    }
}
