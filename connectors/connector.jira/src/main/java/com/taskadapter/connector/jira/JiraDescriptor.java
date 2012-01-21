package com.taskadapter.connector.jira;

import com.taskadapter.connector.common.PriorityLoader;
import com.taskadapter.connector.common.ProjectLoader;
import com.taskadapter.connector.common.TaskLoader;
import com.taskadapter.connector.common.TaskSaver;
import com.taskadapter.connector.definition.AvailableFieldsProvider;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.PluginFactory;

import java.util.Arrays;
import java.util.Collection;

public class JiraDescriptor implements Descriptor {

	public static final JiraDescriptor instance = new JiraDescriptor();

	/**
	 * Keep it the same to enable backward compatibility with the existing
	 * config files.
	 */
	private static final String ID = "Atlassian Jira";

	private static final String INFO = "Atlassian Jira connector (supports Jira v. 3.1.12+)";

	public String getID() {
		return ID;
	}

	@Override
	public String getDescription() {
		return INFO;
	}

	@Override
	public JiraConfig createDefaultConfig() {
		return new JiraConfig();
	}

	@Override
	public String getLabel() {
		return JiraConfig.DEFAULT_LABEL;
	}

	@Override
	public Class<JiraConfig> getConfigClass() {
		return JiraConfig.class;
	}

	@Override
	public AvailableFieldsProvider getAvailableFieldsProvider() {
		return new JiraAvailableFieldsProvider();
	}

	@Override
	public ProjectLoader getProjectLoader() {
		return new JiraProjectLoader();
	}

	@Override
	public TaskLoader<JiraConfig> getTaskLoader() {
		return new JiraTaskLoader();
	}

	@Override
	public TaskSaver<JiraConfig> getTaskSaver(ConnectorConfig config) {
		return new JiraTaskSaver((JiraConfig) config);
	}

	@Override
	public Collection<Feature> getSupportedFeatures() {
		return Arrays.asList(Feature.LOAD_TASK, Feature.SAVE_TASK,
				Feature.TASK_TYPE, Feature.LOAD_PRIORITIES);
	}

	@Override
	public PriorityLoader getPriorityLoader() {
		return new JiraPriorityLoader();
	}

    @Override
    public PluginFactory getPluginFactory() {
        return new JiraFactory();
    }
}
