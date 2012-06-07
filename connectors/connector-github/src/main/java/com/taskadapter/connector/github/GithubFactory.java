package com.taskadapter.connector.github;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.taskadapter.connector.common.ConfigUtils;
import com.taskadapter.connector.common.ProjectLoader;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.PluginFactory;

public class GithubFactory implements PluginFactory<GithubConfig> {
    @Override
    public Connector<GithubConfig> createConnector(GithubConfig config) {
        return new GithubConnector(config);
    }

    @Override
    public Descriptor getDescriptor() {
        return GithubDescriptor.instance;
    }
    
    @Override
    public ProjectLoader getProjectLoader() {
        return new GithubProjectLoader();
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
}
