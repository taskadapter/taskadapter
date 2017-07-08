package com.taskadapter.connector.jira;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.taskadapter.connector.common.ConfigUtils;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.PluginFactory;

import java.util.List;

public class JiraFactory implements PluginFactory<JiraConfig> {
    private static final Descriptor DESCRIPTOR = new Descriptor(JiraConnector.ID, JiraConfig.DEFAULT_LABEL);

    @Override
    public List<String> getAvailableFields() {
        return JiraField.fieldsAsJava();
    }

    @Override
    public JiraConnector createConnector(JiraConfig config) {
        return new JiraConnector(config);
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
		return ConfigUtils.createDefaultGson().fromJson(config,
				JiraConfig.class);
	}

    @Override
    public JiraConfig createDefaultConfig() {
        return new JiraConfig();
    }
}
