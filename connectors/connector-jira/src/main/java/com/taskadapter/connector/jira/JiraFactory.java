package com.taskadapter.connector.jira;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.taskadapter.connector.common.ConfigUtils;
import com.taskadapter.connector.common.PriorityLoader;
import com.taskadapter.connector.common.ProjectLoader;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.PluginFactory;

/**
 * @author Alexey Skorokhodov
 */
public class JiraFactory implements PluginFactory<JiraConfig> {
    @Override
    public JiraConnector createConnector(JiraConfig config) {
        return new JiraConnector(config);
    }

    @Override
    public Descriptor getDescriptor() {
        return JiraDescriptor.instance;
    }
    
    @Override
    public ProjectLoader getProjectLoader() {
        return new JiraProjectLoader();
    }
    
    @Override
    public PriorityLoader getPriorityLoader() {
        return new JiraPriorityLoader();
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

}
