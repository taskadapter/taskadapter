package com.taskadapter.connector.github;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.taskadapter.connector.common.ConfigUtils;
import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.PluginFactory;

public class GithubFactory implements PluginFactory<GithubConfig> {
    private static final Descriptor DESCRIPTOR = new Descriptor(GithubConnector.ID, GithubConfig.DEFAULT_LABEL);

    @Override
    public AvailableFields getAvailableFields() {
        return GithubSupportedFields.SUPPORTED_FIELDS;
    }

    @Override
    public Connector<GithubConfig> createConnector(GithubConfig config) {
        return new GithubConnector(config);
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
    public GithubConfig readConfig(JsonElement config)
            throws JsonParseException {
        return ConfigUtils.createDefaultGson().fromJson(config,
                GithubConfig.class);
    }

    @Override
    public GithubConfig createDefaultConfig() {
        return new GithubConfig();
    }
}
