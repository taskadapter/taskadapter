package com.taskadapter.connector.redmine;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.taskadapter.connector.common.ConfigUtils;
import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.PluginFactory;

public class RedmineFactory implements PluginFactory<RedmineConfig> {
    @Override
    public RedmineConnector createConnector(RedmineConfig config) {
        return new RedmineConnector(config);
    }

    @Override
    public Descriptor getDescriptor() {
        return RedmineDescriptor.instance;
    }

	@Override
	public JsonElement writeConfig(RedmineConfig config) {
		return ConfigUtils.createDefaultGson().toJsonTree(config);
	}

	@Override
	public RedmineConfig readConfig(JsonElement config)
			throws JsonParseException {
		return ConfigUtils.createDefaultGson().fromJson(config,
				RedmineConfig.class);
	}

    @Override
    public RedmineConfig createDefaultConfig() {
        return new RedmineConfig();
    }
}
