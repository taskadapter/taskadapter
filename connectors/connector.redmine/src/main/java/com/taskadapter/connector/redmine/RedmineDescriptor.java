package com.taskadapter.connector.redmine;

import com.taskadapter.connector.common.PriorityLoader;
import com.taskadapter.connector.common.ProjectLoader;
import com.taskadapter.connector.common.TaskLoader;
import com.taskadapter.connector.common.TaskSaver;
import com.taskadapter.connector.definition.AvailableFieldsProvider;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;

import java.util.Arrays;
import java.util.Collection;

public class RedmineDescriptor implements Descriptor {
	private static final String INFO_TEXT = "Redmine/Chiliproject connector. Connects to Redmine servers via REST API. Supports Redmine v. 1.1+";

	/**
	 * Keep it the same to enable backward compatibility for previously created config files.
	 */
	public static final String ID = "Redmine REST";

	public static final RedmineDescriptor instance = new RedmineDescriptor();

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public String toString() {
		return getID();
	}

	@Override
	public RedmineConfig createDefaultConfig() {
		RedmineConfig c = new RedmineConfig();
		return c;
	}

	@Override
	public String getDescription() {
		return INFO_TEXT;
	}

	@Override
	public String getLabel() {
		return RedmineConfig.DEFAULT_LABEL;
	}

	@Override
	public Class<RedmineConfig> getConfigClass() {
		return RedmineConfig.class;
	}

	@Override
	public AvailableFieldsProvider getAvailableFieldsProvider() {
		return new RedmineAvailableFieldsProvider();
	}

	@Override
	public Connector<RedmineConfig> createConnector(ConnectorConfig config) {
		return new RedmineConnector((RedmineConfig) config);
	}

	@Override
	public ProjectLoader getProjectLoader() {
		return new RedmineProjectLoader();
	}

	@Override
	public TaskLoader<RedmineConfig> getTaskLoader() {
		return new RedmineTaskLoader();
	}

	@Override
	public TaskSaver<RedmineConfig> getTaskSaver(ConnectorConfig config) {
		return new RedmineTaskSaver((RedmineConfig) config);
	}

	@Override
	public Collection<Feature> getSupportedFeatures() {
		return Arrays.asList(Feature.LOAD_TASK, Feature.SAVE_TASK, Feature.UPDATE_TASK, Feature.TASK_TYPE);
	}

	@Override
	public PriorityLoader getPriorityLoader() {
		throw new RuntimeException("NOT READY");
	}
}
