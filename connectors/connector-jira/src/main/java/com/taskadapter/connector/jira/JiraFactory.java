package com.taskadapter.connector.jira;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.taskadapter.connector.common.ConfigUtils;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.PluginFactory;

public class JiraFactory implements PluginFactory<JiraConfig> {
    private static final Descriptor instance = new Descriptor(JiraConnector.ID, JiraConfig.DEFAULT_LABEL);

    @Override
    public JiraConnector createConnector(JiraConfig config) {
        return new JiraConnector(config);
    }

    @Override
    public Descriptor getDescriptor() {
        return instance;
    }

	@Override
	public JsonElement writeConfig(JiraConfig config) {
		return ConfigUtils.createDefaultGson().toJsonTree(config);
	}

	@Override
	public JiraConfig readConfig(JsonElement config) throws JsonParseException {
		return ConfigUtils.createDefaultGson().fromJson(config,
				JiraConfig.class);
	}

    @Override
    public JiraConfig createDefaultConfig() {
        return new JiraConfig();
    }
}
